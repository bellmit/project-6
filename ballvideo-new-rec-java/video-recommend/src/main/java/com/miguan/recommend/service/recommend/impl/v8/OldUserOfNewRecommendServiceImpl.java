package com.miguan.recommend.service.recommend.impl.v8;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.recommend.AbstractRecommendService;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.VideoHotService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "oldUserOfNewRecommendService")
public class OldUserOfNewRecommendServiceImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "redisDB11Service")
    private RedisService redisDB11Service;
    @Resource(name = "videoHotServiceV3")
    private VideoHotService videoHotServiceV3;
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        String topVideo = redisDB0Service.get(RedisRecommendConstants.yesterday_top_video + "all");
        List<String> videoList = JSONObject.parseArray(topVideo, String.class);
        if(isEmpty(videoList)){
            log.info("{} 次新用户推荐视频为空", baseDto.getUuid());
            return;
        } else {
            log.info("{} 次新用户推荐视频{}个", baseDto.getUuid(), videoList.size());
        }
        // 校验视频是否上线运营
        List<VideoHotspotVo> onlineVideoList = videoHotspotService.findFromMongoById(videoList);
        videoList = onlineVideoList.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());
        if(isEmpty(onlineVideoList)){
            log.info("{} 次新用户推荐, 视频全部下线，执行正常推荐逻辑", baseDto.getUuid());
            return;
        }
        // 曝光过滤
        List<String> recVideoList = bloomFilterService.containMuilSplit(recommendDto.getVideoNum(), baseDto.getUuid(), videoList);
        if(isEmpty(recVideoList) || recVideoList.size() < recommendDto.getVideoNum()){
            return;
        }

        List<IncentiveVideoHotspot> jlVideoList = new ArrayList<>();
        CompletableFuture<Integer> jlFuture = super.getIncetiveHotVideo(baseDto, recommendDto, jlVideoList, incentiveVideoHotServiceV3New, executor);
        jlFuture.join();

        // 缓存曝光
        List<String> jlVideoIdList = jlVideoList.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList());
        super.bloomVideo(baseDto.getUuid(),recVideoList, jlVideoIdList, bloomFilterService, executor);

        log.info("次新用户推荐, 此次给uuid({})推荐了{} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                baseDto.getUuid(), recVideoList.size(), JSONObject.toJSONString(recVideoList), jlVideoIdList.size(), JSONObject.toJSONString(jlVideoIdList));
        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setJlvideo(jlVideoIdList);

    }

}
