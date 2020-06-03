package com.edison.springCloudAlibabaDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    /*@Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
    }*/

    @Bean
    public TokenStore tokenStore(){
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                // Since we want the protected resources to be accessible in the UI as well we need
                // session creation to be allowed (it's disabled by default in 2.0.6)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().requestMatchers().anyRequest()
                .and().anonymous()
                .and().authorizeRequests()
//                    .antMatchers("/product/**").access("#oauth2.hasScope('select') and hasRole('ROLE_USER')")
                .antMatchers("/hello/**").permitAll()
                .anyRequest().authenticated();//其他必须认证过后才可以访问
        // @formatter:on
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resource) throws Exception {
        super.configure(resource);
        //这里把自定义异常处理加进去
        resource.authenticationEntryPoint(new AuthEntryPointConfiguration());
    }
}
