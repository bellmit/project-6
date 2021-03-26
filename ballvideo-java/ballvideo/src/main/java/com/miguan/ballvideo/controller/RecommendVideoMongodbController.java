package com.miguan.ballvideo.controller;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.recommend.VideoHotspotService;
import com.miguan.ballvideo.service.recommend.BloomFilterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "视频mongodb相关接口", tags = {"视频mongodb相关接口"})
@RequestMapping("/api/mongodb")
@RestController
public class RecommendVideoMongodbController {

    @Resource
    private VideoHotspotService videoHotspotService;

    @Autowired
    private BloomFilterService bloomFilterService;

    @ApiOperation("初始化mongodb权重信息(慎重：此接口会先删除全部数据，在重新同步)")
    @PostMapping("/hotspot/init")
    public ResultMap hotspotInit() {
        videoHotspotService.hotspotInit();
        return ResultMap.success();
    }


    @PostMapping("/check/bloom")
    public ResultMap hotspotInit(String uuid, String videoId) {
        List<String> list = bloomFilterService.containMuil(1, uuid, Lists.newArrayList(videoId));
        if (CollectionUtils.isEmpty(list)) {
            return ResultMap.success("此视频【已在】布隆过滤器里面");
        }
        return ResultMap.success("此视频【不在】布隆过滤器里面");
    }

    @PostMapping("/wash/incentive/video")
    public ResultMap washIncentiveVideo() {
        videoHotspotService.washIncentiveVideo();
        return ResultMap.success("操作成功");
    }


}
