package com.project.sroa.controller;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
public class MapController {
    MapService mapService;

    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/map/{dateTime}/{serviceCenter}")
    public Map<String, Object> drawMap(@PathVariable("dateTime") String dateTime, @PathVariable("serviceCenter") Long centerNum) {
        List<EngineerInfo> list = mapService.searchEngineerAtCenter(centerNum);
        System.out.println("센터내 엔지니어 수 : "+ list.size());
        Map<String, Object> engineerMap = mapService.findScheduleAtTime(list, dateTime);


        return engineerMap;
    }


}
