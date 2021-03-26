package com.miguan.ballvideo.rabbitMQ.util;

import com.miguan.ballvideo.common.constants.Constant;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.stereotype.Component;

@Component
public class RabbitUtil {
    @Autowired
    private RabbitAdmin rabbitAdmin;

    public long getQueueCount(String queue){
        AMQP.Queue.DeclareOk declareOk = rabbitAdmin.getRabbitTemplate().execute(new ChannelCallback<AMQP.Queue.DeclareOk>() {
            @Override
            public AMQP.Queue.DeclareOk doInRabbit(Channel channel) throws Exception {
                return channel.queueDeclarePassive(queue);
            }
        });
        return declareOk.getMessageCount();
    }
}
