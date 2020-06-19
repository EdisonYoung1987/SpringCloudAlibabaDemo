package com.edison.springCloudAlibabaDemo.elasticsearch.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserInfo {
    private String name;
    private String address;
    private String remark;
    private int age;
    private float salary;
    private String birthDate;
    private Date createTime;
}
