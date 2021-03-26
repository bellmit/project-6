package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.SaveLabelInfo;

public interface InterestLabelService {

    /**
     * 保存兴趣标签
     * @param labelInfo
     * @param publicInfo
     * @return
     */
    int saveLabelInfo(SaveLabelInfo labelInfo,String publicInfo);

    /**
     * 是否下发兴趣标签
     * @param deviceId
     * @param channelId
     * @return
     */
    boolean getLabelInfo(String deviceId, String channelId);
}
