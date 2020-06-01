package com.edison.springCloudAlibabaDemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableFeignClients
@Slf4j
public class CloudAuthApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context=SpringApplication.run(CloudAuthApplication.class, args);
        log.info("-------------授权认证服务已启动-------------");
        String[] beans=context.getBeanDefinitionNames();
     /*   for(String bean:beans){
            System.out.println(bean);
        }*/
    }
}
