package com.miguan.ballvideo.rabbitMQ.listener;

import com.miguan.ballvideo.common.enums.VideoESOptions;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.service.VideoEsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 索引数据操作MQ
 * @author shixh
 * @date 2020-09-01
 */
@Slf4j
@EnableRabbit
@Component
public class VideoEsMqProcessor {

    @Resource
    private VideoEsService firstVideoEsItemService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMQConstant.VIDEOS_ES_SEARCH_QUEUE),
                    exchange = @Exchange(value = RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, autoDelete = "true"),
                    key = RabbitMQConstant.VIDEOS_ES_SEARCH_KEY)
    })
    public void processor(String jsonMsg) {
        try {
            String[] params = jsonMsg.split(RabbitMQConstant._MQ_);
            String options = params[0];
            //modify by zhongli 2020/8/10 转换成枚举操作
            VideoESOptions videoESOptions = VideoESOptions.valueOf(options);
            switch (videoESOptions){
                case videoAdd:
                case videoUpdate:
                case directVideoAdd:
                case videoDelete:{
                    String videoIds = params[1];
                    firstVideoEsItemService.update(videoIds, options);
                    break;
                }
                case preVideoAdd:
                    break;
                case preVideoDelete:
                    break;
                case gatherAddOrDelete:{
                    String gatherId = params[1];
                    String videoIds = params[2];
                    firstVideoEsItemService.updateByGatherId(Long.parseLong(gatherId), videoIds);
                    break;
                }
                case gatherDeleteOrClose:{
                    String gatherId = params[1];
                    //0-关闭,1-删除
                    String state = params[2];
                    firstVideoEsItemService.deleteOrCloseGather(Long.parseLong(gatherId), Integer.parseInt(state));
                    break;
                }
                case deleteDueVideos:{
                    firstVideoEsItemService.deleteDueVideos();
                    break;
                }
                case initVideo:{
                    String sqlBuffer = params[1];
                    firstVideoEsItemService.init(sqlBuffer);
                    break;
                }
                default:
            }
        } catch (Exception e) {
            log.error(StringUtils.trimToEmpty(e.getMessage()).concat(" 视频更新ESMQ消费异常,数据 ").concat(jsonMsg),e);
        }
    }
}
