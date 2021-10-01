package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EngineerInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long engineerNum;
    public long avgScore;
    public String workingArea;
    public  long userNum;
    public long empNum;
}
