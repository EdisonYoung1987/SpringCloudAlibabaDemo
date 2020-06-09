package com.edison.springCloudAlibabaDemo.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Iterator;

@Slf4j
@Component
public class DebugFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request= exchange.getRequest();
        HttpHeaders headers=request.getHeaders();
        log.info("当前请求路径：{}",request.getPath());

        StringBuilder sb=new StringBuilder(256);
        Iterator<String> iterator=headers.keySet().iterator();
        while(iterator.hasNext()){
            String key=iterator.next();
            sb.append(key);
            sb.append(":");
            sb.append(headers.get(key));
            sb.append('\n');
        }
        log.info("请求头：{}",sb.toString());

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 2;
    }

}
