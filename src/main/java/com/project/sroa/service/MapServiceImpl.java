package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Schedule;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.ScheduleRepository;
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
public class MapServiceImpl implements MapService {
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    EngineerInfoRepository engineerInfoRepository;
    ScheduleRepository scheduleRepository;

    public  MapServiceImpl(EngineerInfoRepository engineerInfoRepository,
                           ScheduleRepository scheduleRepository){
        this.engineerInfoRepository=engineerInfoRepository;
        this.scheduleRepository=scheduleRepository;
    }

    @Override
    public List<EngineerInfo> searchEngineerAtCenter(Long centerNum) {
        return engineerInfoRepository.findAllByServiceCenterNum(centerNum);
    }

    // 엔지니어 번호, 시간, 좌표표
   @Override
    public Map<Long, Object> findScheduleAtTime(List<EngineerInfo> list, String dateTime) {
        Map<Long, Object> map = null;
        Map<String, Object> info=null;
        for(EngineerInfo engineer:list){
            List<Schedule> schedules = scheduleRepository.findAllByEngineerInfoAndDateTime(engineer.getEngineerNum(), dateTime);
            List<Coordinates> coordinateList= new ArrayList<>();
            List<LocalDateTime> timeList=new ArrayList<>();
            for(Schedule schedule:schedules){
                timeList.add(schedule.getStartDate());
                Coordinates coordinates=findCoordinates(schedule.getAddress());
                coordinateList.add(coordinates);
            }
            info.put("coor", coordinateList);
            info.put("time", timeList);
            map.put(engineer.getEngineerNum(), info);
        }
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
