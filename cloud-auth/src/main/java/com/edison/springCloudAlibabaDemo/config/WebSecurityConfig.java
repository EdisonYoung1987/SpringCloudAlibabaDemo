package com.edison.springCloudAlibabaDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()//关闭csrf过滤器，否则模拟端测试报错
            .authorizeRequests()
                    .antMatchers("/login/**").permitAll()//这些接口不需要验权
                    .anyRequest().authenticated()//其他所有接口需要验权
                .and().formLogin()//定义登录配置类FormLoginConfigurer，会指定表单提交，参数包含username和password
//                    .loginPage("/login/login")//指定登录页面
                    .permitAll()
                .and().logout()//指定登出配置LogoutConfigurer
                    .permitAll()
                ;//关闭http basic认证 /oauth/**接口需要;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /* 这个可以直接在内存中缓存一个用户信息
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {//
        auth
            .inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("edison").password(new BCryptPasswordEncoder().encode("123456")).roles("USER")
        ;

    }*/
}
