package com.miguan.laidian.service;


import com.miguan.laidian.entity.AutoPushConfig;
import com.miguan.laidian.vo.AutoPushInfo;
import com.miguan.laidian.vo.ClDeviceVo;
import java.util.List;
import java.util.Map;

/**
 * 自动配置Service
 * @author laiyudan
 * @date 2020-04-24
 */
public interface AutoPushService {

    void batchPush(List<AutoPushInfo> autoPushInfos, AutoPushConfig autoPushConfig);

    //随机文案处理
    Map<String, List<String>> autoPushContentInfo(List<String> distinctIds, Integer titleType);


    List<ClDeviceVo> findAllTokensByDistinct(String appPackage, List<String> distinctIds);
    /**
     * 判断用户是否完成活动任务
     * @param deviceId
     * @param eventType
     * @param activityType
     * @return
     */
    boolean isFinishActivity(String deviceId, Integer eventType, Integer activityType);

    /**
     * 是否推送过签到数据
     * @param deviceId
     * @return
     */
    boolean isPushSignIn(String deviceId);

    void pushTokenInfo(String appPackage, List<AutoPushInfo> autoPushInfos, Long videoId, List<String> distinctIds, List<ClDeviceVo> clDeviceVos);

    /**
     * 过滤今日推送总数已达6次用户
     */
    List<String> filterPushDevice(List<ClDeviceVo> clDeviceVos);

    /**
     * 由于是集群，所有不能让内存内所有定时器去触发推送
     * @param configId
     */
    void saveAutoPushRedis(String configId);

    boolean hasAutoPushRedis(String configId);
}
