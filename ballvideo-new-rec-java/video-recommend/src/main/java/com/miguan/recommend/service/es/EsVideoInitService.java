package com.miguan.recommend.service.es;

import com.miguan.recommend.bo.VideoInitDto;
import com.miguan.recommend.vo.RecVideosVo;

import java.util.List;

public interface EsVideoInitService {

    /**
     * 发送视频标题初始化消息到MQ
     */
    public void sendVideoTitleInitToMQ();

    /**
     * 视频标签初始化消息处理
     * @param initDto
     */
    public void doVideoTitleInitToMQ(VideoInitDto initDto);

    /**
     * 批量视频标题初始化
     * @param videoIds
     */
    public void batchVideoTitleInitById(List<String> videoIds);

    /**
     * 批量视频标题初始化
     * @param recVideosVoList
     */
    public void batchVideoTitleInit(List<RecVideosVo> recVideosVoList);

    /**
     * 视频标题初始化
     * @param recVideosVo
     */
    public void videoTitleInit(RecVideosVo recVideosVo);
}
