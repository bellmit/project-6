package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.service.ActivityService;
import com.miguan.laidian.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Api(value = "活动controller", tags = {"活动接口"})
@RestController
@RequestMapping("/api/act")
public class ActivityController {
    @Resource
    private ActivityService activityService;

    @ApiOperation("活动页面信息")
    @PostMapping("/activityPageInfo")
    public ResultMap<ActivityPageInfoVo> activityPageInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        ActivityPageInfoVo activityPageInfoVo = activityService.activityPageInfo(commomParams);
        return ResultMap.success(activityPageInfoVo);
    }

    @ApiOperation("获取活动信息")
    @PostMapping("/getActivityInfo")
    public ResultMap<ActActivity> getActivityInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        ActActivity actActivity = activityService.getActivityInfo(commomParams);
        return ResultMap.success(actActivity);
    }

    @ApiOperation("完成活动任务")
    @PostMapping("/finishActivityTask")
//    @ConcurrentLock(lockTime = 1000)
    public ResultMap<ActivityDrawProductVo> finishActivityTask(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                     @ApiParam("活动id") @RequestParam Long activityId,
                                                     @ApiParam("任务类型1签到 2成功设置来电秀 3成功设置来电铃声 4 观看视频  5分享活动") @RequestParam Integer type) {
        ActivityDrawProductVo drawProductVo = activityService.finishActivityTask(commomParams,type,activityId);
        return ResultMap.success(drawProductVo);
    }

    @ApiOperation("转盘抽奖")
    @PostMapping("/activityDraw")
//    @ConcurrentLock(lockTime = 3000) //前端转盘要转5秒
    public ResultMap<ActivityDrawProductVo> activityDraw(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                         @ApiParam("活动id") Long activityId) {
        ActivityDrawProductVo activityConfig = activityService.activityDraw(commomParams,activityId,1);
        return ResultMap.success(activityConfig);
    }

    @ApiOperation("立即领取")
    @PostMapping("/receiveAward")
//    @ConcurrentLock(lockTime = 3000) //前端转盘要转5秒
    public ResultMap receiveAward(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                     @ApiParam("奖品配置id") @RequestParam Long darwRecordId) {
        activityService.receiveAward(commomParams,darwRecordId);
        return ResultMap.success();
    }

    @ApiOperation("兑换奖品")
    @PostMapping("/exchangeAward")
//    @ConcurrentLock(lockTime = 1000)
    public ResultMap<Map> exchangeAward(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                        @ApiParam("奖品配置id") @RequestParam Long activityConfigId,
                                        @ApiParam("联系信息微信号或QQ号") @RequestParam String contastInfo,
                                        @ApiParam("收货人姓名") @RequestParam String rcvrName,
                                        @ApiParam("收货人地址") @RequestParam String rcvrAddr,
                                        @ApiParam("收货人手机号") @RequestParam String rcvrPhone) {
        activityService.exchangeAward(commomParams,activityConfigId,contastInfo,rcvrName,rcvrAddr,rcvrPhone);
        return ResultMap.success();
    }

    @ApiOperation("获取活动参与人数")
    @PostMapping("/getActivityJoinNum")
    public ResultMap<Map<String,Object>> getActivityJoinNum(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                            @ApiParam("操作类型:'0'为定时调用 '1'为手动调用") @RequestParam String type) {
        Map<String,Object> map = activityService.getActivityJoinNum(commomParams,type);
        return ResultMap.success(map);
    }

    @ApiOperation("活动首页滚动播报")
    @PostMapping("/getActivityPageScrollBroadcasts")
    public ResultMap<List<Map<String,String>>> getActivityPageScrollBroadcasts(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        List<Map<String,String>> scrollBroadcasts = activityService.getActivityPageScrollBroadcasts(commomParams);
        return ResultMap.success(scrollBroadcasts);
    }

    @ApiOperation("活动首页任务列表")
    @PostMapping("/getActivityTasks")
    public ResultMap<List<ActivityTaskVo>> getActivityTasks(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        List<ActivityTaskVo> taskVos = activityService.getActivityTasks(commomParams);
        return ResultMap.success(taskVos);
    }

    @ApiOperation("活动签到列表")
    @PostMapping("/getActSignTask")
    public ResultMap<List<ActSignVo>>  getActSignTask(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams){
        List<ActSignVo> signList = activityService.getActSignTask(commomParams);
        return ResultMap.success(signList);
    }

    @ApiOperation("兑奖记录")
    @PostMapping("/queryExchangeRecord")
    public ResultMap<List<ActExchangeRecordVo>>  queryExchangeRecord(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                                     @ApiParam("活动id") Long activityId){
        List<ActExchangeRecordVo> exchangeRecordList = activityService.queryExchangeRecord(activityId,commomParams.getDeviceId());
        return ResultMap.success(exchangeRecordList);
    }
}
