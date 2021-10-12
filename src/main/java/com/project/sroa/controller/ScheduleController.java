package com.project.sroa.controller;

import com.project.sroa.dto.RequestBooking;
import com.project.sroa.model.EngineerInfo;
import com.project.sroa.model.Product;
import com.project.sroa.model.ServiceCenter;
import com.project.sroa.repository.UserInfoRepository;
import com.project.sroa.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
public class ScheduleController {
    // status : 0-> 예약완료 , 1 -> 처리 완료, 2 -> 수령, 3 -> 수리 완료(반납 전) 4 -> 평가 완료
    String apiKey = "553DD31F-7E58-3853-8B42-951509B85AAF";
    ScheduleService scheduleService;
    UserInfoRepository userInfoRepository;

    @Autowired
    public ScheduleController(ScheduleService scheduleService,
                              UserInfoRepository userInfoRepository) {
        this.scheduleService = scheduleService;
        this.userInfoRepository = userInfoRepository;
    }

    // 고객 날짜 선택시 예약 가능 현황 조회
    //[ 09:00 ,10:30, 12:30, 14:00, 15:30, 17:00]
    @GetMapping("/schedule/findAvailableTime/{date}/{address}")
    public List<Boolean> findAvailableTime(@PathVariable("date") String date,
                                           @PathVariable("address") String address) {
        //고객 주소와 가까운 서비스 센터 찾기
        Map<String, Object> closeCenter = scheduleService.searchNearCenter(address);

        return scheduleService.searchAvailableTime(date, closeCenter);
    }

    @PostMapping("/schedule/allocateEngineer")
//    public Boolean allocateEngineer(@RequestBody RequestBooking body){
    public EngineerInfo allocateEngineer(@RequestBody RequestBooking form) {
        //고객 주소와 가까운 서비스 센터와 거리 찾기
        Map<String, Object> closeCenter = scheduleService.searchNearCenter(form.getAddress());


        // 고객이 기입한 날짜 +  시간에 일정이 없는 엔지니어 조회
        Map<String, Object> noScheduleEngineers = scheduleService.noScheduleEngineerAtTime(form.getDateTime(), (ServiceCenter) closeCenter.get("center"));
        List<EngineerInfo> engineers = (List<EngineerInfo>) noScheduleEngineers.get(form.getDateTime());


        List<Long> sortEngineerNumList = scheduleService.findInfoForOptimum(engineers,
                (ScheduleService.Coordinates) closeCenter.get("centerCoor"), form.getDateTime(),
                (ScheduleService.Coordinates) closeCenter.get("customerCoor"));

        // 선별된 엔지니어가 여러명일수 있기때문에 작업량으로 최종 선별
        EngineerInfo engineerInfo =scheduleService.findSmallestWorkEngineerAmongOptimum(sortEngineerNumList);
        //해당 엔지니어에 일정 부여
        Product product = scheduleService.storeProductForReserve(form.getClassifyName(), form.getContent());
        scheduleService.allocateSchedule(engineerInfo, product, form.getDateTime(), form.getUserId(), form.getCustomerName(), form.getPhoneNum(), form.getAddress());
        return engineerInfo;
    }

}
