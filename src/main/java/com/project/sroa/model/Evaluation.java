package com.project.sroa.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long evaluationNum;
    private Date writeDate;
    private String content;
    private long score;

    @OneToOne
    @JoinColumn(name="scheduleNum")
    private Schedule schedule;
}
