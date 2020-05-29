package com.edison.springCloudAlibabaDemo.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**请求github获取token的http client*/
@FeignClient(name="gitUserFeignClient",url="https://api.github.com")
public interface GitUserFeignClient {
    @GetMapping("/user")
    public String getGitUserInfo(@RequestParam("access_token") String access_token,
                                 @RequestParam("scope") String scope,
                                 @RequestParam("token_type") String token_type);
}
