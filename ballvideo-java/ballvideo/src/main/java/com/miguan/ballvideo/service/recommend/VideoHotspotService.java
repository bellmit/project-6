package com.miguan.ballvideo.service.recommend;

/**
 * @Description 处理mongodb中的激励视频和普通视频的权重表
 * @Author zhangbinglin
 * @Date 2020/8/13 15:18
 **/
public interface VideoHotspotService {

    void addOrUpdateHotspot(String videoId, boolean isIncentive, Integer catid, Integer state, Integer collectionId, String onLinedate, String videoTime, Double weight, Double weight1);

    /**
     * 保存普通视频权重
     * @param videoId  视频id
     * @param catid  分类id
     * @param state  状态： 0 = 禁用 1 = 启用
     * @param collectionId  合集id
     */
    void updateHotspot(String videoId, Integer catid, Integer state, Integer collectionId);

    /**
     * 保存激励视频权重
     * @param videoId  视频id
     * @param catid  分类id
     * @param state  状态： 0 = 禁用 1 = 启用
     * @param collectionId  合集id
     */
    void updateIncentiveHotspot(String videoId, Integer catid, Integer state, Integer collectionId);

    /**
     * 设置普通视频权重表的状态为：禁用
     * @param videoId 视频id
     */
    void deleteHotspot(String videoId);

    /**
     * 设置激励视频权重表的状态为：禁用
     * @param videoId
     */
    void deleteIncentiveHotspot(String videoId);

    /**
     * 初始化mongodb权重信息(慎重：此接口会先删除全部数据，在重新同步)
     */

    void hotspotInit();

    /**
     * 清洗激励视频
     */
    void washIncentiveVideo();
}
