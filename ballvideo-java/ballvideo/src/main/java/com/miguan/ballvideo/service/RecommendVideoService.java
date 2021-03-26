package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.vo.video.FirstVideoDetailVo;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;

import java.util.Map;

/**
 * 推荐接口
 * @author laiyd
 * @date 2020-10-26
 *
 */
public interface RecommendVideoService {
    /**
     * 推荐接口
     * @param dto
     * @param publicInfo
     * @param queueVo
     * @return
     */
    FirstVideos161Vo getRecommendVideoList(VideoParamsDto dto, String publicInfo, String abExp, String abIsol, AbTestAdvParamsVo queueVo);

    /**
     * 首页非推荐接口
     *
     * @param params
     * @return
     */
    FirstVideos161Vo getRecommendOtherVideoList(AbTestAdvParamsVo queueVo, Map<String, Object> params, String publicInfo, String abExp, String abIsol);

    /**
     * 详情页列表接口
     *
     * @param params
     * @return
     */
    FirstVideoDetailVo getRecommendDetailVideoList(AbTestAdvParamsVo queueVo, Map<String, Object> params, String publicInfo);

    /**
     * 是否使用新算法
     * @param publicInfo
     * @return
     */
    boolean isABTestUser(VideoParamsDto dto,String publicInfo, int type);
}
