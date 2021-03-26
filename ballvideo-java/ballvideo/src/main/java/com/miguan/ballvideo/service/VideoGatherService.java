package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.VideoGatherParamsDto;
import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by shixh on 2020/1/10.
 */
public interface VideoGatherService {

  /**
   * 集合视频查询
   * @param gatherId
   * @return
   */
    List<Videos161Vo> getVideos(Long gatherId,Long totalWeight);

    Object getVideos(Long gatherId, CommonParamsVo params);

    Object getVideos(Long gatherId);

    Object getVideos(String deviceId,Long gatherId,Long totalWeight,Long videoId,String step);

    Object getDefaultVideos(AbTestAdvParamsVo queueVo, String userId, VideoGatherParamsDto params);

    int countByGatherId(Long gatherId);

    @Cacheable(value = CacheConstant.GET_BY_GATHERID, unless = "#result == null")
    VideoGather getByGatherId(Long gatherId);

    VideoGatherVo getVideoGatherVoByGatherId(Long gatherId,boolean includeSearchData);

    VideoGatherVo getVideoGatherVo(Videos161Vo vo);

    VideoGatherVo getCurrentVideoGatherVo(Videos161Vo vo);

    ResultMap refreshVideosByGatherId(Long gatherId);
}
