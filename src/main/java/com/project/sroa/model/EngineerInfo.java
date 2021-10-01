package com.project.sroa.model;

import javax.persistence.*;

@Entity
public class EngineerInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long engineerNum;
    public long avgScore;
    public String workingArea;

    @OneToOne
    @JoinColumn(name="userNum")
    UserInfo userInfo;

    @OneToOne
    @JoinColumn(name="empNum")
    EmployeeInfo employeeInfo;
}
