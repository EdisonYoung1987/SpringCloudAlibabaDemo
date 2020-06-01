package com.edison.springCloudAlibabaDemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;
import com.edison.springCloudAlibabaDemo.dto.UserLoginDto;
import com.edison.springCloudAlibabaDemo.entity.AuthToken;
import com.edison.springCloudAlibabaDemo.feignClient.GitAuthFeignClient;
import com.edison.springCloudAlibabaDemo.feignClient.GitUserFeignClient;
import com.edison.springCloudAlibabaDemo.response.ResponseData;
import com.edison.springCloudAlibabaDemo.response.RspException;
import com.edison.springCloudAlibabaDemo.util.SeqnoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/***/
@RestController    //@RestContller返回json格式不能用于页面提取数据，如果需要返回数据给页面则使用@Controller注释
@RequestMapping("/login")
@Slf4j
public class LoginController {
    /**第三方登录流程：<p/>
     * 1.客户端请求第三方获取授权码
     *   https://github.com/login/oauth/authorize?client_id=76010f96ba5380e16c13
     * 2.后端使用前端传来的第三方授权码+client_id+client_secret请求第三方获取token<p/>*/
    @Value("${git.client_id}")
    private String git_client_id;
    @Value("${git.client_secret}")
    private String git_client_secret;

    @Autowired
    GitAuthFeignClient gitAuthFeignClient;
    @Autowired
    GitUserFeignClient gitUserFeignClient;

    @Autowired
    SeqnoGenerator seqnoGenerator;//流水号生成器

    @PostMapping("/login")
    public ResponseData loging(@RequestBody UserLoginDto userLoginDto){
        log.info("开始验证,username={},password={}",userLoginDto.getUsername(),userLoginDto.getPassword());
        //TODO 使用私钥解密密码

        //请求/auth/oauth/token获取token TODO

        return ResponseData.success(null);
    }

    /**使用git第三方登录*/
    @GetMapping("/loginThirdGit")
    public ResponseData loginThird_GIT(@RequestParam(name="code") String code){//授权码
        Map<String,Object> res=new HashMap<>(8);
        log.info("第三方授权码={}",code);
        try {
            //请求GIT第三方获取access_token
            String resp = gitAuthFeignClient.getGitToken(git_client_id, git_client_secret, code);
            log.info("git响应:{}",resp);
            if(resp.startsWith("error=") || resp.indexOf("access_token=")==-1){//响应失败
                return ResponseData.error(ResponseConstant.LOGIN_THIRD_LOGIN_FAIL);
            }

            String access_token=this.getTokenByKeywordAndSeparator(resp,"access_token=",'&');

            //再次请求git获取部分用户信息 github网络不好，经常出现失败的情况,多试几次，毕竟需要获取id后用
            resp = gitUserFeignClient.getGitUserInfo(access_token, "", "bearer");

            log.info("git用户响应:{}",resp);
            JSONObject jsonObject=JSONObject.parseObject(resp);
            String userName=jsonObject.getString("login");//获取git用户名
            String uid=jsonObject.getString("id");//git的id
            String head_imag=jsonObject.getString("avatar_url");//头像
            if(StringUtils.isEmpty(uid)){//失败
                log.error("获取git用户uid信息失败:{}",jsonObject.getString("message"));//错误信息
                return ResponseData.error(ResponseConstant.LOGIN_THIRD_LOGIN_FAIL);
            }

            //保存登录信息到redis

            //TODO 看后面第三方登录的表如何设计，是否需要新建一个系统内账户

            res.put("uid",uid);
            res.put("userName",userName);
            res.put("access_token",access_token);//前端签名用，后续通信不再传递
            res.put("head_imag",head_imag);
        }catch(Exception e){
            log.error("git第三方登录异常：",e);
            return ResponseData.error(ResponseConstant.LOGIN_THIRD_LOGIN_FAIL);
        }

        return ResponseData.success(res);

    }

    /**在org中找到关键字的值，以separator分割.<p/>
     * 如access_token=f97f9b935a787614271cb30d19cd0f6c059d59c0&scope=&token_type=bearer；
     * 关键字是"access_token=",分隔符是&*/
    private String getTokenByKeywordAndSeparator(String org,String keyWord,char separator){
        if(StringUtils.isEmpty(org)){
            return org;
        }
        int ind1=org.indexOf(keyWord);//获取关键字下标
        int ind2=org.indexOf(separator,ind1+1);//获取关键字后面第一个分隔符
        if(ind1!=-1) {//存在
            if(ind2!=-1) {
                return org.substring(ind1 + keyWord.length(), ind2);
            }else {
                return  org.substring(ind1);
            }
        }
        return org;
    }
}
