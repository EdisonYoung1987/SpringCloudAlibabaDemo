package com.edison.springCloudAlibabaDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//关闭csrf过滤器，否则模拟端测试报错
            .authorizeRequests()
                    .antMatchers("/login/**","/oauth/**").permitAll()//这些接口不需要验权
                    .anyRequest().authenticated()//其他所有接口需要验权
                .and().httpBasic()//开启http basic认证 /oauth/**接口需要
                .and().formLogin()//定义登录配置类FormLoginConfigurer，会指定表单提交，参数包含username和password
//                    .loginPage("/login/login")//指定登录页面
                    .permitAll()
                .and().logout()//指定登出配置LogoutConfigurer
                    .permitAll();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {//对认证管理器进行配置。
        auth
            .inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("edison").password(new BCryptPasswordEncoder().encode("123456")).roles("USER")
        ;

    }
}