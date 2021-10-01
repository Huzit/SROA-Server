package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {
    @Id
    public long serialNum;
    public String classifyCode;
    public String modelName;
    public long engineerNum;
}
