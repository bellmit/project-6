package com.miguan.recommend.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.*;
import com.miguan.recommend.common.aop.Base;
import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.common.interceptor.BaseDtoArgumentResolver;
import com.miguan.recommend.common.util.Global;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import com.miguan.recommend.service.xy.ClInterestLabelService;
import com.miguan.recommend.service.xy.NewUserSelectionService;
import com.miguan.recommend.vo.ResultMap;
import com.miguan.recommend.vo.VideoRecommendVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Api(value = "首页推荐", tags = {"茜柚视频推荐"})
@RestController
@RequestMapping("/api/video")
public class VideoRecommendController {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "specialRecommendService")
    private VideoRecommendService specialRecommendService;
    @Resource(name = "videoRecommendServiceV3")
    private VideoRecommendService videoRecommendServiceV3;
    @Resource(name = "oldUserOfNewRecommendService")
    private VideoRecommendService oldUserOfNewRecommendService;
    @Resource(name = "videoDetailRecommendService")
    private VideoRecommendService videoDetailRecommendService;
    @Resource(name = "videoRelevantRecommendService")
    private VideoRecommendService videoRelevantRecommendService;
    @Resource
    private NewUserSelectionService newUserSelectionService;

    private static final int defaultVideoNum = 7;

    @ApiOperation(value = "首页视频推荐接口")
    @PostMapping("/recommend")
    public ResultMap<VideoRecommendVo> recommend(@Base BaseDto baseDto, @ModelAttribute VideoRecommendDto recommendDto) {
        if (isEmpty(recommendDto.getDefaultCat())) {
            ResultMap.error("参数: defaultCat 不能为空");
        }
        if (recommendDto.getIncentiveVideoNum() == null) {
            ResultMap.error("参数: incentiveVideoNum 不能为空");
        }
        if (recommendDto.getVideoNum() == null) {
            recommendDto.setVideoNum(defaultVideoNum);
        }

        List<Integer> defaultCats = new ArrayList<Integer>(2);
        String[] defaultCatStr = recommendDto.getDefaultCat().split(SymbolConstants.comma);
        for (String defaultCat : defaultCatStr) {
            defaultCats.add(Integer.parseInt(defaultCat));
        }
        recommendDto.setDefaultCatList(defaultCats);
        // 记录用户刷新次数
        Long flushNum = redisDB0Service.incr(String.format(RedisRecommendConstants.user_flush_num, baseDto.getUuid()), ExistConstants.thirty_days_seconds);
        log.info("{} 视频推荐第{}刷", baseDto.getUuid(), flushNum);
        baseDto.setFlushNum(flushNum);

        if (StringUtils.isNotBlank(recommendDto.getExcludeCat())) {
            String[] excludeCat = recommendDto.getExcludeCat().split(SymbolConstants.comma);
            List<Integer> excludeCatList = Stream.of(excludeCat).map(Integer::valueOf).collect(Collectors.toList());
            recommendDto.setExcludeCatList(excludeCatList);
        }

        long recStart = System.currentTimeMillis();

        String specialUser = Global.getValue("special_video_white_user");
        boolean isSpecialUser = isEmpty(specialUser) ? false : specialUser.contains(baseDto.getUuid());
        log.info("{} {}专题白名单推荐用户", baseDto.getUuid(), isSpecialUser ? "属于" : "不属于");
        if(isSpecialUser || baseDto.publicInfo.isNewApp()){
            int specialFlushNum = Global.getInt("special_video_flush_num");
            if (isSpecialUser || (flushNum <= specialFlushNum && !StringUtils.equals(BaseDtoArgumentResolver.EMPTY_GROUP, baseDto.getSpecialGroup()))) {
                if (isSpecialUser && flushNum > specialFlushNum) {
                    log.info("{} 专题白名单推荐用户已刷完所有专题视频，开始重置", baseDto.getUuid());
                    Long initFlushNum = flushNum % specialFlushNum;
                    baseDto.setFlushNum(initFlushNum == 0 ? specialFlushNum : initFlushNum);
                }
                // 专题推荐
                specialRecommendService.recommend(baseDto, recommendDto);

                // 判断是否满足条件（7个热度视频，1个激励视频）
                // 如果不满足，通过正常推荐逻辑补足
                int selectedVideoNum = isEmpty(recommendDto.getSelectedVideo()) ? 0 : recommendDto.getSelectedVideo().size();
                int jiVideoNum = isEmpty(recommendDto.getJlvideo()) ? 0 : recommendDto.getJlvideo().size();
                if (selectedVideoNum == defaultVideoNum && jiVideoNum == recommendDto.getIncentiveVideoNum()) {
                    VideoRecommendVo vo = new VideoRecommendVo(recommendDto);
                    log.info("{} 视频推荐返回结果：{}", baseDto.getUuid(), JSONObject.toJSONString(vo));
                    return ResultMap.success(vo);
                }
                recommendDto.setVideoNum(recommendDto.getVideoNum() - selectedVideoNum);
            }
            log.debug("{} 用户使用了新推荐0.3,CVR分组:{}", baseDto.getUuid(), baseDto.getCvrGroup());
            videoRecommendServiceV3.recommend(baseDto, recommendDto);
        } else {
            UserFeature userFeature = baseDto.getUserFeature();
            // 如果用户是老用户推荐优化实验2组，并且曝光数小于35或播放数小于4
            // 执行次新用户推荐逻辑
            if(StringUtils.equals("2", baseDto.getOldUserOptimizeGroup()) && (userFeature.getShowCount() < 35 || userFeature.getPlayCount() < 4)){
                log.info("{} 用户使用了次新用户推荐", baseDto.getUuid());
                oldUserOfNewRecommendService.recommend(baseDto, recommendDto);
            }

            // 如果推荐视频为空，执行原有推荐逻辑
            if(CollectionUtils.isEmpty(recommendDto.getRecommendVideo())){
                log.debug("{} 用户使用了新推荐0.3,CVR分组:{}", baseDto.getUuid(), baseDto.getCvrGroup());
                videoRecommendServiceV3.recommend(baseDto, recommendDto);
            }
        }
        log.info("{} 视频推荐总耗时：{}", baseDto.getUuid(), System.currentTimeMillis() - recStart);
        VideoRecommendVo vo = new VideoRecommendVo(recommendDto);
        log.info("{} 视频推荐返回结果：{}", baseDto.getUuid(), JSONObject.toJSONString(vo));
        return ResultMap.success(vo);
    }

    private VideoRecommendVo firstFlush(BaseDto baseDto, VideoRecommendDto recommendDto) {
        // 如果是指定首刷用户，根据客户端渠道，执行相应的主标签首刷
        boolean isSpecifiesFirstFlushUUid = false;
        String specifiesFirstFlushUuid = Global.getValue("use_recommend_first_flush_uuid");
        if (!isEmpty(specifiesFirstFlushUuid) && specifiesFirstFlushUuid.contains(baseDto.getUuid())) {
            log.info("{} 属于指定的首刷用户", baseDto.getUuid());
            isSpecifiesFirstFlushUUid = true;
        }
        if (isSpecifiesFirstFlushUUid || (baseDto.getPublicInfo().isNewApp() && !isEmpty(baseDto.getAbExp()))) {
            String key = String.format(RedisRecommendConstants.user_first_flush, baseDto.getUuid());
            String value = redisDB0Service.get(key);
            if (isSpecifiesFirstFlushUUid || isEmpty(value)) {
                boolean isRetureFirstFlush = false;
                if (isSpecifiesFirstFlushUUid || StringUtils.equals("2", baseDto.getFirstFlushGroup())) {
                    List<String> firstFlushVideo = newUserSelectionService.getVideoByCatId(recommendDto.getDefaultCatList().get(0), recommendDto.getExcludeCatList());
                    if (isEmpty(firstFlushVideo) || firstFlushVideo.size() < 8) {
                        log.info("{} 推荐属于首刷实验B组, 主标签[{}]首刷列表为空或视频个数小于8，执行正常推荐逻辑", baseDto.getUuid(), recommendDto.getDefaultCatList().get(0));
                    } else {
                        log.info("{} 推荐属于首刷实验B组, 返回主标签[{}]首刷列表>>{}", baseDto.getUuid(), recommendDto.getDefaultCatList().get(0), JSONObject.toJSONString(firstFlushVideo));
                        isRetureFirstFlush = true;
                        recommendDto.setRecommendVideo(firstFlushVideo);
                    }
                } else if (StringUtils.equals("3", baseDto.getFirstFlushGroup())) {
                    List<String> defaultFirstFlushVideo = newUserSelectionService.getDefaultVideo(recommendDto.getExcludeCatList());
                    if (isEmpty(defaultFirstFlushVideo) || defaultFirstFlushVideo.size() < 8) {
                        log.info("{} 推荐属于首刷实验C组, 默认首刷列表为空或视频个数小于8，执行正常推荐逻辑", baseDto.getUuid());
                    } else {
                        log.info("{} 推荐属于首刷实验C组, 返回默认首刷列表>>{}", baseDto.getUuid(), JSONObject.toJSONString(defaultFirstFlushVideo));
                        isRetureFirstFlush = true;
                        recommendDto.setRecommendVideo(defaultFirstFlushVideo);
                    }
                } else {
                    log.info("{} 推荐属于首刷实验A组, 执行正常推荐逻辑", baseDto.getUuid());
                }
                redisDB0Service.set(key, "1", ExistConstants.one_day_seconds);
                if (isRetureFirstFlush) {
                    log.debug("{} 推荐属于首刷实验B或C，返回视频列表>>{}", baseDto.getUuid(), JSONObject.toJSONString(recommendDto.getRecommendVideo()));
                    return new VideoRecommendVo(recommendDto);
                }
            } else {
                log.info("{} 推荐已首刷，执行正常推荐逻辑", baseDto.getUuid());
            }
        }
        return null;
    }


    @ApiOperation(value = "视频详情页推荐接口")
    @PostMapping("/detail/recommend")
    public ResultMap<VideoRecommendVo> detailRecommend(@Base BaseDto baseDto, @ModelAttribute VideoDetailRecommendDto detailRecommendDto) {
        videoDetailRecommendService.recommend(baseDto, detailRecommendDto);
        VideoRecommendVo recommendVo = new VideoRecommendVo(detailRecommendDto.getRecommendVideo());
        log.info("{} 视频详情页推荐返回结果：{}", baseDto.getUuid(), JSONObject.toJSONString(recommendVo));
        return ResultMap.success(recommendVo);
    }

    @ApiOperation(value = "即时相关推荐接口")
    @PostMapping("/relevant/recommend")
    public ResultMap<VideoRecommendVo> detailRecommend(@Base BaseDto baseDto, @ModelAttribute VideoRelavantRecommendDto relevantRecommendDto) {
        videoRelevantRecommendService.recommend(baseDto, relevantRecommendDto);
        return ResultMap.success(new VideoRecommendVo(relevantRecommendDto.getRecommendVideo()));
    }
}
