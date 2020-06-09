package com.edison.springCloudAlibabaDemo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;
import com.edison.springCloudAlibabaDemo.constant.SystemConstant;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import com.edison.springCloudAlibabaDemo.util.FilterUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    @Autowired
    RedisTemplate redisTemplate;

    //不存在则设置
    private static final RedisScript<Long> releaseScript=
            RedisScript.of("if redis.call('EXISTS',KEYS[1]) == 0 then " +
                    " redis.call('SET',KEYS[1],ARGV[1],'EX',ARGV[2]) else return 1 end",Long.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request= exchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        log.info("当前请求路径：{}",request.getPath());

        //检查请求频率是否过于频繁:ip;
        boolean isFrequent=checkFrequency(request);
        if(isFrequent){
            //将错误信息返回
            return  FilterUtil.errorResponse(exchange.getResponse(), ResponseData.error(ResponseConstant.REQUEST_TO_FREQUENT));
        }

        //检查登录信息
        String uid= FilterUtil.getUidFromHeader(headers);
        if(StringUtil.isNullOrEmpty(uid) && FilterUtil.needLogin(request)){//必须登录
            return  FilterUtil.errorResponse(exchange.getResponse(), ResponseData.error(ResponseConstant.LOGIN_NO_LOGIN));
        }

        log.info("uid={}",uid);

        try {//进行验签等操作
            //因为登陆后的后续请求通信不含token，所以为了权限问题，需要根据uid取出token并加到请求头中
            Object authObject=redisTemplate.opsForValue().get(SystemConstant.TOKEN_KEY+uid);
            if(authObject==null){//已过期或者该uid无效
                log.error("该uid不正确或已失效{}",uid);
                return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(ResponseConstant.LOGIN_NO_LOGIN));
            }
            String access_token=((JSONObject)authObject).getString("access_token");
            headers.set("Authorization","bearer "+access_token);
            Consumer<HttpHeaders> httpHeaders = httpHeader -> {
                httpHeader.set("Authorization", "bearer "+access_token);
            };
            request= request.mutate().headers(httpHeaders).build();
            exchange=exchange.mutate().request(request).build();

            //检查是否需要验签
            boolean needCheckSinature=FilterUtil.needCheckSinature(request);
            if(!needCheckSinature){//不验签
                return chain.filter(exchange);
            }

            //获取请求体
            String requestBody = exchange.getAttribute("REQUEST_BODY_CACHE");
//          requestBody = URLDecoder.decode(requestBody, "UTF-8");
            log.info(requestBody);
            JSONObject jsonObject= JSON.parseObject(requestBody);

            //检查请求是否超时:_ts
            long ts=jsonObject.getLongValue("_ts");
            long curr=System.currentTimeMillis();
            if(Math.abs(curr-ts)>120*1000){//该请求超时>2min
                log.error("请求超时{}-{}",ts,curr);
//                return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(ResponseConstant.REQUEST_TIMEOUT));
            }

            //验签:_signature
            if(!checkSinature(jsonObject,uid,access_token)){//验签失败
                log.error("验签失败");
                return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(ResponseConstant.REQUEST_BAD_REQUEST));
            }

            //检查是否重复请求：uid+_signature 在redis
            String sinature=jsonObject.getString("_signature");
            String key=uid+":"+sinature;
           /* Object ret=redisTemplate.execute(releaseScript, Collections.singletonList(key),1,150);
            if(ret!=null && (Long)ret==1){
                log.error("重复的请求");
                return FilterUtil.errorResponse(exchange.getResponse(),ResponseData.error(ResponseConstant.REQUEST_DUPLICATED));

            }TODO 这里在报错，后面有空再看*/
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
    private boolean checkSinature(JSONObject jsonObject,String uid,String access_token){
        String sinature=jsonObject.getString("_signature");
        log.info("签名={}",sinature);
        jsonObject.remove("_signature");

        String strOrdered=sortJsonString(jsonObject);
        log.info("重排序后：{}",strOrdered);

        //追加token TODO
        access_token="111";

        //TODO 使用MD5编码
        String checkSinature=strOrdered+access_token;

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

    /**过于频繁访问的规则：
     * 同一个ip或用户在30秒内访问次数超过60次，则限制访问120秒*/
    public boolean checkFrequency(ServerHttpRequest request) {
        String key=request.getRemoteAddress().getHostString();
        log.info("远程ip：{}",key);

        key="auth_fr_"+key;
        long times=redisTemplate.opsForValue().increment(key);//访问次数加一
        if(times>10){//60比较合适 15用于测试
            redisTemplate.expire(key,120, TimeUnit.SECONDS);//这样被限制之后每次访问都会重新计时限制时间
            return true;
        }
        if(times==1){
            redisTemplate.expire(key,30, TimeUnit.SECONDS);
        }
        return false;
    }
}
