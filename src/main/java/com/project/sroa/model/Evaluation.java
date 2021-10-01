package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Evaluation {
    @Id
    public long evaluationNum;
    public String content;
    public long score;
    public long userNum;
    public long scheduleNum;
    public long engineerNum;
}
