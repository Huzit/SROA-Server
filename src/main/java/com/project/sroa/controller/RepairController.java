package com.project.sroa.controller;

import com.project.sroa.dto.ScheduleHandling;
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
    public RepairController(RepairService repairService){
        this.repairService=repairService;
    }

    // 입고시 schedule이 변경된다는 가정
    // status : 처리완료 -> 1 입고 -> 2
    @PostMapping("repair/engineer/handlingRequest")
    public void handlingRequest(@RequestBody ScheduleHandling form){
        repairService.updateState(form);
    }
}
