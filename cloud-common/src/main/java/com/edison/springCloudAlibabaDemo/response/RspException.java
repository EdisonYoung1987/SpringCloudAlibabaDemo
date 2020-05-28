package com.edison.springCloudAlibabaDemo.response;

import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;

public class RspException extends Exception {
    ResponseConstant responseCode;

    public RspException(ResponseConstant responseConstant){
        super(responseConstant.getMessage());
        this.responseCode=responseConstant;
    }

    ResponseConstant getResponseCode(){
        return this.responseCode;
    }

}
