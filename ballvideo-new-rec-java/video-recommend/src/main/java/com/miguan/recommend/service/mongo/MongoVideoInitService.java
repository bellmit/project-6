package com.miguan.recommend.service.mongo;

import com.miguan.recommend.bo.VideoInitDto;

public interface MongoVideoInitService {

    /**
     * 初始化mongodb权重信息(慎重：此接口会先删除全部数据，在重新同步)
     */
    public void hotspotInit();

    /**
     * 发送清洗视频消息到MQ
     */
    public void sendWashHotspotVideoMsgToMQ();

    /**
     * 发送清洗激励视频消息到MQ
     */
    public void sendWashIncentiveVideoMsgToMQ();

    /**
     * 清洗激励视频
     */
    public void doWashIncentiveVideo(VideoInitDto initDto);

    /**
     * 清洗视频
     */
    public void doWashHotspotVideo(VideoInitDto initDto);

    /**
     * 删除全部普通视频权重信息
     */
    public void deleteAllHotspot();

    /**
     * 删除全部激励视频权重信息
     */
    public void deleteAllIncentiveHotspot();

    /**
     * 更新视频
     * @param videoIds 视频ID，多个逗号间隔
     * @param isPopUpOnline 是否新上线
     */
    public void updateHotspot(String videoIds, boolean isPopUpOnline);

    /**
     * 更新视频
     * @param videoId 视频ID
     * @param weights 权重
     * @param weights1 权重1
     */
    public void updateHotspot(String videoId, double weights, double weights1);

    /**
     * 更新视频状态
     * @param videoIds 视频ID，多个逗号间隔
     */
    public void updateHotspotState(String videoIds, Integer state);

    /**
     * 更新视频专辑状态
     * @param videoIds 视频ID，多个逗号间隔
     */
    public void updateHotspotAlbumState(String videoIds);

    /**
     * 初始化评分
     */
    public void initScore();

    /**
     * 发送初始化视频置信度前5的标签ID消息到MQ
     */
    public void sendVideoTagTop5IdsToMQ();

    /**
     * 处理初始化视频置信度前5的标签ID MQ消息
     */
    public void doVideoTagTop5Ids(VideoInitDto initDto);

    /**
     * 更新视频置信度前5的标签ID
     * @param videoId
     */
    public void updateVideoTagTop5Ids(Integer videoId);
}
