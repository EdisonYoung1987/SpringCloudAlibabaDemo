package com.edison.springCloudAlibabaDemo.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * ***************************************************
 * @ClassName UserLongDto
 * @Description 用户登录dto
 * ****************************************************
 **/
@Data
@ToString
public class UserLoginDto implements Serializable {
    private String username;
    private String password;
//    private String grant_type;//用户类型
}
