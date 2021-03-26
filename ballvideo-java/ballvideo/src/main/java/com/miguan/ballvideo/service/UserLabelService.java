package com.miguan.ballvideo.service;


import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.vo.QueryParamsVo;
import com.miguan.ballvideo.vo.video.UserLabelVo;

/**
 * 用户标签Service
 * @author laiyudan
 * @date 2019-10-22
 */
public interface UserLabelService {

    /**
     * 更新用户标签
     * @param params
     * @return
     */
    UserLabel updateUserLabelInfo(QueryParamsVo params);

    /**
     * 更新用户标签 1.8.0版本
     * @return
     */
    UserLabel calculateCatIdsSort(UserLabel userLabel, String appPackage);

/*    *//**
     * 批量更新老用户的视频标签 1.8.0版本
     * @return
     *//*
    int updateUserLabelInfo();*/

    /**
     * 根据设备ID查询用户标签信息
     * @param deviceId
     * @return
     */
    UserLabel findTopByDeviceId(String deviceId);

    /**
     * 保存或更新用户标签信息
     * @param userLabel
     * @return
     */
    UserLabel saveToMQ(UserLabel userLabel);

    void initUserLabel(String deviceId, String channelId, String appPackage);

    UserLabel createIOSUserLabel(String deviceId, String channelId, String appPackage);

    UserLabel getUserLabelByDeviceId(String deviceId);
    //1.8
    UserLabelVo getUserLabelVoByDeviceId(String deviceId, String appPackage);

    /**
     * 更新用户标签缓存
     * @param userLabel
     * @return
     */
    UserLabelVo updateUserLabelByRedis(UserLabel userLabel);
    UserLabelVo updateDBAndRedis(UserLabel userLabel);
    UserLabel saveToDB(UserLabel userLabel);

    void updateIdFromRedis(UserLabel vo);

    void deleteUserLabelDatas();

    void deleteUserLabel(int loop,String tableName1,String tableName2);
}
