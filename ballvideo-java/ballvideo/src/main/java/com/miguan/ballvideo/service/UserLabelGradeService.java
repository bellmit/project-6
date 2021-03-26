package com.miguan.ballvideo.service;

public interface UserLabelGradeService {

    /**
     * 更新用户标签分
     * @param deviceId
     * @param catId
     * @param actionId
     */
    void updateUserLabelGrade(String deviceId, Long catId, String actionId);
}
