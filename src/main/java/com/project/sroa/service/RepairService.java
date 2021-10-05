package com.project.sroa.service;

import com.project.sroa.dto.ScheduleHandling;
import org.springframework.stereotype.Service;

@Service
public interface RepairService {
    void updateState(ScheduleHandling form);
}
