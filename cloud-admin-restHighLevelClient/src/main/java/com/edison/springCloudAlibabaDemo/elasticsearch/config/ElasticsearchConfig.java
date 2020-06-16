package com.edison.springCloudAlibabaDemo.elasticsearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {
    @Bean(name = "restHighLevelClient" ,destroyMethod="close" )
    public RestHighLevelClient initClient(){
//        RestClientBuilder builder= RestClient.builder(new HttpHost("192.168.50.128",9200));
        RestClientBuilder builder= RestClient.builder(new HttpHost("10.150.62.128",9200));

        //设置header信息
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Charset", "UTF-8"),
                //设置token 是为了安全 网关可以验证token来决定是否发起请求 我们这里只做象征性配置
//                new BasicHeader("E_TOKEN", token)
        };
//        builder.setDefaultHeaders(defaultHeaders);

        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(5000);
            requestConfigBuilder.setSocketTimeout(5000);
            requestConfigBuilder.setConnectionRequestTimeout(5000);
            return requestConfigBuilder;
        });;
        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(100);
            httpClientBuilder.setMaxConnPerRoute(100);
            return httpClientBuilder;
        });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
