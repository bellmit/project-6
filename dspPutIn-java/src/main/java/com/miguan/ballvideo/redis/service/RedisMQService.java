//package com.miguan.ballvideo.redis.service;
//
//import com.miguan.ballvideo.redis.util.IPUtils;
//import com.miguan.ballvideo.redis.util.RedisKeyConstant;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
///**
// * @Author shixh
// * @Date 2019/9/12
// **/
//@Component
//public class RedisMQService {
//
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//
//    public boolean sendToMQ(String topicName,String json){
//        try{
//            String IP = IPUtils.getHostAndPort();//获取IP地址
//            //通过UUID保证唯一性s
//            stringRedisTemplate.convertAndSend(topicName, IP + RedisKeyConstant._MQ_ + json);
//        }catch (Exception e){
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//}
