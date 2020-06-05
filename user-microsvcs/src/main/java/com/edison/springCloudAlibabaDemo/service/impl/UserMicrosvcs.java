package com.edison.springCloudAlibabaDemo.service.impl;

import com.edison.springCloudAlibabaDemo.dubboService.UserService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Map;

@Service(loadbalance = "roundrobin")
public class UserMicrosvcs implements UserService {
    @Override
    public Map<String, Object> getUserFullInfoByName(String username) {
        //TODO 先直接返回,后面加数据库和mybatis
        return ;
    }
}
