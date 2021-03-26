package com.miguan.laidian.redis.mq.listener;

import com.miguan.laidian.redis.mq.ReceiverRedisMessage;
import com.miguan.laidian.redis.mq.RedisDefaultMessage;
import com.miguan.laidian.redis.util.RedisTopicConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @Author shixh
 * @Date 2019/9/3
 **/
@Configuration
public class RedisMQListener {

    /**
     * 使用容器进行监听
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory connectionFactory,
            MessageListener userContact) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //你的消息订阅处理器
        container.addMessageListener(userContact, new PatternTopic(RedisTopicConstant.TOPIC_USERCONTACT_MQ));//通讯录监听
        return container;
    }

    @Bean
    MessageListenerAdapter userContact(ReceiverRedisMessage message) {
        return new MessageListenerAdapter(message, "userContact");
    }
}
