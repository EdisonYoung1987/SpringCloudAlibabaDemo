package com.edison.springCloudAlibabaDemo.service.impl;

import com.edison.springCloudAlibabaDemo.dubboService.UserService;
import com.edison.springCloudAlibabaDemo.entity.UserAuth;
import com.edison.springCloudAlibabaDemo.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**重写用户服务*/
@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {
    @Reference(check = false)//spring启动时不检查远程应用是否启动
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("进入MyUserDetailsService：{}", username);

        Map<String, Object> userMap = null;/**/
        try {
            userMap = userService.getUserFullInfoByName(username);
        } catch (RpcException e) {
            log.error("验密失败，当前用户服务不可用");
            throw e;
        }
        try {
            if (userMap == null || userMap.isEmpty()) {
                log.error("用户{}不存在");
                return null;
            }
            String uid=MapUtil.getAsString(userMap,"uid","");
            username = MapUtil.getAsString(userMap, "username", "");
            String password = MapUtil.getAsString(userMap, "password", "");
            boolean credentialsNonExpired = MapUtil.getAsBoolean(userMap, "credentialsNonExpired", true);
            boolean accountNonLocked = MapUtil.getAsBoolean(userMap, "accountNonLocked", true);

            List<String> authorities = (List<String>) userMap.getOrDefault("authorities", new ArrayList<String>(2));

            UserAuth userAuth = new UserAuth(username, password, credentialsNonExpired, accountNonLocked,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.join(authorities, ',')));
            return userAuth;
        }catch (Exception e){
            log.error("获取用户信息失败",e);
            throw e;
        }
    }
}
