package com.project.sroa.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long scheduleNum;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer status;
    private String address;
    private String name;
    private String phoneNum;

    @OneToOne
    @JoinColumn(name = "productNum")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "userNum")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "engineerNum")
    private EngineerInfo engineerInfo;
}
