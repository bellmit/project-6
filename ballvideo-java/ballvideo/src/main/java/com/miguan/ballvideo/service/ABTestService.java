package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.AbTestUserVo;

public interface ABTestService {

    /**
     * 根据设备Id查询返回A用户/B用户
     * @param deviceId
     * @return
     */
    AbTestUserVo findABUserInfo(String deviceId, String channelId);
}
