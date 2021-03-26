package com.miguan.recommend.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.CatRecommendDto;
import com.miguan.recommend.common.aop.Base;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.recommend.CatRecommendService;
import com.miguan.recommend.vo.ResultMap;
import com.miguan.recommend.vo.VideoRecommendVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Api(value = "分类推荐", tags = {"视频推荐"})
@RestController
@RequestMapping("/api/cat")
public class CatRecommendController {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource
    private CatRecommendService catRecommendService;

    @ApiOperation("分类视频推荐接口")
    @PostMapping("/recommend")
    public ResultMap<VideoRecommendVo> recommend(@ModelAttribute CatRecommendDto recommendDto, @Base BaseDto baseDto) {
        log.info("{} {}分类推荐 sensitiveState:{}", baseDto.getUuid(), recommendDto.getCatid(), recommendDto.getSensitiveState());
        Long catFlushNum = redisDB0Service.incr(String.format(RedisRecommendConstants.user_cat_rec_flush, baseDto.getUuid()), ExistConstants.one_day_seconds);
        baseDto.setCatFlushNum(catFlushNum);

        VideoRecommendVo recommendVo = catRecommendService.recommend(baseDto, recommendDto.getCatid(), recommendDto.getSensitiveState(), recommendDto.getNum());
        log.info("{} {}分类推荐结果>>{}", baseDto.getUuid(), recommendDto.getCatid(), JSONObject.toJSONString(recommendVo));
        return ResultMap.success(recommendVo);
    }
}
