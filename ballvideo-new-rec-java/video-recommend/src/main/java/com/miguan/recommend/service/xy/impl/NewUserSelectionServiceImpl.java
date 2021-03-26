package com.miguan.recommend.service.xy.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.mapper.NewUserSelectionMapper;
import com.miguan.recommend.mapper.VideosCatMapper;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.xy.NewUserSelectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.isEmpty;

@Slf4j
@Service
public class NewUserSelectionServiceImpl implements NewUserSelectionService {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private NewUserSelectionMapper newUserSelectionMapper;
    @Resource
    private VideosCatMapper videosCatMapper;
    @Resource
    private VideoHotspotService videoHotspotService;

    @Override
    public List<String> getVideoByCatId(Integer catId, List<Integer> excludeCatList) {
        List<String> videoList = null;
        String key = String.format(RedisRecommendConstants.first_flush_video, catId);
        String videos = redisDB0Service.get(key);
        if (isEmpty(videos)) {
            videoList = newUserSelectionMapper.selectVideoIdByTagIdOrderBySortAsc(catId);
            if (!isEmpty(videoList)) {
                redisDB0Service.set(key, String.join(SymbolConstants.comma, videoList), ExistConstants.one_day_seconds);
            }
        } else {
            videoList = Lists.newArrayList(videos.split(SymbolConstants.comma));
        }
        this.removeTheVideoIsScreened(videoList, excludeCatList);
        return videoList;
    }

    @Override
    public List<String> getDefaultVideo(List<Integer> excludeCatList) {
        List<String> videoList = null;
        String key = String.format(RedisRecommendConstants.first_flush_video, -1);
        String videos = redisDB0Service.get(key);
        if (isEmpty(videos)) {
            videoList = newUserSelectionMapper.selectVideoIdByTagIdOrderBySortAsc(-1);
            if (!isEmpty(videoList)) {
                redisDB0Service.set(key, String.join(SymbolConstants.comma, videoList), ExistConstants.one_day_seconds);
            }
        } else {
            videoList = Lists.newArrayList(videos.split(SymbolConstants.comma));
        }
        this.removeTheVideoIsScreened(videoList, excludeCatList);
        return videoList;
    }

    @Override
    public void updateVideoByCatId(String catIds) {
        List<String> catIdList = null;
        if (isEmpty(catIds)) {
            catIdList = videosCatMapper.getCatIdsByStateAndType(1, null);
        } else {
            catIdList = Arrays.asList(catIds.split(SymbolConstants.comma));
        }

        catIdList.forEach(e -> {
            redisDB0Service.del(String.format(RedisRecommendConstants.first_flush_video, e));
        });
        redisDB0Service.del(String.format(RedisRecommendConstants.first_flush_video, -1));
    }

    @Override
    public void removeTheVideoIsScreened(List<String> videoIds, List<Integer> excludeCatList) {
        if (isEmpty(excludeCatList) || isEmpty(videoIds)) {
            return;
        }
        log.info("首刷视频移除屏蔽分类：视频ID>>{}, 屏蔽分类>>{}", JSONObject.toJSONString(videoIds), JSONObject.toJSONString(excludeCatList));
        List<VideoHotspotVo> mongoList = videoHotspotService.findFromMongoById(videoIds);
        if(!isEmpty(mongoList)){
            mongoList.forEach(m -> {
                if (excludeCatList.contains(m.getCatid())) {
                    videoIds.remove(m.getVideo_id());
                }
            });
        }
    }
}
