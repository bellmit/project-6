package com.miguan.ballvideo.rabbitMQ.listener.recommend;

import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.recommend.ReccommendMqConsumerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * 索引数据操作MQ
 * @author shixh
 * @date 2020-09-01
 */
@Slf4j
@EnableRabbit
@Component
public class RecommendMqProcessor {
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Autowired
    private ReccommendMqConsumerServiceImpl reccommendMqConsumerService;

    /*@RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEO_REC_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEO_REC_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEO_REC_KEY)
    })*/
    public void processor(String jsonMsg) {
        String[] params = jsonMsg.split(RabbitMQConstant._MQ_);
        String options = params[0];
        VideoESOptions videoESOptions = VideoESOptions.valueOf(options);
        switch (videoESOptions) {
            case videoAdd:
            case videoUpdate:
            case directVideoAdd:{
                String videoIds = params[1];
                reccommendMqConsumerService.updateVideo(videoIds, options);
                break;
            }
            case videoDelete: {
                String videoIds = params[1];
                reccommendMqConsumerService.deleteVideo(videoIds, options);
                break;
            }
            case preVideoAdd:
                break;
            case preVideoDelete:
                break;
            case gatherAddOrDelete: {
                String gatherId = params[1];
                String videoIds = params[2];
                reccommendMqConsumerService.updateByGatherId(Long.parseLong(gatherId), videoIds);
                break;
            }
            case gatherDeleteOrClose: {
            }
            case deleteDueVideos: {
                reccommendMqConsumerService.deleteExpiredVideos();
                break;
            }
            default:
        }
    }
}
