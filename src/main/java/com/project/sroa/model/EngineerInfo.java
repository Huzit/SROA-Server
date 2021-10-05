package com.project.sroa.model;

import com.project.sroa.dto.SignupEngineer;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class EngineerInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long engineerNum;
    private Integer avgScore;
    private String workingArea;

    @OneToOne
    @JoinColumn(name="userNum")
    private UserInfo userInfo;

    @OneToOne
    @JoinColumn(name="empNum")
    private EmployeeInfo employeeInfo;

    @Builder
    public EngineerInfo(UserInfo userInfo, EmployeeInfo employeeInfo, String workingArea){
        this.workingArea=workingArea;
        this.avgScore=0;
        this.userInfo=userInfo;
        this.employeeInfo=employeeInfo;

    }


}
