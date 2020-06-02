package com.edison.springCloudAlibabaDemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController    //@RestContller返回json格式不能用于页面提取数据，如果需要返回数据给页面则使用@Controller注释
@RequestMapping("/hello")
@Slf4j
public class HelloController {
    /**这个没有权限验证*/
    @PostMapping("/sayhello" )
    public ResponseData hello(HttpServletRequest request, @RequestBody String body){

        log.info(request.getCharacterEncoding());
//        //请求使用的协议和版本
//        log.info("Protocol: " + request.getProtocol());
//        //返回当前所使用的协议：http  ftp等
//        log.info("Scheme: " + request.getScheme());
//        //请求被发送到的服务器主机名
//        log.info("Server Name: " + request.getServerName());
//        //请求被发送到的服务器端口
//        log.info("Server Port: " + request.getServerPort());
//        //客户端的IP地址
//        log.info("Remote Addr: " + request.getRemoteAddr());
//        //客户端的主机名
//        log.info("Remote Host: " + request.getRemoteHost());
//        //请求的字符编码
//        log.info("Character Encoding: " + request.getCharacterEncoding());
//        //请求的消息体（body）的大小 字节数，没有消息体的，返回-1
//        log.info("Content Length: " + request.getContentLength());
//        //返回请求的消息体的MIME类型，MIME是描述消息内容类型的因特网标准
//        log.info("Content Type: " + request.getContentType());
//        //获取保护servlet的认证方案名(BASIC或SSL 等),未受保护的servlet返回的就是null
//        log.info("Auth Type: " + request.getAuthType());
//        //获取请求方式获取请求方式(GET与POST为主,也会有PUT、DELETE、INPUT)
//        log.info("HTTP Method: " + request.getMethod());
//        //解释在下面
//        log.info("Path Info: " + request.getPathInfo());
//        //返回URL中的额外路径信息所对应的资源的真实路径。
//        log.info("Path Trans: " + request.getPathTranslated());
//        //获取url中参数的部分
//        log.info("Query String: " + request.getQueryString());
//        //如果客户登录了，那么获取用户的登录信息
//        log.info("Remote User: " + request.getRemoteUser());
//        //获取sessionID
//        log.info("Session Id: " + request.getRequestedSessionId());
//        //获取完整的url，不带参数的
//        log.info("Request URI: " + request.getRequestURI());
//        //获取请求路径
//        log.info("Servlet Path: " + request.getServletPath());
//
//        //接下来是获取消息头中的信息
//        //获取accept等等
//        log.info("Accept: " + request.getHeader("Accept"));
//        log.info("Host: " + request.getHeader("Host"));
//        log.info("Referer : " + request.getHeader("Referer"));
//        log.info("Accept-Language : " + request.getHeader("Accept-Language"));
//        log.info("Accept-Encoding : " + request.getHeader("Accept-Encoding"));
//        log.info("User-Agent : " + request.getHeader("User-Agent"));
//        log.info("Connection : " + request.getHeader("Connection"));
//        log.info("Cookie : " + request.getHeader("Cookie"));
//        log.info("Created : " + request.getSession().getCreationTime());
//        log.info("LastAccessed : " + request.getSession().getLastAccessedTime());
        JSONObject jsonObject= JSONObject.parseObject(body);
        String name=jsonObject.getString("name");
        long ts=jsonObject.getLongValue("_ts");
        log.info("_ts={}",ts);
        if(name==null) {
            return ResponseData.success("HELLO");
        }else{
            return ResponseData.success("HELLO "+name);
        }
    }
}
