package com.edison.springCloudAlibabaDemo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**请求本地/auth/oauth/token获取令牌*/
@FeignClient(name="cloud-auth")
public interface TokenFeignClient {
    @GetMapping("/auth/oauth/token")
    public String getGitUserInfo(@RequestParam("grant_type") String grant_type,
                                 @RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("client_id") String client_id);
}
