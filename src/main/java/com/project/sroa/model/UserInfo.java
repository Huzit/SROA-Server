package com.project.sroa.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserInfo {
    @Id
    public long userNum;
    public String id;
    public String pw;
    public String name;
    public String address;
    public long phoneNum;
    public long code;
}
