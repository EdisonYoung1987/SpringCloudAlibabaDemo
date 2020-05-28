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

    /**检查请求路径是否为文件操作*/
    public static boolean isFileOperation(ServerHttpRequest request){
        String path=request.getPath().toString();
        if(path.startsWith("/user/file")){
            return true;
        }else{
            return false;
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
