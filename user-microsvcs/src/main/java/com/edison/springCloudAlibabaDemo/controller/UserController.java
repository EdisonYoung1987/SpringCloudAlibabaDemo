package com.edison.springCloudAlibabaDemo.controller;

import com.edison.springCloudAlibabaDemo.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController    //@RestContller返回json格式不能用于页面提取数据，如果需要返回数据给页面则使用@Controller注释
@RequestMapping("/user")
@Slf4j
public class UserController {
    @GetMapping("/getUserInfo")
    public ResponseData getUserInfo(HttpServletRequest request){
        Map<String,Object> res=new HashMap<>(16);
        String uid=request.getHeader("uid");//用户id
        //TODO
        res.put("username","张三丰");
        return ResponseData.success(res);
    }
}
