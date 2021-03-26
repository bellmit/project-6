package com.xiyou.speedvideo.dao;

import com.xiyou.speedvideo.entity.mongo.VideoMcaResult;

/**
 * description:
 *
 * @author huangjx
 * @date 2020/11/5 5:55 下午
 */
public interface VideoMcaResultDao {

    VideoMcaResult saveVideoMcaResult(VideoMcaResult videoMcaResult);

    VideoMcaResult findByVideoId(Integer videoId);

    long updateByVideoId(VideoMcaResult videoMcaResult);

    long removeByVideoId(Integer videoId);

}
