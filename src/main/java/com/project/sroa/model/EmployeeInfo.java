package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class EmployeeInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long empNum;
    public String name;
}
