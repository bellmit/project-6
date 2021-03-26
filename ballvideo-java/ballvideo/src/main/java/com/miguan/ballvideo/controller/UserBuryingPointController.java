
package com.miguan.ballvideo.controller;


import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.util.AppPackageUtil;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.rabbitMQ.util.RabbitUtil;
import com.miguan.ballvideo.service.DeviceVideoLogService;
import com.miguan.ballvideo.service.UserBuriedPointService;
import com.miguan.ballvideo.vo.BuryingPointPushVO;
import com.miguan.ballvideo.vo.GarbageBuryingPointVo;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointActivityVo;
import com.miguan.ballvideo.vo.userBuryingPoint.UserBuryingPointVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.miguan.ballvideo.vo.BuryingActionType.TASK_CLICK;
import static com.miguan.ballvideo.vo.BuryingActionType.TASK_PAGEVIEW;

@Slf4j
@RestController
@Api(value = "埋点controller",tags={"用户操作埋点"})
@RequestMapping("/api/userBuryingPoint")
public class UserBuryingPointController {

    private static final String GUANG_DIAN_TONG = "guang_dian_tong";
    private static final String CHUAN_SHAN_JIA = "chuan_shan_jia";

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private DeviceVideoLogService deviceVideoLogService;

    @Resource
    private RabbitUtil rabbitUtil;

    @PostMapping("/insert")
    @ApiOperation(value = "保存埋点")
    public ResultMap insert(@ModelAttribute UserBuryingPointVo userBuryingPointVo) {
        if(userBuryingPointVo==null
                || org.apache.commons.lang3.StringUtils.isBlank(userBuryingPointVo.getActionId())
                || org.apache.commons.lang3.StringUtils.isBlank(userBuryingPointVo.getDeviceId()) ){
            return ResultMap.error("ActionId为空 或者 deviceId为空");
        }
        Integer flag = userBuriedPointService.judgeUser(userBuryingPointVo.getDeviceId(),userBuryingPointVo.getChannelId());

        userBuryingPointVo.setCreatTime(new Date());
        userBuryingPointVo.setCreateDate(DateUtil.parseDateToStr(new Date(), tool.util.DateUtil.DATEFORMAT_STR_002));
        String dataStr = JSON.toJSONString(userBuryingPointVo);

        StringBuilder stringBuilder = new StringBuilder(flag.toString());
        stringBuilder.append(RabbitMQConstant._MQ_);
        stringBuilder.append(dataStr);
        String jsonStr = stringBuilder.toString();
        //往本服务发送消息
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_LABEL_EXCHANGE, RabbitMQConstant.BURYPOINT_LABEL_KEY, dataStr);
        //往埋点服务burypoint发送消息
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE, RabbitMQConstant.BURYPOINT_RUTE_KEY, jsonStr);
        //发送活动埋点队列(砸金蛋)
        String actionId = userBuryingPointVo.getActionId();
        int source = userBuryingPointVo.getSource();
        if((TASK_PAGEVIEW.equals(actionId)&&(source == 30||source == 31))||(TASK_CLICK.equals(actionId)&&(source == 40||source == 41))){
            UserBuryingPointActivityVo activityVo = new UserBuryingPointActivityVo();
            BeanUtils.copyProperties(userBuryingPointVo, activityVo);
            activityVo.setIsNew(flag);
            activityVo.setAppType(AppPackageUtil.getAppType(userBuryingPointVo.getAppPackage()));
            String json = JSON.toJSONString(activityVo);
            rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_ACTIVITY_EXCHANGE, RabbitMQConstant.BURYPOINT_ACTIVITY_RUTE_KEY, json);
        }
        return ResultMap.success();
    }

    /**
     * 判断是不是新老用户
     *
     * @param deviceId
     * @param channelId channelId不能删除，日志记录统计用到
     * @return 10-新用户,20-老用户
     */
    //@UserOperate(name= OperateConstant.checkIsNew,business ="判断是不是新老用户",recordParams=true)
    @GetMapping("/checkIsNew")
    @ApiOperation(value = "判断是不是新老用户")
    public ResultMap checkIsNew(@RequestParam(name = "deviceId") String deviceId,String channelId) {
        if (StringUtils.isEmpty(deviceId)) return ResultMap.error("缺少必填参数:deviceId");
        Integer newOrOld = userBuriedPointService.judgeUser(deviceId,channelId);
        return ResultMap.success(newOrOld, "10-新用户,20-老用户");
    }

    /**
     * 删除埋点当前手机的数据，将老用户变成新用户。
     *
     * @param deviceId
     * @return
     */
    @GetMapping("/deleteByDeviceId")
    @ApiOperation(value = "删除埋点当前手机的数据")
    public ResultMap deleteByDeviceId(@RequestParam(name = "deviceId") String deviceId) {
        if (StringUtils.isEmpty(deviceId)) return ResultMap.error("缺少必填参数:deviceId");
        Integer integer = userBuriedPointService.deleteByDeviceId(deviceId);
        return ResultMap.success(integer);
    }

    @PostMapping("/savePushBuryingPoint")
    @ApiOperation(value = "保存推送埋点")
    public ResultMap savePushBuryingPoint(@ModelAttribute BuryingPointPushVO buryingPointPushVO) {
        if(buryingPointPushVO!=null && org.apache.commons.lang3.StringUtils.isBlank(buryingPointPushVO.getActionId())){
            return ResultMap.error("ActionId为空");
        }
        String dataStr = JSON.toJSONString(buryingPointPushVO);
        //往埋点服务burypoint发送推送埋点消息
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_PUSH_EXCHANGE, RabbitMQConstant.BURYPOINT_PUSH_KEY, dataStr);
        return ResultMap.success();
    }

    /**
     * 保存进入垃圾清理埋点信息
     * @param garbageBuryingPointVo
     * @return
     */
    @PostMapping("/saveEntryGarbageBuryingPoint")
    @ApiOperation(value = "保存进入垃圾清理埋点")
    public ResultMap saveEntryGarbageBuryingPoint(@ModelAttribute GarbageBuryingPointVo garbageBuryingPointVo) {
        if (StringUtil.isBlank(garbageBuryingPointVo.getDeviceId())) {
            return ResultMap.error("设备Id不能为空");
        }
        if (StringUtil.isBlank(garbageBuryingPointVo.getAppPackage())) {
            return ResultMap.error("包名不能为空");
        }
        // MQ数据组装
        userBuriedPointService.convertAndSendGarbageBuryingPoint(garbageBuryingPointVo);
        return ResultMap.success();
    }
}
