package com.miguan.laidian.controller;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.ActActivity;
import com.miguan.laidian.entity.LdBuryingPoint;
import com.miguan.laidian.entity.LdBuryingPointAdditional;
import com.miguan.laidian.entity.LdBuryingPointEvery;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.service.ActivityService;
import com.miguan.laidian.service.UserBuriedPointService;
import com.miguan.laidian.vo.LdBuryingPointActivityVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Api(value = "埋点controller", tags = {"埋点接口"})
@RestController
@RequestMapping("/api")
public class UserBuriedPointController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private UserBuriedPointService userBuriedPointService;

    @Resource
    private ActivityService activityService;

    /**
     * 记录用户操作流程（埋点流程第二版本，使用消息队列模式）
     * 1 每次登陆插入新记录；
     * 2 每次登陆的操作修改当前插入记录；
     *
     * @param topName
     * @param ldBuryingPoint
     * @return
     */
    @ApiOperation("sendToMQ")
    @PostMapping("/userBuried/point/sendToMQ/{topName}")
    public ResultMap sendToMQ(@PathVariable("topName") String topName,
                              @ModelAttribute LdBuryingPoint ldBuryingPoint) {
        if (ldBuryingPoint == null || StringUtils.isBlank(ldBuryingPoint.getActionId()))
            return ResultMap.error("参数异常。");
        if (StringUtils.isEmpty(ldBuryingPoint.getAppType())) {
            ldBuryingPoint.setAppType("xld");
        }
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE + "." + topName, RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + topName, JSON.toJSONString(ldBuryingPoint));
        return ResultMap.success();
    }

    /**
     * 记录用户操作流程（新增埋点）
     * 2019年9月30日15:08:54
     * HYL
     *
     * @param topName
     * @param ldBuryingPointAdditional
     * @return
     */
    @ApiOperation("sendToMQ")
    @PostMapping("/userBuriedAdditional/point/sendToMQ/{topName}")
    public ResultMap userBuriedAdditional(@PathVariable("topName") String topName,
                                          @ModelAttribute LdBuryingPointAdditional ldBuryingPointAdditional) {
        if (ldBuryingPointAdditional == null || StringUtils.isBlank(ldBuryingPointAdditional.getActionId()))
            return ResultMap.error("参数异常。");

        if (StringUtils.isEmpty(ldBuryingPointAdditional.getAppType())) {
            ldBuryingPointAdditional.setAppType("xld");
        }
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE + "." + topName, RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + topName, JSON.toJSONString(ldBuryingPointAdditional));
        return ResultMap.success();
    }


    /**
     * 广告位展示，广告位点击，分类点击，分类内容点击埋点
     * 2019年10月21日18:23:39
     * HYL
     *
     * @param topName
     * @param ldBuryingPointEvery
     * @return
     */
    @ApiOperation("sendToMQ")
    @PostMapping("/userBuriedPoint/additionalEvery/sendToMQ/{topName}")
    public ResultMap userBuriedAdditionalEvery(@PathVariable("topName") String topName,
                                               @ModelAttribute LdBuryingPointEvery ldBuryingPointEvery) {
        if (ldBuryingPointEvery == null || StringUtils.isBlank(ldBuryingPointEvery.getActionId()))
            return ResultMap.error("参数异常。");
        if (StringUtils.isEmpty(ldBuryingPointEvery.getAppType())) {
            ldBuryingPointEvery.setAppType("xld");
        }
        rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_EXCHANGE + "." + topName, RabbitMQConstant.BURYPOINT_RUTE_KEY + "." + topName, JSON.toJSONString(ldBuryingPointEvery));
        return ResultMap.success();
    }


    @GetMapping("/userBuriedPoint/checkIsNew")
    public ResultMap checkIsNew(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        String deviceId = commomParams.getDeviceId();
        String appType = commomParams.getAppType();
        if (StringUtils.isEmpty(deviceId) || StringUtils.isEmpty(appType)) return ResultMap.error("缺少必填参数");
        boolean isNew = userBuriedPointService.getUserState(deviceId, appType);
        return ResultMap.success(isNew, "true-新用户,false-老用户");
    }


    @ApiOperation("保存活动埋点信息")
    @PostMapping("/saveBuryingPointActivity")
    public ResultMap userBuryingPointActivity(@ModelAttribute LdBuryingPointActivityVo ldBuryingPointActivityVo) {
        if (ldBuryingPointActivityVo == null || StringUtils.isBlank(ldBuryingPointActivityVo.getPointType())){
            return ResultMap.error("参数异常。");
        }
        //过滤过期活动埋点
        ldBuryingPointActivityVo.setCreateDate(new Date());
        ActActivity curActivity = activityService.getCurActivity(ldBuryingPointActivityVo);
        if (curActivity != null) {
            Long id = curActivity.getId();
            if (ldBuryingPointActivityVo.getActivityId().equals(id)){
                rabbitTemplate.convertAndSend(RabbitMQConstant.BURYPOINT_ACTIVITY_EXCHANGE, RabbitMQConstant.BURYPOINT_ACTIVITY_RUTE_KEY, JSON.toJSONString(ldBuryingPointActivityVo));
            }
        }
        return ResultMap.success();
    }
}
