package com.edison.springCloudAlibabaDemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;
import com.edison.springCloudAlibabaDemo.constant.SystemConstant;
import com.edison.springCloudAlibabaDemo.dto.UserLoginDto;
import com.edison.springCloudAlibabaDemo.dubboService.UserService;
import com.edison.springCloudAlibabaDemo.feignClient.TokenFeignClient;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import com.edison.springCloudAlibabaDemo.service.AuthService;
import feign.Contract;
import feign.Feign;
import feign.FeignException;
import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    //系统内部的client信息
    @Value("${secure.myself.client_id}")
    private String my_client_id;
    @Value("${secure.myself.client_secret}")
    private String my_client_secret;

    private TokenFeignClient tokenFeignClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Reference(check = false)//spring启动时不检查远程应用是否启动
    UserService userService;
    @Resource(name="customRedisTemplate")
    RedisTemplate redisTemplate;

    @Override
    public ResponseData doLogin(UserLoginDto userLoginDto) {
        log.info("{}-{}-{}-{}",my_client_id,my_client_secret,userLoginDto.getUsername(),userLoginDto.getPassword());
        //1. 先去获取用户信息，避免因用户service不可用导致验密报错不明确
        Map<String,Object> userMap= null;
        try {
            userMap = userService.getUserBasicInfoByName(userLoginDto.getUsername());
        } catch (Exception e) {
            return ResponseData.error(ResponseConstant.REMOTE_SERVICE_UNAVAILABLE);
        }

        //2. 请求自己的认证服务器获取token
        // 从nacos中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose("cloud-auth");//认证服务器的应用名称
        URI uri = serviceInstance.getUri();// 此地址就是http:// ip:port

        //因为这个涉及到Basic认证，所以就没有使用
        tokenFeignClient= Feign.builder()
//                .client(client).encoder(encoder).decoder(decoder)
                .contract(new Contract.Default())
                .requestInterceptor(new BasicAuthRequestInterceptor(my_client_id,my_client_secret))
                .target(TokenFeignClient.class,uri.toString());

        try{
            //调用认证服务接口
            String res=tokenFeignClient.getToken("password",userLoginDto.getUsername(),userLoginDto.getPassword());
            //返回有两种情况-成功：ResponseEntity<OAuth2AccessToken> || 失败：ResponseEntity<OAuth2Exception>
            if(res.indexOf("access_token")!=-1) {//成功
                JSONObject jsonObject = JSONObject.parseObject(res);

                //3. 存储token到uid
                String uid=""+userMap.get("uid");
                int expire=Integer.parseInt(""+jsonObject.get("expires_in"));//过期实际
                redisTemplate.opsForValue().set(SystemConstant.TOKEN_KEY+uid,jsonObject,expire, TimeUnit.SECONDS);

                //4. 返回信息处理
                jsonObject.putAll(userMap);
                return ResponseData.success(jsonObject);

            }else{//不成功
                log.error("验密失败：{}",res);
                return ResponseData.error(ResponseConstant.LOGIN_WRONG_USERORPASSWORD);
            }
        }catch (FeignException e){
            log.error("验密异常:"+e.getMessage(),e);
            return ResponseData.error(ResponseConstant.LOGIN_WRONG_USERORPASSWORD);
        }catch (Exception e){//比如请求user微服务失败等其他异常
            log.error("验密异常:",e);
            return ResponseData.error(ResponseConstant.SYSTEM_ERR_CODE);
        }
    }
}
