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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl implements ScheduleService {
    String[] times = {"09:00", "10:30", "12:30", "14:00", "15:30", "17:00"};
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";

    ServiceCenterRepository serviceCenterRepository;
    EngineerInfoRepository engineerInfoRepository;
    ScheduleRepository scheduleRepository;

    public class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }

    @Autowired
    public ScheduleServiceImpl(ServiceCenterRepository serviceCenterRepository,
                               EngineerInfoRepository engineerInfoRepository,
                               ScheduleRepository scheduleRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
        this.engineerInfoRepository = engineerInfoRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Boolean> searchAvailableTime(String date, String address) {
        // 가까운 서비스 센텀 찾기
        Map<String, Object> closeCenter = searchNearCenter(address);
        ServiceCenter serviceCenter = serviceCenterRepository.findByCenterNum((Long) closeCenter.get("centerNum"));
        Integer distance = Math.toIntExact(Math.round((Double) closeCenter.get("distance")));

        System.out.println("가까운 서비스 센터명 : " + serviceCenter.getCenterName());
        System.out.println("가까운 서비스 센터주소 : " + serviceCenter.getAddress());
        System.out.println("거리 : " + distance + " meter");

        Integer totalEngineers = engineerInfoRepository.findByServiceCenter(serviceCenter).size();

        // 해당 센터의 엔지니어의 일정 조회
        List<Boolean> res=new ArrayList<>();
        Map<String, Object> availableEngineers = searchAvailableEngineer(date, serviceCenter);
        for(String time:times){
            Integer possibleEngineersAtTimeCnt = ((List<EngineerInfo>) availableEngineers.get(time)).size();
            if(totalEngineers-possibleEngineersAtTimeCnt<=0){
                res.add(false);
            }
            else{
                res.add(true);
            }
            System.out.println(date+" "+time+"에 예약가능한 엔지니어 수 : "+ possibleEngineersAtTimeCnt);
        }
        return res;
    }

    // 날짜 선택시 가능한 엔지니어, 날짜 + 시간 선택시 엔지니어 할당을 위한 가능 엔지니어
    // return map 시간대 - 가능 엔지니어 리스트
    private Map<String, Object> searchAvailableEngineer(String date, ServiceCenter serviceCenter) {
        Map<String, Object> map = new HashMap<>();

        String[] searchTimes;
        if(date.length()>=10){
            searchTimes = times;
        }
        else{
            searchTimes= new String[]{date};
        }

        System.out.println("담당 서비스 센터 번호 : "+serviceCenter.getCenterNum());
        for (String time : times) {
            String dateTime = date + " " + time;
            List<EngineerInfo> list=engineerInfoRepository.findAllPossibleEngineerByDate(serviceCenter.getCenterNum(), dateTime);
            map.put(time, list);
        }
        return map;
    }


    private Map<String, Object> searchNearCenter(String customerAddress) {
        String rootAddress = customerAddress.split(" ")[0];
        List<ServiceCenter> serviceCenters = serviceCenterRepository.findByAddressContaining(rootAddress);

        Coordinates customerCoordinates = findCoordinates(customerAddress);

        double min = 100000.0;
        int min_idx = 0, idx = 0;
        for (ServiceCenter s : serviceCenters) {
            assert customerCoordinates != null;

            System.out.println(s.getCenterName() + "의 위도, 경도 = " + "(" + s.getLatitude() + ", " + s.getLongitude() + ")");
            if (s.getLongitude() == null || s.getLatitude() == null) {
                System.out.println("현재 서비스 센터의 위도, 경도 정보가 없어 갱신");
                Coordinates coordinates = findCoordinates(s.getAddress());
                s.setLatitude(coordinates.lat);
                s.setLongitude(coordinates.lon);
                serviceCenterRepository.updatePos(s.getLatitude(), s.getLongitude(), s.getCenterNum());
            }

            Double now = harverSine(customerCoordinates, new Coordinates(s.getLongitude(), s.getLatitude()));
            System.out.println("고객 정보와 " + s.getCenterName() + "의 거리 완료");
            if (now < min) {
                min = now;
                min_idx = idx;
            }
            idx += 1;
        }
        System.out.println("같은지역의 서비스 센터 및 최소 거리 탐색 완료");
        Map<String, Object> map = new HashMap<>();
        map.put("centerNum", serviceCenters.get(min_idx).getCenterNum());
        map.put("distance", min);
        return map;
    }

    // 직선거리 미터 반환
    private Double harverSine(Coordinates coordinates1, Coordinates coordinates2) {
        double dist;
        double radius = 6371;//지구 반지름
        double toRadian = Math.PI / 180; // 라디안 변환을 위해

        double deltaLat = Math.abs(coordinates1.lat - coordinates2.lat) * toRadian;
        double deltaLon = Math.abs(coordinates1.lon - coordinates2.lon) * toRadian;

        double sinDeltaLat = Math.sin(deltaLat / 2);
        double sinDeltaLon = Math.sin(deltaLon / 2);

        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                        Math.cos(coordinates1.lat) * Math.cos(coordinates2.lat) * sinDeltaLat * sinDeltaLon);

        dist = 2 * radius * Math.asin(squareRoot);
        return dist * 1000;
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
