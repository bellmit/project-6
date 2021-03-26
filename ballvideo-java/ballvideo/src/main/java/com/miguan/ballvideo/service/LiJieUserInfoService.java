package com.miguan.ballvideo.service;

public interface LiJieUserInfoService {

    /**
     * 删除设备信息
     * @return
     */
    void deleteUserInfo();

    /**
     * 根据设备号删除账号信息
     * @return
     */
    int deleteUserInfo(String deviceId,String type);
}
