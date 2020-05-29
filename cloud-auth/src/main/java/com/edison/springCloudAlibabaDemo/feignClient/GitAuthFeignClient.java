package com.edison.springCloudAlibabaDemo.feignClient;

import com.edison.springCloudAlibabaDemo.entity.AuthToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**请求github获取token的http client*/
@FeignClient(name="gitAuthFeignClient",url="https://github.com")
public interface GitAuthFeignClient {
    @GetMapping("/login/oauth/access_token")
    public String getGitToken(@RequestParam("client_id") String client_id,
                                 @RequestParam("client_secret")String client_secret,
                                 @RequestParam("code")String code);
    @GetMapping("/user")
    public String getGitUserInfo(@RequestParam("access_token") String access_token,
                                 @RequestParam("scope")String scope,
                                 @RequestParam("token_type")String token_type);
}
