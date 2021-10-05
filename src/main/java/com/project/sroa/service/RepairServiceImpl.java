package com.project.sroa.service;

import com.project.sroa.dto.ScheduleHandling;
import com.project.sroa.model.Schedule;
import com.project.sroa.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepairServiceImpl implements RepairService {
    ScheduleRepository scheduleRepository;

    @Autowired
    public RepairServiceImpl(ScheduleRepository scheduleRepository){
        this.scheduleRepository=scheduleRepository;
    }

    @Override
    public void updateState(ScheduleHandling form) {
        Schedule schedule = scheduleRepository.findByScheduleNum(form.getScheduleNum());
        if(form.getStatus().equals(1) && schedule.getStatus().equals(2)){
            System.out.println("처리완료 처리 : 입고되어 수리가 완료되지 않음");
            return;
        }
        scheduleRepository.updateStatus(form.getScheduleNum(), form.getStatus());
    }
}
