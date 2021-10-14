package com.project.sroa.controller;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class MapController {
    MapService mapService;

    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/map/{dateTime}/{serviceCenter}")
    public Map<Long, Object> drawMap(@PathVariable("dateTime") String dateTime, @PathVariable("serviceCenter") Long centerNum) {
        List<EngineerInfo> list = mapService.searchEngineerAtCenter(centerNum);
        Map<Long, Object> map = mapService.findScheduleAtTime(list, dateTime);
        Map<String, Object> infoMap = null;
//        for (int i = 0; i < list.size(); i++) {
//            Long engineerNum = list.get(i).getEngineerNum();
//            infoMap= (Map<String, Object>) map.get(engineerNum);
////            List<LocalDateTime> timeList= (List<LocalDateTime>) map.get("time");
////            List<MapService.Coordinates> coorList= (List<MapService.Coordinates>) map.get("coor");
//
//        }

        return map;
    }


}
