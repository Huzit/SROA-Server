package com.project.sroa.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long scheduleNum;
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
