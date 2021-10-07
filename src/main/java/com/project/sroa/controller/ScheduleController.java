package com.project.sroa.controller;

import com.project.sroa.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@ResponseBody
public class ScheduleController {
    // status : 0-> 예약완료 , 1 -> 처리 완료, 2 -> 수령, 3 -> 수리 완료(반납 전) 4 -> 평가 완료
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 고객 날짜 선택시 예약 가능 현황 조회
    //[ 09:00 ,10:30, 12:30, 14:00, 15:30, 17:00]
    @GetMapping("/schedule/findAvailableTime/{date}/{address}")
    public List<Boolean> findAvailableTime(@PathVariable("date") String date,
                                           @PathVariable("address") String address) {
        return scheduleService.searchAvailableTime(date, address);
    }

}
