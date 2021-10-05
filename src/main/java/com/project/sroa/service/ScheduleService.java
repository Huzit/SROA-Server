package com.project.sroa.service;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


public interface ScheduleService {
    List<Boolean> searchAvailableTime(Date date, String address);
}
