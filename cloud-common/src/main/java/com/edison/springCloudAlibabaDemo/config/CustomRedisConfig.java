package com.edison.springCloudAlibabaDemo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 通用的redis配置类！<br>
 * RedisAutoConfiguration这个spring提供的自动配置类只有<Object,Object>，还不提供序列化
 * 需要自定义一个
 * 需要对RedisTemplate进行配置，主要配置redis操作时，key和value的序列化，否则
 * redis存储的key、value前面有乱码，比如我redisTemplate.set("127.0.0.1","xx");
 * 使用redis-cli展示的是"\xac\xed\x00\x05t\x00-127.0.0.1"
 */

@Configuration
public class CustomRedisConfig {

    @Bean("customRedisTemplate")
//    @SuppressWarnings("all") //没这个 会报错normalConnection不能注入。。
    //RedisConnectionFactory有两个实现类：JedisConnectionFactory和LettuceConnectionFactory
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory normalConnection){
        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(normalConnection);

        //指定key的序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式（包括String,list,set,zset）
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //指定value的序列化方式：
        // JdkSerializationRedisSerializer采用jdk序列化和反序列化，缺点是体积大，占内存多
        //Jackson2JsonRedisSerializer 采用jackson库进行序列化，速度快，体积小，缺点是
        //此类的构造函数中有一个类型参数，必须提供要序列化对象的类型信息,所以一般传json
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.activateDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);//不序列号null
        jackson2JsonRedisSerializer.setObjectMapper(om);

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return  redisTemplate;
    }

}
