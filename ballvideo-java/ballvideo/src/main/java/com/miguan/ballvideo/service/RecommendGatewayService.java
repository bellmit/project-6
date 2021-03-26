package com.miguan.ballvideo.service;

import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;

/**
 * @author zhongli
 * @date 2020-09-03 
 *
 */
public interface RecommendGatewayService {

    FirstVideos161Vo getRecommendVideos(VideoParamsDto dto, String publicInfo, AbTestAdvParamsVo queueVo);
}
