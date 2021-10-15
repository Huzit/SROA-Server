package com.project.sroa.controller;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.ServiceCenter;
import com.project.sroa.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@ResponseBody
public class MapController {
    MapService mapService;


    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/map/{dateTime}/{serviceCenter}")
    public String map(@PathVariable("dateTime") String dateTime, @PathVariable("serviceCenter") Long centerNum, Model model){
        ServiceCenter center = mapService.searchCenterPos(centerNum);
        model.addAttribute("lat",center.getLatitude());
        model.addAttribute("lon", center.getLongitude());
        model.addAttribute("dateTime", dateTime);
        model.addAttribute("centerNum", centerNum);
        return "map";
    }

    @PostMapping("/map")
    @ResponseBody
    public List<Object> drawMap(@RequestParam("dateTime") String dateTime, @RequestParam("serviceCenter") Long centerNum) {
        System.out.println("asd");
        List<EngineerInfo> list = mapService.searchEngineerAtCenter(centerNum);
        System.out.println("센터내 엔지니어 수 : "+ list.size());
        List<Object> engineerMap = mapService.findScheduleAtTime(list, dateTime);


        return engineerMap;
    }


}
