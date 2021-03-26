package com.miguan.ballvideo.common.util.video;

import com.miguan.ballvideo.vo.video.FirstVideoDetailVo;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import com.miguan.ballvideo.vo.video.FirstVideosVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCacheUtils {

    public static volatile Map<String, Object> videoMap=new HashMap<>();

    public static Object getObject(String key) {
        return videoMap != null ? videoMap.get(key) : null;
    }

    /**
     * 初始化首页视频缓存信息
     */
    public static void initVideoCache(String mapKey, FirstVideos161Vo firstVideos161Vo) {
        Object videoCache = VideoCacheUtils.getObject(mapKey);
        if(null!=videoCache){
            return;
        }
        if(null ==firstVideos161Vo) {
            return;
        }
        List<FirstVideosVo> firstVideosVos = firstVideos161Vo.getFirstVideosVos();
        if (CollectionUtils.isNotEmpty(firstVideosVos)) {
            videoMap.put(mapKey, firstVideos161Vo);
        }
    }

    /**
     * 初始化详情页列表缓存信息
     */
    public static void initVideoCache(String mapKey, FirstVideoDetailVo firstVideoDetailVo) {
        Object videoCache = VideoCacheUtils.getObject(mapKey);
        if(null!=videoCache){
            return;
        }
        if(null ==firstVideoDetailVo) {
            return;
        }
        List<Videos161Vo> videos161Vos = firstVideoDetailVo.getVideos();
        if (CollectionUtils.isNotEmpty(videos161Vos)) {
            videoMap.put(mapKey, firstVideoDetailVo);
        }
    }
}
