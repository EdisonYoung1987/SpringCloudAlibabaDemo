package com.edison.springCloudAlibabaDemo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    public String access_token;//访问token就是短令牌，用户身份令牌
    public String refresh_token;//刷新token
    public long expires_in; //过期时间
//    public String jwt_token;//jwt令牌
}