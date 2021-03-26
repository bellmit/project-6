package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.UserBuryingPointPush;
import com.miguan.ballvideo.vo.GarbageBuryingPointVo;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;

public interface UserBuriedPointService {

    ResultMap insert(UserBuryingPointVo userBuryingPointVo);

    Integer judgeUser(String deviceId,String channelId);

    Integer deleteByDeviceId(String deviceId);

    void savePushBuryingPoint(UserBuryingPointPush buryingPointPush);

    void saveEntryGarbageBuryingPoint(String jsonString);

    /**
     * 组装进入垃圾清理埋点MQ队列数据
     */
    void convertAndSendGarbageBuryingPoint(GarbageBuryingPointVo garbageBuryingPointVo);
}
