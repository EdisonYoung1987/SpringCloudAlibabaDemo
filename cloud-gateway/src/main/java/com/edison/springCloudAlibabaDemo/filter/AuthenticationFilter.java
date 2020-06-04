package com.edison.springCloudAlibabaDemo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import com.edison.springCloudAlibabaDemo.util.FilterUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    Logger logger= LoggerFactory.getLogger(AuthenticationFilter.class);
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request= exchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        log.info("当前请求路径：{}",request.getPath());

        //TODO 检查请求频率是否过于频繁:rediskey=已登录uid/未登录ip; 除了验签，其他检查逻辑都可以在springbootdemonew中找到

        //检查登录信息
        String uid= FilterUtil.getUidFromHeader(headers);
        if(StringUtil.isNullOrEmpty(uid) && FilterUtil.needLogin(request)){//必须登录
            return  FilterUtil.errorResponse(exchange.getResponse(), ResponseData.error(ResponseConstant.LOGIN_NO_LOGIN));
        }

        log.info("uid={}",uid);

        boolean needCheckSinature=FilterUtil.needCheckSinature(request);
        if(!needCheckSinature){//不验签
            return chain.filter(exchange);
        }

        try {//进行验签等操作
            //获取请求体
            String requestBody = exchange.getAttribute("REQUEST_BODY_CACHE");
//          requestBody = URLDecoder.decode(requestBody, "UTF-8");
            log.info(requestBody);
            JSONObject jsonObject= JSON.parseObject(requestBody);

            //TODO 检查请求是否超时:_ts
            long ts=jsonObject.getLongValue("_ts");
            log.info("请求体中时间={}",ts);

            //验签:_signature
            if(!checkSinature(jsonObject,uid)){//验签失败
                log.error("验签失败");
                return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(ResponseConstant.REQUEST_BAD_REQUEST));
            }

            //TODO 检查是否重复请求：_signature+uid 在redis
        } catch (Exception e) {
            log.error("验权异常:",e);
            return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(e));
        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    /**验签*/
    private boolean checkSinature(JSONObject jsonObject,String uid){
        String sinature=jsonObject.getString("_signature");
        log.info("签名={}",sinature);
        jsonObject.remove("_signature");

        String strOrdered=sortJsonString(jsonObject);
        log.info("重排序后：{}",strOrdered);

        //从redis获取token TODO
        redisTemplate.opsForValue().get("auth_"+uid);
        String token="111";//TODO 暂时用111代替

        //TODO 使用MD5编码
        String checkSinature=strOrdered+token;

        return checkSinature.equals(sinature);
    }

    /**按key重新排序jsong字串，为验签做准备*/
    public static String sortJsonString(JSONObject jsonObject){
        Set<String> keys=jsonObject.keySet();
        String[] keyArray= new String[keys.size()];
        int i=0;
        for(String key :keys){
            keyArray[i++]=key;
        }
        Arrays.sort(keyArray);

        StringBuilder sb=new StringBuilder(128);
        for(String key :keyArray){
//            sb.append('\"');
            sb.append(key);
//            sb.append("\":\"");
            sb.append(jsonObject.getString(key));
//            sb.append("\",");
        }
        return sb.toString();
    }
}
