package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EmployeeInfo {
    @Id
    public long empNum;
    public String name;
}
