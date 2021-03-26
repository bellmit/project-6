package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.recommend.AbstractRecommendService;
import com.miguan.recommend.service.recommend.CatRecommendService;
import com.miguan.recommend.service.recommend.PredictService;
import com.miguan.recommend.service.recommend.VideoHotService;
import com.miguan.recommend.vo.VideoRecommendVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class CatRecommendServiceImpl implements CatRecommendService {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private VideoHotService<VideoHotspotVo> videoHotServiceV3;
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource
    private PredictService predictService;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public VideoRecommendVo recommend(BaseDto baseDto, Integer catId, Integer sensitive, Integer num) {
        List<String> recVideoList = null;
        UserFeature userFeature = baseDto.getUserFeature();
        if (StringUtils.equals("2", baseDto.getOldUserOptimizeGroup()) && (userFeature.getShowCount() < 35 || userFeature.getPlayCount() < 4)) {
            recVideoList = this.getTopVideo(baseDto.getUuid(), catId, num);
            if (!CollectionUtils.isEmpty(recVideoList) && recVideoList.size() == num) {
                // 将返回的视频缓存记录，避免短时间内重复推荐
                bloomFilterService.putAll(baseDto.getUuid(), recVideoList);
                return new VideoRecommendVo(recVideoList, null, null);
            }
        }

        long recStart = System.currentTimeMillis();
        VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, catId, sensitive, num);
        if (baseDto.isVideo98Group()) {
            queryDto.setExcludedSource(AbstractRecommendService.excludeSource);
        }
        List<VideoHotspotVo> queryList = videoHotServiceV3.findAndFilter(queryDto, null);
        if (CollectionUtils.isEmpty(queryList)) {
            log.info("{} {}分类推荐 查询到视频0个", baseDto.getUuid(), catId);
            return null;
        } else {
            log.info("{} {}分类推荐 查询到视频{}个", baseDto.getUuid(), catId, queryList.size());
        }

        Map<String, Integer> videoCatMap = queryList.stream().collect(Collectors.toMap(VideoHotspotVo::getVideo_id, VideoHotspotVo::getCatid, (e, e1) -> e));
        Map<String, BigDecimal> videoPlayRates = predictService.getVideoListPlayRate(baseDto, queryList);
        recVideoList = predictService.videoTopK(videoPlayRates, videoCatMap, num, num, 20);
        // 将返回的视频缓存记录，避免短时间内重复推荐
        bloomFilterService.putAll(baseDto.getUuid(), recVideoList);
        log.debug("{} {}分类推荐 总耗时：{}", baseDto.getUuid(), catId, System.currentTimeMillis() - recStart);
        return new VideoRecommendVo(recVideoList, null, null);
    }


    private List<String> getTopVideo(String uuid, Integer catId, Integer getNum) {
        String topVideo = redisDB0Service.get(RedisRecommendConstants.yesterday_top_video + catId);
        List<String> videoList = JSONObject.parseArray(topVideo, String.class);
        if (isEmpty(videoList)) {
            log.info("{} 分类次新用户推荐视频为空", uuid);
            return null;
        } else {
            log.info("{} 分类次新用户推荐视频{}个", uuid, videoList.size());
        }
        // 校验视频是否上线运营
        List<VideoHotspotVo> onlineVideoList = videoHotspotService.findFromMongoById(videoList);
        videoList = onlineVideoList.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());
        if (isEmpty(onlineVideoList)) {
            log.info("{} 次新用户推荐, 视频全部下线，执行正常推荐逻辑", uuid);
            return null;
        }
        // 曝光过滤
        List<String> recVideoList = bloomFilterService.containMuilSplit(getNum, uuid, videoList);
        return recVideoList;
    }


}
