package com.edison.springCloudAlibabaDemo.dubboService;

import java.util.Map;

public interface UserService {
    /**获取用户信息-含密码 oauth/token服务调用用于验证用户信息*/
    Map<String,Object> getUserFullInfoByName(String username);
}
