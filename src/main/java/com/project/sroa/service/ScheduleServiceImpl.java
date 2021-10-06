package com.project.sroa.service;

import com.project.sroa.model.ServiceCenter;
import com.project.sroa.repository.ServiceCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl implements ScheduleService {
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    ServiceCenterRepository serviceCenterRepository;

    class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }

    class Center {
        ServiceCenter serviceCenter;
        Integer distanceFromCustomer;

        Center(ServiceCenter serviceCenter, double distanceFromCustomer) {
            this.serviceCenter = serviceCenter;
            this.distanceFromCustomer = Math.toIntExact(Math.round(distanceFromCustomer));
        }
    }

    @Autowired
    public ScheduleServiceImpl(ServiceCenterRepository serviceCenterRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
    }

    @Override
    public List<Boolean> searchAvailableTime(LocalDateTime date, String address) {
        // 가까운 서비스 센텀 찾기
        Center center = searchNearCenter(address);

        ServiceCenter serviceCenter = center.serviceCenter;
        Integer distance = center.distanceFromCustomer;
        System.out.println("가까운 서비스 센터명 : " + serviceCenter.getCenterName());
        System.out.println("가까운 서비스 센터주소 : " + serviceCenter.getAddress());
        System.out.println("거리 : " + distance + " meter");
        // 해당 센터의 엔지니어 타타임 테이블
        return null;
    }

    private Center searchNearCenter(String customerAddress) {
        String rootAddress = customerAddress.split(" ")[0];
        List<ServiceCenter> list = serviceCenterRepository.findByAddressContaining(rootAddress);

        Coordinates customerCoordinates = findCoordinates(customerAddress);

        double min = 100000.0;
        int min_idx = 0, idx = 0;
        for (ServiceCenter s : list) {
            assert customerCoordinates != null;
            Double now = harverSine(customerCoordinates, Objects.requireNonNull(findCoordinates(s.getAddress())));

            if (now < min) {
                min = now;
                min_idx = idx;
            }
            idx += 1;
        }
        return new Center(list.get(min_idx), min);
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
