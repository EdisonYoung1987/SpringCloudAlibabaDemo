package com.edison.springCloudAlibabaDemo.response;


import com.alibaba.fastjson.JSON;
import com.edison.springCloudAlibabaDemo.constant.ResponseConstant;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;


/**作为前后端交互的响应消息*/
public class ResponseData implements Serializable {
    private int statusCode;  //返回码
    private String retMessage;  //返回消息
    private Object data;        //数据

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public void setRetMessage(String retMessage) {
        this.retMessage = retMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseData(ResponseConstant responseConstant){
        this.statusCode=responseConstant.getCode();
        this.retMessage=responseConstant.getMessage();
    }
    public ResponseData( ){
    }

    public static ResponseData error(ResponseConstant responseConstant){
        ResponseData response=new ResponseData();
        response.setStatusCode(responseConstant.getCode());
        response.setRetMessage(responseConstant.getMessage());
        return response;
    }

    //构造错误信息
    public static ResponseData error(int errCd,String message){
        ResponseData response=new ResponseData();
        response.setStatusCode(errCd);
        response.setRetMessage(message);
        return response;
    }

    //构造异常信息
    public static ResponseData error(Exception e){
        ResponseData response=new ResponseData();
        if(e instanceof RspException) {
            ResponseConstant code=((RspException) e).getResponseCode();
            response.setStatusCode(code.getCode());
            response.setRetMessage(code.getMessage());
        }else{
            response.setStatusCode(ResponseConstant.SYSTEM_ERR_CODE.getCode());
            response.setRetMessage(ResponseConstant.SYSTEM_ERR_CODE.getMessage() + ":" + e.getMessage());
        }
        return response;
    }

    public static ResponseData success(Object data){
        ResponseData response=new ResponseData();
        response.setStatusCode(ResponseConstant.SUCC_CODE.getCode());
        response.setRetMessage(ResponseConstant.SUCC_CODE.getMessage());
        response.setData(data);
        return response;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);//这样，每个Controller返回的Response对象就被转为了json字符串。
    }
}
