package com.project.sroa.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long sheduleNum;
    public Date startDate;
    public Date endDate;
    public String problem;
    public Integer status;

    @OneToOne
    @JoinColumn(name="productNum")
    Product product;

    @ManyToOne
    @JoinColumn(name="userNum")
    UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name="engineerNum")
    EngineerInfo engineerInfo;
}
