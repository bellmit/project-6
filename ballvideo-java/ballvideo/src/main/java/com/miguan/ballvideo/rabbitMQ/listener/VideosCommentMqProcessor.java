package com.miguan.ballvideo.rabbitMQ.listener;

import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.CommentReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 初始化视频评论信息
 * @author laiyd
 * @date 2019-12-23
 */
@Slf4j
@EnableRabbit
@Component
public class VideosCommentMqProcessor {

    @Resource
    private CommentReplyService commentReplyService;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(autoStartup="#{environment['spring.rabbitmq.open']}",bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEOS_COMMENT_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEOS_COMMENT_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEOS_COMMENT_KEY)
    })
    public void processor(String jsonMsg) {
        String[] strIds = jsonMsg.split(RabbitMQConstant._MQ_);
        commentReplyService.updateVideosInitInfo(strIds[0], strIds[1]);
        //刷新ES缓存
        String json = VideoESOptions.videoUpdate.name() + RabbitMQConstant._MQ_ + strIds[0];
        rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
        log.info("-------视频评论信息"+jsonMsg+"初始化完成--------");
    }
}
