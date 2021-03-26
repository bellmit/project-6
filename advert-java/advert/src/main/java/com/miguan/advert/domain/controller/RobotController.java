package com.miguan.advert.domain.controller;

import com.miguan.advert.common.task.DingTalkTask;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.RobotUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(value = "机器人接口",tags={"机器人接口接口"})
@RestController
@Slf4j
@RequestMapping(value = "/robot")
public class RobotController {

    @Value("${ding.robot.secret}")
    private String secret;


    @Value("${ding.robot.accessToken}")
    private String accessToken;

    @Resource
    private DingTalkTask dingTalkTask;

    @ApiOperation(value = "机器人话说接口")
    @GetMapping(value = "/talk")
    public ResultMap talk(String talk) {
        RobotUtil.talkText(talk, secret, accessToken);
        return ResultMap.success();
    }

    @ApiOperation(value = "机器人业务接口")
    @GetMapping(value = "/talkBusiness")
    public ResultMap talkBusiness(@ApiParam("类型：0-每半小时预警，1-每天10点预警前24小时") Integer warnType) {
        dingTalkTask.dingTalkAdMonitory(warnType);
        return ResultMap.success();
    }
}
