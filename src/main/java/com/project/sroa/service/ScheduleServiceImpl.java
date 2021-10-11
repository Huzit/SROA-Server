package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import com.project.sroa.model.ServiceCenter;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.ScheduleRepository;
import com.project.sroa.repository.ServiceCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl implements ScheduleService {
    Integer MAX = 987654321;
    String[] times = {"09:00", "10:30", "12:30", "14:00", "15:30", "17:00"};
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";

    class sortElem {
        Long num;
        Integer dist;
        Integer dirDiff;

        sortElem(Long num, Integer dist, Integer dirDiff) {
            this.num = num;
            this.dist = dist;
            this.dirDiff = dirDiff;
        }
    }

    ServiceCenterRepository serviceCenterRepository;
    EngineerInfoRepository engineerInfoRepository;
    ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ServiceCenterRepository serviceCenterRepository,
                               EngineerInfoRepository engineerInfoRepository,
                               ScheduleRepository scheduleRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
        this.engineerInfoRepository = engineerInfoRepository;
        this.scheduleRepository = scheduleRepository;
    }

    //날짜와 가장 가까운 서비스 센터가 주어졌을때 시간대 마다 가능한 엔지니어가 있는지를 조회
    @Override
    public List<Boolean> searchAvailableTime(String date, Map<String, Object> closeCenter) {
        // 가까운 서비스 센텀 찾기
        ServiceCenter serviceCenter = (ServiceCenter) closeCenter.get("center");
        Integer distance = (Integer) closeCenter.get("distance");

        Integer totalEngineers = engineerInfoRepository.findByServiceCenter(serviceCenter).size();

        // 해당 센터의 엔지니어의 일정 조회
        List<Boolean> res = new ArrayList<>();
        Map<String, Object> availableEngineers = noScheduleEngineerAtTime(date, serviceCenter);

        for (String time : times) {
            time = date + " " + time;
            Integer possibleEngineersAtTimeCnt = ((List<EngineerInfo>) availableEngineers.get(time)).size();

            if (possibleEngineersAtTimeCnt > 0) {
                res.add(true);
            } else {
                res.add(false);
            }
            System.out.println(date + " " + time + "에 예약가능한 엔지니어 수 : " + possibleEngineersAtTimeCnt);
        }
        System.out.println("사용 가능 시간 대");
        for(int i =0;i<res.size();i++){
            if(res.get(i)==true) System.out.println(times[i]);
        }
        System.out.println("=================================================");
        return res;
    }

    // 날짜 선택시 가능한 엔지니어, 날짜 + 시간 선택시 엔지니어 할당을 위한 가능 엔지니어
    // return map 시간대 - 가능 엔지니어 리스트
    @Override
    public Map<String, Object> noScheduleEngineerAtTime(String date, ServiceCenter serviceCenter) {
        Map<String, Object> map = new HashMap<>();

        // 변수로 날짜만 들어오면 날짜 + 6개의 시간으로 탐색
        // 변수로 날짜 + 시간이 들어오면 해당 날짜 + 시간에 대해 탐색
        String[] searchTimes;
        if (date.length() < 11) {
            searchTimes = new String[6];
            for (int i = 0; i < searchTimes.length; i++) {
                searchTimes[i] = date + " " + times[i];
                System.out.println(searchTimes[i]);
            }
        } else {
            searchTimes = new String[]{date};
        }
        System.out.println("=================================================");
        System.out.println("담당 서비스 센터 번호 : " + serviceCenter.getCenterNum());

        // 시간별 탐색 가능한 엔지니어 리스트
        for (String time : searchTimes) {
            String dateTime = time;

            List<EngineerInfo> list = engineerInfoRepository.findAllPossibleEngineerByDate(serviceCenter.getCenterNum(), dateTime);
            System.out.println("탐색 시간대 " + dateTime+"에 가능한 엔지니어 수 : "+list.size());
            map.put(time, list);
        }
        System.out.println("=================================================");
        return map;
    }

    // 고객 주소를 이용하여 가장 가까운 서비스 센터와 거리를 측정
    @Override
    public Map<String, Object> searchNearCenter(String customerAddress) {
        String rootAddress = customerAddress.split(" ")[0];
        List<ServiceCenter> serviceCenters = serviceCenterRepository.findByAddressContaining(rootAddress);

        Coordinates customerCoordinates = findCoordinates(customerAddress);

        Integer min = MAX;
        int min_idx = 0, idx = 0;
        for (ServiceCenter s : serviceCenters) {
            assert customerCoordinates != null;


            if (s.getLongitude() == null || s.getLatitude() == null) {
                System.out.println("현재 서비스 센터의 위도, 경도 정보가 없어 갱신");
                Coordinates coordinates = findCoordinates(s.getAddress());
                s.setLatitude(coordinates.lat);
                s.setLongitude(coordinates.lon);
                serviceCenterRepository.updatePos(s.getLatitude(), s.getLongitude(), s.getCenterNum());
            }

            Integer now = harverSine(customerCoordinates, new Coordinates(s.getLongitude(), s.getLatitude()));
            if (now < min) {
                min = now;
                min_idx = idx;
            }
            idx += 1;
        }
        System.out.println("=================================================");
        System.out.println("같은지역의 서비스 센터 및 최소 거리 탐색 완료");
        System.out.println("가까운 서비스 센터명 : " + serviceCenters.get(min_idx));
        System.out.println("가까운 서비스 센터주소 : " + serviceCenters.get(min_idx).getAddress());
        System.out.println("거리 : " + min + " meter");


        Map<String, Object> map = new HashMap<>();
        map.put("center", serviceCenters.get(min_idx));
        map.put("centerCoor", findCoordinates(serviceCenters.get(min_idx).getAddress()));
        map.put("distance", min);
        map.put("customerCoor", customerCoordinates);
        return map;
    }

    // 날짜 + 시간에 활동 가능한 엔지니어 중 최적의 엔지니어 탐색
    @Override
    public EngineerInfo findOptimumEngineer(List<EngineerInfo> engineers, Coordinates centerCoor, String dateTime, Coordinates customerCoor) {
        List<sortElem> decideList = new ArrayList<>();
        List<Long> sortEngineerNumList = new ArrayList<>();

        Integer distBetweenCustomerAndCenter_first = harverSine(centerCoor, customerCoor);
        Integer dist;
        Integer dirDiff;
        Coordinates beforeCoor;
        Coordinates afterCoor;

        // 활동가능한 엔지니어의 고객과의 거리 조회
        for (EngineerInfo engineer : engineers) {
            String _dateTime = dateTime.split(" ")[0];
            List<Schedule> timeOfSchedules = scheduleRepository.findAllScheduleTimeByEngineerNumAndDate(engineer.getEngineerNum(), _dateTime);
            int scheduleSize = timeOfSchedules.size();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
            System.out.println("현재 엔지니어 번호 : "+engineer.getEngineerNum());

            // 당일에 일정이 없으면 센터의 위치로 거리등록
            if (timeOfSchedules.size() == 0) {
                dist = distBetweenCustomerAndCenter_first;
                dirDiff = 360;
                decideList.add(new sortElem(engineer.getEngineerNum(), dist, dirDiff));
                System.out.println("당일 일정 없음");
            }
            // 이번에 할당될 일정이 마지막 일정보다 시간적으로 뒤
            else {
                String string = timeOfSchedules.get(scheduleSize - 1).getStartDate().format(formatter);
                if (dateTime.compareTo(string) > 0) {
                    System.out.println("현재 할당되는 일정이 마지막 일정");
                    afterCoor = findCoordinates(timeOfSchedules.get(scheduleSize - 1).getAddress());
                    dist = harverSine(customerCoor, afterCoor);

                    // 일정이 한개라면 방향성 : 센터 - 원래 일정 - 현재 할당 일정
                    if (timeOfSchedules.size() == 1) {
                        beforeCoor = centerCoor;
                    } else {
                        beforeCoor = findCoordinates(timeOfSchedules.get(scheduleSize - 2).getAddress());
                    }
                    dirDiff = calcDirDiff(beforeCoor, afterCoor, customerCoor);

                    decideList.add(new sortElem(engineer.getEngineerNum(), dist, dirDiff));
                }
                // 현재 할당될 일정이 일정 사이 or 다른 일정들 보다 일찍 시작되는 일정정
                else {
                    Integer beforeScheduleDist;
                    Integer afterScheduleDist;
                    int idx = 0;

                    // 할당 될 일정이 다른 일정보다 빠른지 판단 요소
                    for (Schedule schedule : timeOfSchedules) {
                        string = schedule.getStartDate().format(formatter);
                        if (dateTime.compareTo(string) < 0) {
                            idx -= 1;
                            break;
                        }
                        idx++;
                    }
                    // 다른 일정들보다 시간이 빠름
                    if (idx == -1) {
                        //전 - 센터, 중간 - 이번 일정, 후 - 이번에 배정되는 일정 뒤의 일정(idx+1)
                        beforeScheduleDist = harverSine(centerCoor, customerCoor);

                        afterCoor = findCoordinates(timeOfSchedules.get(idx + 1).getAddress());
                        afterScheduleDist = harverSine(customerCoor, afterCoor);

                        dist = Math.round((beforeScheduleDist + afterScheduleDist) / 2);
                        dirDiff = calcDirDiff(centerCoor, customerCoor, afterCoor);
                        decideList.add(new sortElem(engineer.getEngineerNum(), dist, dirDiff));
                    } else {
                        // 전 -  이번에 배정되는 일정 전의 일정(idx) 중간 - 이번 일정, 후 - 이번에 배정되는 일정 뒤의 일정(idx+1)
                        beforeCoor = findCoordinates(timeOfSchedules.get(idx).getAddress());
                        beforeScheduleDist = harverSine(beforeCoor, customerCoor);

                        afterCoor = findCoordinates(timeOfSchedules.get(idx + 1).getAddress());
                        afterScheduleDist = harverSine(customerCoor, afterCoor);

                        dist = Math.round((beforeScheduleDist + afterScheduleDist) / 2);
                        dirDiff = calcDirDiff(beforeCoor, customerCoor, afterCoor);
                        decideList.add(new sortElem(engineer.getEngineerNum(), dist, dirDiff));
                    }
                }
            }

            System.out.println("고객과의 평가 거리 : "+ dist);
        }
        System.out.println("---------------------------------------");
        // 엔지너어들의 거리, 방향성을 이용하여 1차 선별
        sortEngineerNumList = findOptimunEngineerGroup(decideList);
        // 선별된 엔지니어가 여러명일수 있기때문에 작업량으로 최종 선별
        return findSmallestWorkEngineer(sortEngineerNumList);
    }

    // findOptimunEngineerGroup의 함수의 결과가 여러명일 수 있기 때문에 작업량이 적은 엔지니어를 선별
    private EngineerInfo findSmallestWorkEngineer(List<Long> sortEngineerNumList) {
        System.out.println("최종 선별 후 엔지니어 수 : "+ sortEngineerNumList.size());
        EngineerInfo res = null;

        // 결과 엔지니어가 여러명이라면 작업량으로 선별
        if (sortEngineerNumList.size() > 1) {
            System.out.println("최종 선별된 엔지니어가 여러명 -> " + sortEngineerNumList.size());
            int maxIdx = 0;
            int max = engineerInfoRepository.findWorkByNum(sortEngineerNumList.get(0));
            for (int i = 1; i < sortEngineerNumList.size(); i++) {
                int temp = engineerInfoRepository.findWorkByNum(sortEngineerNumList.get(i));
                if (max < temp) {
                    max = temp;
                    maxIdx = i;
                }
            }
            res = engineerInfoRepository.findByEngineerNum(sortEngineerNumList.get(maxIdx));
        }
        // 결과 1명 -> 엔지니어 배정
        else {
            res = engineerInfoRepository.findByEngineerNum(sortEngineerNumList.get(0));
        }
        return res;
    }

    // 엔지니어들의 거리, 방향성을 이용하여 최적 엔지니어 그룹을 판별
    // 표준 편차에서 벗어난(유독 거리가 짧은) 엔지니어가 있다면 그 중 거리가 짧은 엔지니어
    // 벗어난 엔지니어들이 없다면 엔지니어마다 거리가 비슷하다는 의미로 방향성 차이가 가장 적은 엔지니어
    private List<Long> findOptimunEngineerGroup(List<sortElem> decideList) {
        List<Long> sortEngineerNumList = new ArrayList<>();
        System.out.println("현재 그룹내 엔지니어 수 " + decideList.size() );
        Integer distMean = calcMean(decideList);
        System.out.println("엔지니어와 고객 거리의 평균 : " + distMean);
        double distDev = calcDev(decideList);
        System.out.println("엔지니어와 고객 거리의 표준편차 : " + distDev);
        System.out.println("-------------------------------------------");

        // 비교적 거리가 먼 후보 제외
        for (int i = 0; i < decideList.size(); i++) {
            if (decideList.get(i).dist >= distMean + distDev) {
                System.out.println("고객과 거리가 너무 멀어 배제되는 엔지니어 : " + decideList.get(i).num + ", 거리 : " +  decideList.get(i).dist);
                decideList.remove(i);
            }
        }

        System.out.println("거리상 배제후 남은 엔지니어의 수: " + decideList.size() );
        List<sortElem> sortList = new ArrayList<>();
        Integer minDistIdx = 0;
        Integer minDirDiffIdx = 0;
        for (int i = 0; i < decideList.size(); i++) {
            if (decideList.get(i).dist <= distMean - distDev) {
                System.out.println("거리가 너무 가까워 선별 가능성이 높은 엔지니어 : "+ decideList.get(i).num+", 거리 : "+ decideList.get(i).dist);
                sortList.add(decideList.get(i));
            }
        }
        System.out.println("-------------------------------------------");
        // 현재 그룹내에서 거리와 방향성 차이가 최소인 엔지니어 선별
        for(int i=0;i<sortList.size();i++) {
            if (decideList.get(minDirDiffIdx).dirDiff > decideList.get(i).dirDiff)
                minDirDiffIdx = i;
            if (decideList.get(minDistIdx).dist > decideList.get(i).dist)
                minDistIdx = i;
        }

        // 거리 유독 가까운 엔지니어 중에서 선발
        if (sortList.size() > 0) {
            System.out.println("거리가 너무 가까운 엔지니어 존재 ");
            for (int i = 0; i < sortList.size(); i++) {
                if (sortList.get(minDistIdx).dist == sortList.get(i).dist) {
                    sortEngineerNumList.add(sortList.get(i).num);
                }
            }
        }
        //거리가 비슷하여 방향성으로 선별
        // 방향성 차이가 가장 적은 엔지니어 선별
        else {
            System.out.println("거리가 비슷하여 방향성 차이로 선별");
            for (int i = 0; i < decideList.size(); i++) {
                if (decideList.get(minDirDiffIdx).dirDiff == decideList.get(i).dirDiff)
                    sortEngineerNumList.add(decideList.get(i).num);
            }
        }


        return sortEngineerNumList;
    }

    // 평균구하기
    private Integer calcMean(List<sortElem> decideList) {
        Integer sum = 0;
        for (int i = 0; i < decideList.size(); i++) {
            sum += decideList.get(i).dist;
        }
        return (int) sum / decideList.size();
    }

    // 표준편차 구하기
    private Double calcDev(List<sortElem> decideList) {
        double sum = 0.0;
        double sd = 0.0;
        double diff;
        double meanValue = calcMean(decideList);
        for (int i = 0; i < decideList.size(); i++) {
            diff = decideList.get(i).dist - meanValue;
            sum += diff * diff;
        }
        return Math.sqrt(sum / decideList.size());
    }

    // 방위각 차이 구하기
    Integer calcDirDiff(Coordinates before, Coordinates now, Coordinates after) {
        Integer dir1 = calcDir(before, now);
        Integer dir2 = calcDir(now, after);
        Integer diff1 = Math.abs(dir1 - dir2);
        return Math.min(diff1, 360 - diff1);
    }

    // 좌표간 방향성(방위각) 구구하기
   private Integer calcDir(Coordinates a, Coordinates b) {
        double lat1_rad = convertDegreesToRadians(a.lat);
        double lat2_rad = convertDegreesToRadians(b.lat);
        double lon_diff_rad = convertDegreesToRadians(a.lon - b.lon);
        double y = Math.sin(lon_diff_rad) * Math.cos(lat2_rad);
        double x = Math.cos(lat1_rad) * Math.sin(lat2_rad) - Math.sin(lat1_rad) * Math.cos(lat2_rad) * Math.cos(lon_diff_rad);

        return ((int) convertRadiansToDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    // 직선거리 미터 반환
    private Integer harverSine(Coordinates coordinates1, Coordinates coordinates2) {
        double dist;
        double radius = 6371;//지구 반지름
        double toRadian = Math.PI / 180; // 라디안 변환을 위해

        double deltaLat = convertDegreesToRadians(Math.abs(coordinates1.lat - coordinates2.lat));
        double deltaLon = convertDegreesToRadians(Math.abs(coordinates1.lon - coordinates2.lon));

        double sinDeltaLat = Math.sin(deltaLat / 2);
        double sinDeltaLon = Math.sin(deltaLon / 2);

        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(coordinates1.lat) * Math.cos(coordinates2.lat) * sinDeltaLat * sinDeltaLon);

        dist = 2 * radius * Math.asin(squareRoot);
        return Math.toIntExact(Math.round(dist * 1000));
    }

    private double convertDegreesToRadians(double deg) {
        return (deg * Math.PI / 180);
    }

    private double convertRadiansToDegrees(double rad) {
        return (rad * 180 / Math.PI);
    }

    // 지번 주소에 대해 좌표 계산
    private Coordinates findCoordinates(String customerAddress) {
        String apiURL = "http://api.vworld.kr/req/address";
        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            String text_content = URLEncoder.encode(customerAddress, StandardCharsets.UTF_8);

            String postParams = "service=address";
            postParams += "&request=getcoord";
            postParams += "&version=2.0";
            postParams += "&crs=epsg:4326";
            postParams += "&address=" + text_content;
            postParams += "&refine=true";
            postParams += "&simple=false";
            postParams += "&format=json";
            postParams += "&type=ROAD";
            postParams += "&key=" + apiKey;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) {// 정상호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {// 에러발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            con.disconnect();
            Map<String, Object> map = jsonParser.parseMap(response.toString());
            Map<String, Object> point = (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) map.get("response")).get("result")).get("point");

            return new Coordinates(Double.parseDouble((String) point.get("x")), Double.parseDouble((String) point.get("y")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
