package com.miguan.ballvideo.service;

/**
 * 设备视频记录
 */
public interface DeviceVideoLogService {

    /**
     * 保存视频信息到Redis
     * @param deviceId 设备Id
     * @param videoId 视频Id
     */
    void saveVideoInfoToRedis(String deviceId, Long videoId);

    /**
     * 记录设备展示视频记录到Redis
     *
     */
    public void saveDeviceCatShowIdToRedis(String deviceId, Long catId, Long videoId);

    public void saveCtrShowToRedis(String deviceId,Long videoId);

    public void saveCtrClickToRedis(String deviceId,Long videoId);
}
