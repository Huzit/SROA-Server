package com.project.sroa.dto;

import lombok.Getter;

@Getter
public class RequestBooking {
    private Long userNum;
    private String customerName;
    private String classifyName;
    private String address;
    private String dateTime;
    private String phoneNum;
    private String content;
}
