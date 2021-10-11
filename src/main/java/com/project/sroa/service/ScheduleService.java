package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.ServiceCenter;

import java.util.List;
import java.util.Map;


public interface ScheduleService {
    Map<String, Object> searchNearCenter(String address);

    EngineerInfo findOptimumEngineer(List<EngineerInfo> engineers, Coordinates distBetweenCustomerAndCenter, String dateTime, Coordinates customerCoor);

    public class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }

    Map<String, Object> noScheduleEngineerAtTime(String date, ServiceCenter serviceCenter);
    List<Boolean> searchAvailableTime(String date, Map<String, Object> closeCenter);
//    List<Boolean> searchAvailableTime(LocalDateTime date, String address);
}
