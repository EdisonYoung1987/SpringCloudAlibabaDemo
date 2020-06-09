cloud-gateway:
    网关系统，采用的spring cloud gateway，主要是路由、过滤功能
    服务注册和发现中心使用的是nacos
    rpc采用dubbo，其注册和发现中心还是nacos
    http请求客户端：feign
    服务降级和熔断采用sentinel
    链路追踪：skywalking
    负载均衡：除了dubbo自带的，还可以使用Ribbon

前后端交互说明：
       a.不管是哪个接口，前端请求体必须包含_ts时间戳:current time in milliseconds ,类似System.currentTimeMillis();
       b.必须登录，授权验权采用oauth2，预期设置两种登录模式：账户密码和第三方登录git

账号密码登录：
    0. 登录-1：前端请求login/pk获取公钥
    1. 登录-2：前端用公钥加密用户密码发送后端/login/login,后端使用私钥解密后再Bcrypt加密后请求/oauth/token获取access_token，
               登录成功后后端会返回一个uid+token:
               { "username":"","password":""} //这里参数名称默认就是这个，由HttpSecurity配置时指定的formLogin()预定义。
    2. 登录-3：后续通信前端需要在header里面增加uid: ${uid};
              且请求体必须包含签名_signature，签名规则：MD5(请求体json串按key重排序后+登录时返回的token)

第三方登录流程：
    1. 前端请求第三方/auth/code?client_id=xx&scope=xx&callback=本系统第三方登录接口：获取授权码
    2. 浏览器或前端应用调用callback接口+授权码code请求后端/login/loginThirdXxx第三方登录
    3. 后端请求第三方/auth/token?client_id=xxx&client_secret=xx&code=xxx获取token
    4. 后端请求第三方/user?token=xxx获取部分用户信息，如id\unionid\openid
       查询user和第三方表，如果无记录，则user和第三方表都插入相关记录，user.userName=5位随机字符串+20位全局流水号，user.password=Bcrypt加密(随机10位字符串)，
             否则直接查询获得user表记录;
       然后使用userName+password+本系统的client_id+本系统的client_secret请求oauth2服务器获取本系统的token_new，成功后返回uid+token_new;
其他：
    后端提供用户补充用户信息的接口，以及绑定第三方账号的接口

RSA工具“https://blog.csdn.net/weixin_42231507/article/details/80898890
