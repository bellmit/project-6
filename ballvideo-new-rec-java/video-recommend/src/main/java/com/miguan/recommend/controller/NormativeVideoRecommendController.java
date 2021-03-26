package com.miguan.recommend.controller;

import com.miguan.recommend.bo.NormativeIncentiveVideoRecommendDto;
import com.miguan.recommend.bo.NormativeVideoRecommendDto;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.service.recommend.FeatureService;
import com.miguan.recommend.service.recommend.NormativeVideoRecommendService;
import com.miguan.recommend.vo.NormativeVideoRecommendVo;
import com.miguan.recommend.vo.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "通用视频推荐相关接口", tags = {"通用推荐"})
@RestController
@RequestMapping("/api/normative")
public class NormativeVideoRecommendController {

    @Resource
    private FeatureService featureService;
    @Resource(name = "normativeVideoRecommendServiceV1")
    private NormativeVideoRecommendService normativeVideoRecommendService;
    @Resource(name = "normativeIncentiveVideoRecommendService")
    private NormativeVideoRecommendService normativeIncentiveVideoRecommendService;

    @ApiOperation("通用视频推荐")
    @PostMapping("/video/recommend")
    public ResultMap<NormativeVideoRecommendVo> videoRecommend(@RequestBody NormativeVideoRecommendDto recommendDto) {
        PredictDto predictDto = new PredictDto();
        BeanUtils.copyProperties(recommendDto, predictDto);
        predictDto.set_first(Boolean.parseBoolean(recommendDto.getIs_first()));
        predictDto.set_first_app(Boolean.parseBoolean(recommendDto.getIs_first_app()));

        UserFeature userFeature = featureService.initUserFeature(predictDto.getDevice_id(), recommendDto.getIp());
        predictDto.setUserFeature(userFeature);

        NormativeVideoRecommendVo recommendVo = normativeVideoRecommendService.recommend(predictDto, recommendDto);
        return ResultMap.success(recommendVo);
    }

    @ApiOperation("通用激励视频推荐")
    @PostMapping("/video/incentive/recommend")
    public ResultMap<NormativeVideoRecommendVo> videoIncentiveRecommend(@RequestBody NormativeIncentiveVideoRecommendDto recommendDto) {
        PredictDto predictDto = new PredictDto();
        BeanUtils.copyProperties(recommendDto, predictDto);
        predictDto.set_first(Boolean.parseBoolean(recommendDto.getIs_first()));
        predictDto.set_first_app(Boolean.parseBoolean(recommendDto.getIs_first_app()));
        NormativeVideoRecommendVo recommendVo = normativeIncentiveVideoRecommendService.recommend(predictDto, recommendDto);
        return ResultMap.success(recommendVo);
    }


}
