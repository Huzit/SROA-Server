package com.project.sroa.controller;

import com.project.sroa.dto.RequestBooking;
import com.project.sroa.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@ResponseBody
public class ScheduleController {
    // status : 0-> 예약완료 , 1 -> 처리 완료, 2 -> 수령, 3 -> 수리 완료(반납 전) 4 -> 평가 완료

    ScheduleService scheduleService;
    @Autowired
    public ScheduleController(ScheduleService scheduleService){
        this.scheduleService=scheduleService;
    }
    // 고객 날짜 선택시 예약 가능 현황 조회
    @GetMapping("/schedule/findAvailableTime/{date}/{address}")
    public List<Boolean> findAvailableTime(@PathVariable("date") Date date, @PathVariable("address") String address){
        return scheduleService.searchAvailableTime(date, address);
    }
}
