package com.edison.springCloudAlibabaDemo.constant;

public enum ResponseConstant {
    SUCC_CODE(0,"成功"),

    LOGIN_WRONG_PARAMETERS(10001,"登录参数错误!"),
    LOGIN_NO_LOGIN(10002,"未登录"),
    REQUEST_TO_FREQUENT(10003,"请求过于频繁,请稍后再试"),
    REQUEST_TIMEOUT(10004,"请求超时"),
    REQUEST_DUPLICATED(10005,"请求重复"),
    REMOTE_SERVICE_UNAVAILABLE(88888,"远程服务不可达"),
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