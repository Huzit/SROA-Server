package com.project.sroa.dto;

import com.project.sroa.model.UserInfo;
import lombok.Getter;

@Getter
public class SignupEngineer {
    private long empNum;
    public String id;
    public String pw;
    public String name;
    public String address;
    public String phoneNum;
    private String workingArea;
}
