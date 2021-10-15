package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.ServiceCenter;

import java.util.List;

public interface MapService {
    ServiceCenter searchCenterPos(Long centerNum);

    public class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }
    List<EngineerInfo> searchEngineerAtCenter(Long centerNum);

    List<Object> findScheduleAtTime(List<EngineerInfo> list, String dateTime);
}
