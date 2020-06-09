package com.edison.springCloudAlibabaDemo.util;

import java.util.Map;

public class MapUtil {
    public static String getAsString(Map<String,Object> inmap,String key,String defaultVal){
        if(inmap==null ){
            return defaultVal;
        }
        try{
            Object object=inmap.get(key);
            if(object==null){
                return defaultVal;
            }
            return (String)object;
        }catch (Exception e){
            return defaultVal;
        }
    }
    public static Boolean getAsBoolean(Map<String,Object> inmap,String key,boolean defaultVal){
        if(inmap==null ){
            return defaultVal;
        }
        try{
            Object object=inmap.get(key);
            if(object==null){
                return defaultVal;
            }
            return (boolean)object;
        }catch (Exception e){
            return defaultVal;
        }
    }
    public static long getAsLong(Map<String,Object> inmap,String key,long defaultVal){
        if(inmap==null ){
            return defaultVal;
        }
        try{
            Object object=inmap.get(key);
            if(object==null){
                return defaultVal;
            }
            return (long)object;
        }catch (Exception e){
            return defaultVal;
        }
    }
}
