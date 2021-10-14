package com.project.sroa.service;

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
                             EngineerInfoRepository engineerInfoRepository) {
        this.scheduleRepository = scheduleRepository;
        this.engineerInfoRepository = engineerInfoRepository;
    }

    @Override
    public Schedule searchSchedule(Long scheduleNum) {
        return scheduleRepository.findByScheduleNum(scheduleNum);
    }

    @Override
    public boolean updateState(Schedule schedule, Integer state) {
        if (state == 1) {
            scheduleRepository.updateEndDate(schedule.getScheduleNum(), Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            engineerInfoRepository.updateEngineerAmountOfWork(schedule.getEngineerInfo().getEngineerNum());
        }
        scheduleRepository.updateStatus(schedule.getScheduleNum(), state);
        return true;
    }
}
