package com.edison.springCloudAlibabaDemo.service.impl;

import com.edison.springCloudAlibabaDemo.dto.UserLoginDto;
import com.edison.springCloudAlibabaDemo.entity.AuthToken;
import com.edison.springCloudAlibabaDemo.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public AuthToken login(UserLoginDto userLongDto) {
        return null;
    }
}
