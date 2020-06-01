package com.edison.springCloudAlibabaDemo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FilterUtil {
    public static String getUidFromHeader(HttpHeaders headers){
        String uid=null;
        List<String> list=headers.get("uid");
        if(list!=null && !list.isEmpty()) {//用户已登陆
            uid = list.get(0);
        }
        return uid;
    }

    /**检查请求路径是否需要验签等操作：验签需要缓存请求体*/
    public static boolean needCheckSinature(ServerHttpRequest request){
        String path=request.getPath().toString();
        if(path.startsWith("/user/file")||path.startsWith("/auth/login")){//登录和文件上传等操作都不需要
            return false;
        }else{
            return true;
        }
    }

    /**检查请求是否必须需要先登录*/
    public static boolean needLogin(ServerHttpRequest request){
        String path=request.getPath().toString();
        if(path.startsWith("/auth/login")){//登录操作
            return false;
        }else{
            return true;
        }
    }

    public static Mono<Void> errorResponse(ServerHttpResponse response, ResponseData data){
        byte[] datas = JSONObject.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(datas);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

}
