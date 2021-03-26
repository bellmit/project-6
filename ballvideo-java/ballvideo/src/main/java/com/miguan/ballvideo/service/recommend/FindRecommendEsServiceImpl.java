package com.miguan.ballvideo.service.recommend;

import com.miguan.ballvideo.entity.es.FirstVideoEsVo;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**推荐视频，使用es查找视频
 **/
@Slf4j
@Service
public class FindRecommendEsServiceImpl {
    @Resource
    private ElasticsearchTemplate esTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    public List<Videos161Vo> list(List<String> videos, String incentiveVideoRate) {
        List<Long> videoIds = videos.stream().map(Long::valueOf).collect(Collectors.toList());
        TermsQueryBuilder termsQuery = new TermsQueryBuilder("id", videoIds);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(termsQuery);
        List<FirstVideoEsVo> list = esTemplate.queryForList(nativeSearchQuery, FirstVideoEsVo.class);
        if (CollectionUtils.isEmpty(list)) {
            log.error("从es中未找到任何的视频数据，ids = {}", videos.toString());
            if (videos != null && videos.size() > 0) {
                String videoIdStr = videos.stream().collect(Collectors.joining(","));
                String json = "videoAdd" + RabbitMQConstant._MQ_ + videoIdStr;
                rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEOS_ES_SEARCH_EXCHANGE, RabbitMQConstant.VIDEOS_ES_SEARCH_KEY, json);
                //log.warn("推荐0.1视频变动更新至权重列表，videoIds = '{}'  options = '{}'", videoIds, "videoAdd");
                rabbitTemplate.convertAndSend(RabbitMQConstant.VIDEO_REC_EXCHANGE, RabbitMQConstant.VIDEO_REC_KEY, json);
            }
            return null;
        }
        double rate = Double.parseDouble(incentiveVideoRate);
        return list.stream().sorted(Comparator.comparingLong(e -> videoIds.indexOf(e.getId()))).map(e -> {
            Videos161Vo videos161Vo = new Videos161Vo();
            BeanUtils.copyProperties(e, videos161Vo);
            videos161Vo.setIncentiveRate(rate);
            videos161Vo.setShowShareCount(videos161Vo.getShowShareCount());
            return videos161Vo;
        }).collect(Collectors.toList());
    }
}
