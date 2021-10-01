package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long sheduleNum;
    public Date startDate;
    public Date endDate;
    public long status;
    public long serialNum;
    public long userNum;
    public long engineerNum;
}
