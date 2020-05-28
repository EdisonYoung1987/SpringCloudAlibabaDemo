package com.edison.springCloudAlibabaDemo.filter;

import com.edison.springCloudAlibabaDemo.util.FilterUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**读取请求体并缓存起来
 * 参考：https://windmt.com/2019/01/16/spring-cloud-19-spring-cloud-gateway-read-and-modify-request-body/
 *      以及ModifyRequestBodyGatewayFilterFactory；
 * 另一种方法是config/RouteConfig*/
@Component
@Slf4j
public class ReadRequestBodyFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入ReadRequestBodyFilter");
        log.info(exchange.getRequest().getHeaders().toString());
        String uid= FilterUtil.getUidFromHeader(exchange.getRequest().getHeaders());
        if(!StringUtil.isNullOrEmpty(uid)){//已登录 需要验签等操作，故缓存请求体
            if(FilterUtil.isFileOperation(exchange.getRequest())){//文件操作直接返回
                return chain.filter(exchange); //不做任何处理
            }
            ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());

            // mediaType
            MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            log.info("content-Type={}",mediaType);

            // TODO: flux or mono
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class) //String.class?Object.class
                    .flatMap(body -> {
                        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {//非文件请求application/json
                            /*// origin body map
                            Map<String, Object> bodyMap = decodeBody(body);

                            // TODO 假设有些解密的动作可以放到这里

                            // new body map
                            Map<String, Object> newBodyMap = new HashMap<>();

                            return Mono.just(encodeBody(newBodyMap));*/
                            exchange.getAttributes().put("REQUEST_BODY_CACHE", body);
                            return Mono.just(body);
                        }
                        return Mono.empty();
                    });

            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());

            // the new content type will be computed by bodyInserter
            // and then set in the request decorator
            headers.remove(HttpHeaders.CONTENT_LENGTH); //这里body长度不变，所以contentLength也不变

            // if the body is changing content types, set it here, to the bodyInserter
            // will know about it
            /*if (config.getContentType() != null) {
                headers.set(HttpHeaders.CONTENT_TYPE, config.getContentType());
            }*/

            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
                    exchange, headers);
            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                    // .log("modify_request", Level.INFO)
                    .then(Mono.defer(() -> {
                        ServerHttpRequest decorator = decorate(exchange, headers,
                                outputMessage);
                        return chain
                                .filter(exchange.mutate().request(decorator).build());
                    }));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
                                        CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                }
                else {
                    // TODO: this causes a 'HTTP/1.1 411 Length Required' // on
                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                    log.info("trunked模式");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
}
