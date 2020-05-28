//package com.edison.springCloudAlibabaDemo.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//
/**这个自定义路由实际上也是为了读取body，请求body会被exchange缓存到附加属性中，
 * exchange.getAttributes().put(CACHE_REQUEST_BODY_OBJECT_KEY，body),这样的话劣势是：
 * 无法实现动态路由，因为路由规则在这里都写死了，优点是使用底层提供的类读取body，比自己实现的
 * 更有保证*/
//@Configuration
//public class RouteConfig {
//    @Bean
//    public RouteLocator requestBodyCacheRoute(RouteLocatorBuilder builder) {
//        RouteLocatorBuilder.Builder routes = builder.routes();
//        RouteLocatorBuilder.Builder serviceProvider = routes
//                .route("requestBodyCacheRoute",
//                        r -> r.method(HttpMethod.POST,HttpMethod.GET)
//                                .and()
//                                .readBody(String.class, readBody -> true)
//                                .and()
//                                .path("/**")
//                                .uri("lb://user-microsvcs")); //不能动态路由了
//        RouteLocator routeLocator = serviceProvider.build();
//        return routeLocator;
//    }
//
//}
