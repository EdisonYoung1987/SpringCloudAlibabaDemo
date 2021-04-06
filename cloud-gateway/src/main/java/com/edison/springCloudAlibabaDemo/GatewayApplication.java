package com.edison.springCloudAlibabaDemo;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.MutableDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        log.info("--------------网关服务启动---------------");
        MutableDateTime dateTime=new MutableDateTime();
        dateTime.getYear();
    }

}