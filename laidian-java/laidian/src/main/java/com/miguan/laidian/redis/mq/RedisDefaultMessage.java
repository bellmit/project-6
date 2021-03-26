package com.miguan.laidian.redis.mq;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @Author shixh
 * @Date 2019/9/4
 **/
@Component
public class   RedisDefaultMessage implements MessageListener {

    public static Log log = LogFactory.getLog(RedisDefaultMessage.class);

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        String msg = serializer.deserialize(message.getBody());
        log.info("接收到的消息是：" + msg);
    }
}

