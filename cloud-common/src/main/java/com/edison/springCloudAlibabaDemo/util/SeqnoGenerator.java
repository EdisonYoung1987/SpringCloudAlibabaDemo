package com.edison.springCloudAlibabaDemo.util;

import com.edison.springCloudAlibabaDemo.constant.SystemConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**序号生成器*/
@Component
@Slf4j
public class SeqnoGenerator {
    private static final long SEQ_STEP=5L; //每次从redis获取一批次流水号使用
    private long currNum=0L;
    private long currMaxNum=0L;

    @Autowired
    RedisTemplate redisTemplate;

    /**生成一个全局的id，用于跟踪*/
    public  String getGlobalReqId(){
        return UUID.randomUUID().toString();
    }

    /**根据redis生成一个分布式的20位长度全局流水号<p>
     * 组成规则：系统标志+yymmddHHMM+10位序号<p>
     * 生成流程：key=yymmddHHMM,value=游标位置，可用流水号=key+value*偏移量(100)*/
    public String getSeqNoFromReis(){
        synchronized (this) {
            if (currMaxNum == 0 || currNum >= currMaxNum) { //还未有流水号或者该批次流水号已用完，需要从新获取
                long currentStep = this.getCurrStepFromRedis();
                currMaxNum = currentStep;
                currNum = currMaxNum - SEQ_STEP;

            }
            currNum++;//从1开始
        }
        return SystemConstant.SYSTEM_SIGN+
                DateUtil.getFormatDateString(LocalDateTime.now(),"yyMMddHHmm")+
                String.format("%010d",currNum);
    }

    /**获取当天流水偏移量*/
    private long getCurrStepFromRedis(){
        String key="EdisonSeq:"+DateUtil.getFormatDateString(LocalDateTime.now(),"yyyyMMdd");
        long currentStep=redisTemplate.opsForValue().increment(key,SEQ_STEP);
        if(currentStep==SEQ_STEP){//说明是当天第一个获取流水
            log.info("当天第一个获取流水");
            redisTemplate.expire(key,48, TimeUnit.HOURS);//为避免时间差异，保留2天
        }
        return currentStep;
    }
}
