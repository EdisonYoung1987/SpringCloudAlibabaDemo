package com.edison.springCloudAlibabaDemo.config;

import com.edison.springCloudAlibabaDemo.service.impl.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;//源自WebSecurityConfig的实例化
    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    DataSource dataSource;//实际上是JdbcAutoconfiguration实现的配置

    @Autowired
    MyUserDetailsService myUserDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //配置客户端，主要是client_id client_sercret 授权模式 scope
        /*clients.inMemory().withClient("client1")
//                .resourceIds(DEMO_RESOURCE_ID)
                .authorizedGrantTypes("authorization_code",  "refresh_token") //client1只能使用授权码模式-第三方应用使用
//                .authorizedGrantTypes("client_credentials", "refresh_token")
//                .scopes("select")
//                .authorities("client")
                .redirectUris("http://localhost:9001/user/callback")
                .secret(new BCryptPasswordEncoder().encode("123456"))
                .accessTokenValiditySeconds(3*24*3600)
                .and().withClient("client2")
//                .resourceIds(DEMO_RESOURCE_ID)
                .authorizedGrantTypes("client_credentials","password", "refresh_token")//client2只能使用密码模式-内部使用
//                .scopes("select")
                .accessTokenValiditySeconds(7*24*3600)
//                .authorities("client")
                .secret(new BCryptPasswordEncoder().encode("123456")) //密码模式不需要client_secret
        ;*/
        //改为数据库模式,需要先建表oauth_client_details
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(new RedisTokenStore(redisConnectionFactory)) //认证服务器和资源服务器都配置redis保存token，这样资源服务器直接查redis即可验证是否有效
                .authenticationManager(authenticationManager)//WebSecurityConfig提供的
                .userDetailsService(myUserDetailsService);//重写用户获取服务
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .passwordEncoder(new BCryptPasswordEncoder())
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
        //允许表单认证
//        oauthServer.allowFormAuthenticationForClients();
    }

}
