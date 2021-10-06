package com.project.sroa.service;

import com.project.sroa.dto.ScheduleHandling;
import com.project.sroa.model.Schedule;
import com.project.sroa.repository.EngineerInfoRepository;
import com.project.sroa.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class RepairServiceImpl implements RepairService {
    ScheduleRepository scheduleRepository;
    EngineerInfoRepository engineerInfoRepository;

    @Autowired
    public RepairServiceImpl(ScheduleRepository scheduleRepository,
                             EngineerInfoRepository engineerInfoRepository){
        this.scheduleRepository=scheduleRepository;
        this.engineerInfoRepository=engineerInfoRepository;
    }

    @Override
    public void updateState(ScheduleHandling form) {
        Schedule schedule = scheduleRepository.findByScheduleNum(form.getScheduleNum());
        if(form.getStatus().equals(1) && schedule.getStatus().equals(2)){
            System.out.println("처리완료 처리 : 입고되어 수리가 완료되지 않음");
            return;
        }
        if(form.getStatus().equals(1)){
            scheduleRepository.updateEndDate(schedule.getScheduleNum(), Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            engineerInfoRepository.updateEngineerAmountOfWork(schedule.getEngineerInfo().getEngineerNum());
        }
        scheduleRepository.updateStatus(form.getScheduleNum(), form.getStatus());
    }
}
