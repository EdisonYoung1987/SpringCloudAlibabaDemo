package com.edison.springCloudAlibabaDemo.dubboService;

import java.util.Map;

public interface UserService {
    /**获取用户信息-含密码 -auth认证使用*/
    Map<String,Object> getUserFullInfoByName(String username);

    /**获取用户信息-基本信息 不含密码 含权限列表 */
    Map<String,Object> getUserBasicInfoByName(String username);
}
