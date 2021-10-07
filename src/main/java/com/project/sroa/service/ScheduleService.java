package com.project.sroa.service;

import java.time.LocalDateTime;
import java.util.List;


public interface ScheduleService {
    List<Boolean> searchAvailableTime(LocalDateTime date, String address);
}
