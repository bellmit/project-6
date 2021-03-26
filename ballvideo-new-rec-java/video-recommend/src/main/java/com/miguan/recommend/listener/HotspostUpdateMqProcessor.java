package com.miguan.recommend.listener;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.common.constants.RabbitMqConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.common.enums.VideoESOptions;
import com.miguan.recommend.service.mongo.MongoVideoInitService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@EnableRabbit
@Component
public class HotspostUpdateMqProcessor {

    @Resource
    private MongoVideoInitService mongoVideoInitService;

    @RabbitListener(autoStartup = "#{environment['spring.rabbitmq.open']}", bindings = {
            @QueueBinding(value = @Queue(value = RabbitMqConstants.VIDEO_REC_QUEUE),
                    exchange = @Exchange(value = RabbitMqConstants.VIDEO_REC_EXCHANGE, autoDelete = "true"),
                    key = RabbitMqConstants.VIDEO_REC_KEY)
    })
    public void processor(String jsonMsg) {
        String[] params = jsonMsg.split(RabbitMqConstants._MQ_);
        String options = params[0];
        String videos = params[1];
        if(StringUtils.isNotEmpty(videos)){
            videos = videos.replaceAll("\n", "");
            videos = videos.replaceAll("\r", "");
            videos = videos.replaceAll(" ", "");
            if(videos.startsWith(SymbolConstants.comma)){
                videos = videos.substring(1);
            }
            if(videos.endsWith(SymbolConstants.comma)){
                videos = videos.substring(0, videos.length() - 1);
            }
        }
        VideoESOptions videoESOptions = VideoESOptions.valueOf(options);
        log.warn("视频操作>>{}", JSONObject.toJSONString(params));
        switch (videoESOptions) {
            case videoAdd:
            case directVideoAdd:
                mongoVideoInitService.updateHotspot(videos, true);
                break;
            case videoUpdate:
                mongoVideoInitService.updateHotspot(videos, false);
                break;
            case videoDelete:
                mongoVideoInitService.updateHotspotState(videos, XyConstants.close);
                break;
//            case preVideoAdd:
//                break;
//            case preVideoDelete:
//                break;
//            case gatherAddOrDelete: {
//                String gatherId = params[1];
//                String videoIds = params[2];
//                mongoVideoInitService.updateByGatherId(Long.parseLong(gatherId), videoIds);
//                break;
//            }
//            case gatherDeleteOrClose: {
//            }
//            case deleteDueVideos: {
//                mongoVideoInitService.deleteExpiredVideos();
//                break;
//            }
            case albumIdUpdate:
                mongoVideoInitService.updateHotspotAlbumState(videos);
                break;
            case bdkafkaUnSearchVideo:
//                String[] msg = videos.split(":");
//                mongoVideoInitService.updateHotspot(msg[0], Double.parseDouble(msg[1]), Double.parseDouble(msg[2]));
                break;
            default:
                log.info("视频[{}]操作>>{}, 未定义具体业务处理，执行通用的更新操作", params[1], options);
                mongoVideoInitService.updateHotspot(videos, false);
        }
    }

}
