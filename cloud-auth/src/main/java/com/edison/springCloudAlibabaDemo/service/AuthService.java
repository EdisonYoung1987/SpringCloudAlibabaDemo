package com.edison.springCloudAlibabaDemo.service;


import com.edison.springCloudAlibabaDemo.dto.UserLoginDto;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public interface AuthService {

    /**
     * 用户登录认证
     * @param userLongDto
     * @return
     */
    public ResponseData doLogin(UserLoginDto userLongDto);


}
