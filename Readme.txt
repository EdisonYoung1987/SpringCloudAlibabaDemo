cloud-gateway:
    网关系统，采用的spring cloud gateway，主要是路由、过滤功能
    服务注册和发现中心使用的是nacos
    rpc采用dubbo
    http请求客户端：feign
    服务降级和熔断采用sentinel
    链路追踪：skywalking
    负载均衡：除了dubbo自带的，还可以使用Ribbon


登录及请求说明：
    1. 登录后后端会返回一个uid+token，前端需要在header里面增加uid: ${uid}
    2. 前端请求体必须包含_ts时间戳:current time in milliseconds ,类似System.currentTimeMillis();
    2. 登陆后前端请求体必须包含签名_signature，签名规则：MD5(请求体json串按key重排序后+登录时返回的token)，未登录不验签,token除了登录回传，后续不再参与通信
    3. 授权验权采用oauth2，预期设置两种登录模式：账户密码和第三方登录git  //TODO
