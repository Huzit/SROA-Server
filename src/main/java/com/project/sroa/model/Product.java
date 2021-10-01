package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public long serialNum;
    public String classifyCode;
    public String modelName;
    public long engineerNum;
}
