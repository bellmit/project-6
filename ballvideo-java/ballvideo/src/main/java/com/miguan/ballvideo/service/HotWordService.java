package com.miguan.ballvideo.service;

import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.video.HotWordVideoVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface HotWordService {

    /**
     * 获取百度网址的当日前10热词
     * @param editor
     */
    void getBaiduHotWord(String editor);

    /**
     * 获取权重值前10的热词
     * @param channelId
     * @param appVersion
     * @return
     */
    List<String> findHotWordInfo(String channelId, String appVersion, String mobileType);

    /**
     * 刷新redis热词缓存
     */
    void freshHotWordInfo();

    /**
     * 查询热词关联的视频列表
     * @param title
     * @return
     */
    @Cacheable(value = CacheConstant.QUERY_HOT_WORD_VIDEO_ID)
    List<HotWordVideoVo> findHotWordVideoInfo(String title);
}
