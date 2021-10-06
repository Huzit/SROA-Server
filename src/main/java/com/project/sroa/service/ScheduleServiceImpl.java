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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl implements ScheduleService {
    class Coordinates {
        Double x;
        Double y;

        Coordinates(Double x, Double y) {
            this.x= x;
            this.y= y;
        }
    }

    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    ServiceCenterRepository serviceCenterRepository;

    @Autowired
    public ScheduleServiceImpl(ServiceCenterRepository serviceCenterRepository) {
        this.serviceCenterRepository = serviceCenterRepository;
    }


    @Override
    public List<Boolean> searchAvailableTime(LocalDateTime date, String address) {

        ServiceCenter serviceCenter = searchNearCenter(address);
        return null;
    }

    private ServiceCenter searchNearCenter(String customerAddress) {
        String rootAddress = customerAddress.split(" ")[0];
        List<ServiceCenter> list = serviceCenterRepository.findByAddressContaining(rootAddress);

        List<Double> distanceList = new ArrayList<>();
        for (ServiceCenter s : list) {
//            distanceList.add(calculateDistance(customerAddress, s.getAddress()));
            calculateDistance(customerAddress, s.getAddress());
        }
        return null;
    }

    private Double calculateDistance(String customerAddress, String address) {
        Coordinates customerCoordinates = findCoordinates(customerAddress);
        Coordinates serviceCoordinates = findCoordinates(address);

        return null;
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
            postParams +="&version=2.0";
            postParams +="&crs=epsg:4326";
            postParams +="&address="+text_content;
            postParams +="&refine=true";
            postParams +="&simple=false";
            postParams +="&format=json";
            postParams +="&type=ROAD";
            postParams +="&key="+apiKey;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode ==200){// 정상호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            else{// 에러발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while((inputLine=br.readLine())!=null){
                response.append(inputLine);
            }
            br.close();
            con.disconnect();
            Map<String, Object> map = jsonParser.parseMap(response.toString());
            Map<String, Object> point= (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) map.get("response")).get("result")).get("point");
            System.out.println("test");
            return new Coordinates(Double.parseDouble((String) point.get("x")),Double.parseDouble((String) point.get("y")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
