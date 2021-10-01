package com.project.sroa.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long evaluationNum;
    public Date writeDate;
    public String content;
    public long score;

    @OneToOne
    @JoinColumn(name="scheduleNum")
    Schedule schedule;
}
