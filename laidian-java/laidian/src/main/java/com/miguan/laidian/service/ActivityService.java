package com.miguan.laidian.service;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.vo.*;

import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @date 2020/5/22
 */
public interface ActivityService {
    /**
     * 获取活动页面信息
     * @param commomParams
     * @return
     */
    ActivityPageInfoVo activityPageInfo(CommonParamsVo commomParams);

    /**
     * 获取正在进行中的活动信息
     * @return
     */
    ActActivity getCurActivityInfo(CommonParamsVo commomParams);

    /**
     * 获取活动信息，无活动时状态默认0（方便前端处理）
     * @param commomParams
     * @return
     */
    ActActivity getActivityInfo(CommonParamsVo commomParams);

    /**
     * 完成活动任务
     * @param commomParams
     * @param type
     * @param activityId

     */
    ActivityDrawProductVo finishActivityTask(CommonParamsVo commomParams, Integer type, Long activityId);

    /**
     * 转盘抽奖
     * @param commomParams
     * @param activityId
     * @param source 来源 1转盘抽奖 2任务签到
     * @return
     */
    ActivityDrawProductVo activityDraw(CommonParamsVo commomParams, Long activityId,Integer source);

    /**
     * 领取奖品
     * @param commomParams
     * @param darwRecordId
     */
    void receiveAward(CommonParamsVo commomParams, Long darwRecordId);

    /**
     * 兑换奖品
     * @param commomParams
     * @param activityConfigId
     * @param contastInfo
     * @param rcvrName
     * @param rcvrAddr
     * @param rcvrPhone
     */
    void exchangeAward(CommonParamsVo commomParams, Long activityConfigId, String contastInfo,String rcvrName, String rcvrAddr, String rcvrPhone);

    /**
     * 获取活动参与人数
     * @param commomParams
     * @param type
     * @return
     */
    Map<String, Object> getActivityJoinNum(CommonParamsVo commomParams, String type);

    /**
     * 活动首页滚动播报
     * @param commomParams
     * @return
     */
    List<Map<String,String>> getActivityPageScrollBroadcasts(CommonParamsVo commomParams);

    /**
     * 活动首页任务列表
     * @param commomParams
     * @return
     */
    List<ActivityTaskVo> getActivityTasks(CommonParamsVo commomParams);

    /**
     * 获取签到任务信息
     *
     * @param commomParams
     * @return
     */
    List<ActSignVo> getActSignTask(CommonParamsVo commomParams);

    /**
     *  查询兑奖记录
     *
     * @param activityId
     * @param deviceId
     * @return
     */
    List<ActExchangeRecordVo> queryExchangeRecord(Long activityId, String deviceId);

    /**
     * 根据埋点信息获取当前活动
     *
     * @param ldBuryingPointActivityVo
     * @return
     */
    ActActivity getCurActivity(LdBuryingPointActivityVo ldBuryingPointActivityVo);
}
