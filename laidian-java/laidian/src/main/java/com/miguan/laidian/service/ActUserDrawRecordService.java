package com.miguan.laidian.service;

import com.miguan.laidian.entity.ActUserDrawRecord;

import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/22
 */
public interface ActUserDrawRecordService {
    /**
     * 获取用户碎片记录
     * @param deviceId
     * @param activityId
     * @param activityConfigId
     * @return
     */
    List<ActUserDrawRecord> getUserDarwRecord(String deviceId, Long activityId, Long activityConfigId);

    /**
     * 保存记录
     * @param activityId
     * @param activityConfigId
     * @param deviceId
     */
    ActUserDrawRecord saveRecord(Long activityId, Long activityConfigId, String deviceId);

    /**
     * 更新记录状态
     * @param deviceId
     * @param darwRecordId
     * @return
     */
    int updateRecordState(String deviceId, Long darwRecordId);

    /**
     * 更新用户碎片状态为已兑换
     * @param deviceId
     * @param activityConfigId
     * @param debrisReachNum
     */
    int updateUserIsEchangeState(String deviceId, Long activityConfigId, Integer debrisReachNum);
}
