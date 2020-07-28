package com.edison.springCloudAlibabaDemo.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class RestClientApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context= SpringApplication.run(RestClientApp.class, args);
        System.out.println("-------------授权认证服务已启动-------------");
        String[] beans=context.getBeanDefinitionNames();
        for(String bean:beans){
            System.out.println(bean);
        }
    }
}
