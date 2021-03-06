package com.project.sroa.service;

import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Product;
import com.project.sroa.model.ServiceCenter;

import java.util.List;
import java.util.Map;


public interface ScheduleService {
    public class Coordinates {
        Double lon; //경도
        Double lat; //위도

        Coordinates(Double x, Double y) {
            this.lon = x;
            this.lat = y;
        }
    }

    Map<String, Object> searchNearCenter(String address);
    List<Boolean> searchAvailableTime(String date, Map<String, Object> closeCenter);

    Map<String, Object> noScheduleEngineerAtTime(String date, ServiceCenter serviceCenter);
    List<Long> findInfoForOptimum(List<EngineerInfo> engineers, Coordinates distBetweenCustomerAndCenter, String dateTime, Coordinates customerCoor);
    EngineerInfo findSmallestWorkEngineerAmongOptimum(List<Long> sortEngineerNumList);
    Product storeProductForReserve(String name, String content);
    void allocateSchedule(EngineerInfo engineerInfo, Product product, String dateTime, Long userId, String customerName, String phoneNum, String address);
}
