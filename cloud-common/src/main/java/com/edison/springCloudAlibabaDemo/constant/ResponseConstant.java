package com.edison.springCloudAlibabaDemo.constant;

public enum ResponseConstant {
    SUCC_CODE(0,"成功"),

    LOGIN_WRONG_PARAMETERS(10001,"登录参数错误!"),
    LOGIN_NO_LOGIN(10002,"未登录"),
    LOGIN_THIRD_LOGIN_FAIL(10003,"第三方授权登录失败"),
    LOGIN_WRONG_USERORPASSWORD(10003,"用户名或密码错误"),

    REQUEST_TO_FREQUENT(10050,"请求过于频繁,请稍后再试"),
    REQUEST_TIMEOUT(10051,"请求超时"),
    REQUEST_DUPLICATED(10052,"请求重复"),
    REQUEST_BAD_REQUEST(10053,"bad request"),

    REMOTE_SERVICE_UNAVAILABLE(88888,"系统错误:服务不可用"),
    SYSTEM_ERR_CODE(99999,"系统错误");

    int code;
    String message;
    ResponseConstant(int code, String msg) {
        this.code=code;
        this.message=msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}