package com.project.sroa.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long sheduleNum;
    private Date startDate;
    private Date endDate;
    private String problem;
    private Integer status;

    @OneToOne
    @JoinColumn(name="productNum")
    private Product product;

    @ManyToOne
    @JoinColumn(name="userNum")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name="engineerNum")
    private EngineerInfo engineerInfo;
}
