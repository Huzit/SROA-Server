package com.project.sroa.service;

import com.project.sroa.model.Schedule;


public interface RepairService {
    boolean updateState(Schedule schedule, Integer state);

    Schedule searchSchedule(Long scheduleNum);
}
