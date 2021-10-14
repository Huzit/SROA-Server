package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;

import java.util.List;
import java.util.Map;

public interface MapService {
    public class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }
    List<EngineerInfo> searchEngineerAtCenter(Long centerNum);

    Map<Long, Object> findScheduleAtTime(List<EngineerInfo> list, String dateTime);
}
