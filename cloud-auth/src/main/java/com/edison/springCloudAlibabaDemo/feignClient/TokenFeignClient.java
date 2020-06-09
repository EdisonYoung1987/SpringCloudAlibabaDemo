package com.edison.springCloudAlibabaDemo.feignClient;

import feign.Param;
import feign.RequestLine;

/**使用用户名+密码+client_id+client_secret请求本地/auth/oauth/token获取令牌*/
//@FeignClient(name="cloud-auth")
public interface TokenFeignClient {
//    @GetMapping(value = "/auth/oauth/token",consumes = "application/x-www-form-urlencoded")
    @RequestLine("POST /auth/oauth/token?grant_type={grant_type}&username={username}&password={password}&scope=select")
    public String getToken(@Param("grant_type") String grant_type,
                                   @Param("username") String username,
                                   @Param("password") String password);
}
