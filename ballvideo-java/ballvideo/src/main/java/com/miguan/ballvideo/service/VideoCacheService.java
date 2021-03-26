package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.AdvertVo;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.SmallVideosVo;
import com.miguan.ballvideo.vo.VideosCatVo;
import com.miguan.ballvideo.vo.video.HotListVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import java.util.List;
import java.util.Map;

/**
 * 用于缓存video相关实现
 * @author xujinbang
 * @date 2019/11/9.
 */
public interface VideoCacheService {

    List<Videos161Vo> getFirstVideos161(Map<String, Object> params,int count);

    List<FirstVideos> getFirstVideos(Map<String, Object> params,int count);

    List<SmallVideosVo> getSmallVideos(Map<String, Object> params,int count);

    List<AdvertVo> getAdvertList(Map<String, Object> param, int count);

    List<AdvertVo> getBaseAdvertList(Map<String, Object> param);

    /**
     * 获取视频分类信息
     * @param type
     * @return
     */
    Map<Long, VideosCatVo> getVideosCatMap(String type);

    void fillParams(List<Videos161Vo> firstVideos);

    /**
     * 根据用户视频关联表判断是否收藏
     *
     * @param firstVideos
     * @param userId
     */
    void getVideosCollection(List<Videos161Vo> firstVideos,String userId);

    /**
     * 热榜根据用户视频关联表判断是否收藏
     *
     * @param hotList
     * @param userId
     */
    void getVideosCollection2(List<HotListVo> hotList, String userId);
}
