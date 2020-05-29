package com.edison.springCloudAlibabaDemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController    //@RestContller返回json格式不能用于页面提取数据，如果需要返回数据给页面则使用@Controller注释
@RequestMapping("/user")
@Slf4j
public class UserController {
}
