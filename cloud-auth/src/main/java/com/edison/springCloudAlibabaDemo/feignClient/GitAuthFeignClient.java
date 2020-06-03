package com.edison.springCloudAlibabaDemo.feignClient;

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
}
