package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.ScheduleRepository;
import com.project.sroa.repository.ServiceCenterRepository;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapServiceImpl implements MapService {
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    ScheduleRepository scheduleRepository;
    EngineerInfoRepository engineerInfoRepository;
    ServiceCenterRepository serviceCenterRepository;

    public MapServiceImpl(EngineerInfoRepository engineerInfoRepository,
                          ScheduleRepository scheduleRepository,
                          ServiceCenterRepository serviceCenterRepository) {
        this.engineerInfoRepository = engineerInfoRepository;
        this.scheduleRepository = scheduleRepository;
        this.serviceCenterRepository = serviceCenterRepository;
    }

    @Override
    public List<EngineerInfo> searchEngineerAtCenter(Long centerNum) {
        return engineerInfoRepository.findAllByServiceCenter(serviceCenterRepository.findByCenterNum(centerNum));
    }

    // 엔지니어 번호, 시간, 좌표표
    @Override
    public Map<String, Object> findScheduleAtTime(List<EngineerInfo> list, String dateTime) {
        Map<String, Object> map = new HashMap<>();
        List<String> timeList=new ArrayList<>();
        List<Double> yList=new ArrayList<>();
        List<Double> xList=new ArrayList<>();

        for (EngineerInfo engineer : list) {
            System.out.println(engineer.getEngineerNum() + "번 엔지니어");
            List<Schedule> schedules = scheduleRepository.findAllByEngineerInfoAndDateTime(engineer.getEngineerNum(), dateTime);
            System.out.println("당일 일정 수 : " + schedules.size());
            if(schedules.size()==0)continue;


            for (Schedule schedule : schedules) {
                timeList.add(engineer.getEngineerNum().toString()+"번 엔지니어 " + schedule.getStartDate().toString());

                Coordinates coordinates = findCoordinates(schedule.getAddress());
                yList.add(coordinates.lat);
                xList.add(coordinates.lon);
            }
        }
        map.put("y", yList);
        map.put("x",xList);
        map.put("test", timeList);

        return map;
    }

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
