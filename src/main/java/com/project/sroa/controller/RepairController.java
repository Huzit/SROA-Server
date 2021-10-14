package com.project.sroa.controller;

import com.project.sroa.dto.ScheduleHandling;
import com.project.sroa.model.Schedule;
import com.project.sroa.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class RepairController {
    RepairService repairService;

    @Autowired
    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    // 입고시 schedule이 변경된다는 가정
    // status : 처리완료 -> 1 입고 -> 2
    @PostMapping("repair/engineer/requestForCompletion")
    public boolean requestForCompletion(@RequestBody ScheduleHandling form) {
        Schedule schedule = repairService.searchSchedule(form.getScheduleNum());

        if (!(form.getStatus().equals(1) && (schedule.getStatus().equals(2) || schedule.getStatus().equals(3)))) {
            System.out.println("처리완료 처리 : 아직 입고되어있는 상태 처리가 완료되지 않음");
            return false;
        }
        if (form.getStatus().equals(2) && !schedule.getStatus().equals(0)) {
            System.out.println("입고 처리 : 현재상태가 예약 완료가 아니면 처리 X");
            return false;
        }

        return repairService.updateState(schedule, form.getStatus());
    }
}
