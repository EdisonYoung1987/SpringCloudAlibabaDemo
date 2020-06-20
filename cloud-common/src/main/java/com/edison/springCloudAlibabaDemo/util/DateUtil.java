package com.edison.springCloudAlibabaDemo.util;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static final String FORMAT_DATE_DEFAULT="yyyyMMddHHmmss";
    private static final String FORMAT_DATE_SHORT="yyyyMMdd";
    private static final String FORMAT_DATE_TIME_SHORT="yyyyMMddHHmmss";
    private static final String FORMAT_TIME_SHORT="HHmmss";

    public static String getFormatDateString(LocalDateTime date, String format){
        String res=null;
        if(date==null) {
            return null;
        }
        if(StringUtils.isEmpty(format)){
            format=FORMAT_DATE_DEFAULT;
        }

        try {
            res=date.format(DateTimeFormatter.ofPattern(format));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
