package com.miguan.recommend.controller;

import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoInitDto;
import com.miguan.recommend.bo.VideoRelavantRecommendDto;
import com.miguan.recommend.common.constants.MongoConstants;
import com.miguan.recommend.entity.mongo.FullLable;
import com.miguan.recommend.entity.mongo.VideoPaddleTag;
import com.miguan.recommend.mapper.FirstVideosMapper;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.es.EsVideoInitService;
import com.miguan.recommend.service.mongo.MongoVideoInitService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import com.miguan.recommend.vo.ResultMap;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/video/init")
public class VideoInitController {

    @Resource
    private FirstVideosMapper firstVideosMapper;
    @Resource
    private MongoVideoInitService mongoVideoInitService;
    @Resource
    private EsVideoInitService esVideoInitService;
    @Autowired
    private BloomFilterService bloomFilterService;
    @Resource(name = "featureMongoTemplate")
    private MongoTemplate featureMongoTemplate;

    @Resource(name = "videoRelevantRecommendService")
    private VideoRecommendService<VideoRelavantRecommendDto> videoRelevantRecommendService;

    @ApiOperation("初始化mongodb权重信息(慎重：此接口会先删除全部数据，在重新同步)")
    @PostMapping("/hotspot")
    public ResultMap hotspot() {
        mongoVideoInitService.hotspotInit();
        return ResultMap.success();
    }

    /**
     * 清洗激励视频
     *
     * @return
     */
    @PostMapping("/wash/incentive")
    public ResultMap washIncentiveVideo() {
        mongoVideoInitService.sendWashIncentiveVideoMsgToMQ();
        return ResultMap.success("操作成功");
    }

    /**
     * 清洗热度视频
     *
     * @return
     */
    @PostMapping("/wash/hotspot")
    public ResultMap washHotspotVideo() {
        mongoVideoInitService.sendWashHotspotVideoMsgToMQ();
        return ResultMap.success("操作成功");
    }

    /**
     * 清洗指定的热度视频
     *
     * @return
     */
    @PostMapping("/wash/hotspot/SomeOne")
    public ResultMap washHotspotVideoById(String videoIds) {
        mongoVideoInitService.updateHotspot(videoIds, false);
        return ResultMap.success("操作成功");
    }

    /**
     * 初始化Es视频标题
     *
     * @return
     */
    @GetMapping("/videoTitle")
    public ResultMap videoTitle() {
        esVideoInitService.sendVideoTitleInitToMQ();
        return ResultMap.success();
    }

    /**
     * 初始化Es视频标题
     *
     * @return
     */
    @GetMapping("/videoTitle/test")
    public ResultMap videoTitleTest() {
        Integer count = firstVideosMapper.count();
        int skip = 0;
        int limit = 1000;
        while (skip < count) {
            VideoInitDto initDto = new VideoInitDto();
            initDto.setType(VideoInitDto.es_video_title_init);
            initDto.setSkip(skip);
            initDto.setSize(limit);
            esVideoInitService.doVideoTitleInitToMQ(initDto);
            skip += limit;
        }
        return ResultMap.success();
    }

    /**
     * 初始化视频百度飞桨置信度前5的标签ID
     *
     * @return
     */
    @GetMapping("/videoTag/top5Ids")
    public ResultMap top5Ids() {
        mongoVideoInitService.sendVideoTagTop5IdsToMQ();
        return ResultMap.success();
    }

    /**
     * 初始化视频百度飞桨置信度前5的标签ID
     *
     * @return
     */
    @GetMapping("/videoTag/top5Ids/test")
    public ResultMap top5IdsTest() {
        Query query = new Query();
        Long count = featureMongoTemplate.count(query, MongoConstants.video_paddle_tag);
        if (count < 1) {
            return ResultMap.success();
        }
        log.debug("初始化视频百度飞桨置信度前5的标签,共计{}个视频", count);
        int skip = 0;
        int limit = 1000;
        while (skip < count) {
            VideoInitDto dto = new VideoInitDto();
            dto.setType(VideoInitDto.video_paddle_tag_top5);
            dto.setSkip(skip);
            dto.setSize(limit);
            mongoVideoInitService.doVideoTagTop5Ids(dto);
            skip += limit;
        }
        return ResultMap.success();
    }

    @PostMapping("/check/bloom")
    public ResultMap checkBloom(String uuid, String videoId) {
        List<String> list = bloomFilterService.containMuil(1, uuid, Lists.newArrayList(videoId));
        if (CollectionUtils.isEmpty(list)) {
            return ResultMap.success("此视频【已在】布隆过滤器里面");
        }
        return ResultMap.success("此视频【不在】布隆过滤器里面");
    }

    /**
     * 初始化head_recommend_video表中的视频评分到video_hotspot表
     *
     * @return
     */
    @GetMapping("/score")
    public ResultMap score() {
        mongoVideoInitService.initScore();
        return ResultMap.success();
    }

    @GetMapping("/videoTagEntity")
    public ResultMap videoTagEntity(Integer videoId){
        Query query = new Query();
        query.addCriteria(Criteria.where("video_id").is(videoId));
        List<VideoPaddleTag> tag = featureMongoTemplate.find(query, VideoPaddleTag.class, MongoConstants.video_paddle_tag);


        List<Integer> top5Ids = tag.get(0).getFull_lable().stream().sorted((t1, t2)->{
            return t2.getProbability().compareTo(t1.getProbability());
        }).map(FullLable::getClass_id).limit(5).collect(Collectors.toList());
        return ResultMap.success(top5Ids);
    }
}
