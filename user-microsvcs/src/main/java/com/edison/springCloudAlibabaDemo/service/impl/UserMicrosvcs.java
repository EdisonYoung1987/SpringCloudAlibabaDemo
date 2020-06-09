package com.edison.springCloudAlibabaDemo.service.impl;

import com.edison.springCloudAlibabaDemo.dubboService.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(loadbalance = "roundrobin")
@Slf4j
public class UserMicrosvcs implements UserService {
    /**返回用户信息，不含密码**/
    @Override
    public Map<String, Object> getUserFullInfoByName(String username) {
        Map<String,Object> userInfo=new HashMap<>(16);
        userInfo.put("uid",100086);//uid
        userInfo.put("username","edison");
        //密码必须加上类型
//        userInfo.put("password","{bcrypt}"+new BCryptPasswordEncoder().encode("123456"));
        userInfo.put("password",new BCryptPasswordEncoder().encode("123456"));
        userInfo.put("credentialsNonExpired",true);//密钥未过期
        userInfo.put("accountNonLocked",true);//用户未被锁
        //用户权限列表
        List<String> roles=new ArrayList<String>(8){
            {add("admin");add("user");}};
        userInfo.put("authorities",roles);//权限列表

        //TODO 先直接返回,后面加数据库和mybatis
        log.info("getUserFullInfoByName-返回所有信息");
        return userInfo;
    }

    /**认证用，返回用户和密码*/
    @Override
    public Map<String, Object> getUserBasicInfoByName(String username) {
        Map<String,Object> userInfo=this.getUserFullInfoByName(username);
        userInfo.remove("password");//密码需剔除
        return userInfo;
    }
}
