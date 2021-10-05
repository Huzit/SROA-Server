package com.project.sroa.controller;

import com.project.sroa.dto.RequestBooking;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ScheduleController {
    // status : 0-> 예약완료 , 1 -> 처리 완료, 2 -> 수령, 3 -> 수리 완료(반납 전) 4 -> 평가 완료
    @PostMapping("schedule/customer/bookingReservation")
    public boolean bookingReservation(@RequestBody RequestBooking form){
        return true;
    }
}
