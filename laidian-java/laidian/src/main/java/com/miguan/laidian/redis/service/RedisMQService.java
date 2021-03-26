package com.miguan.laidian.redis.service;

import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.redis.util.RedisTopicConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author shixh
 * @Date 2019/9/12
 **/
@Component
public class RedisMQService {

    @Resource
    private StringRedisTemplate template;

    public boolean sendToMQ(String topicName,String json){
        try{
            //通过UUID保证唯一性s
            String uuid = UUID.randomUUID().toString().replace("-", "");
            template.opsForValue().set(RedisKeyConstant.UUID_MQ + uuid, RedisTopicConstant.open);
            template.convertAndSend(topicName,uuid + RedisKeyConstant._MQ_ + json);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
