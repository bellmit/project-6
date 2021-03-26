package com.miguan.bigdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.SymbolConstants;
import com.miguan.bigdata.common.util.Global;
import com.miguan.bigdata.entity.npush.PushDataIterArcticle;
import com.miguan.bigdata.mapper.DwdUserInfoMapper;
import com.miguan.bigdata.service.AppDeviceService;
import com.miguan.bigdata.service.NpushIterationService;
import com.miguan.bigdata.task.NpushIterationTask;
import com.miguan.bigdata.vo.ResultMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "推测试接口", tags = {"推测测试接口"})
@RequestMapping("/api/test")
@RestController
public class NpushTestController {

    @Resource
    private NpushIterationService npushIterationService;
    @Resource
    private DwdUserInfoMapper dwdUserInfoMapper;
    @Resource
    private NpushIterationTask npushIterationTask;
    @Resource
    private AppDeviceService appDeviceService;

    @ApiOperation(value = "根据激活日期和包名，查询符合推送条件的素材及用户")
    @PostMapping("/npush/info")
    public ResultMap npushInfo(@ApiParam(value = "激活天数") Integer actDay, @ApiParam(value = "项目：来电-1，视频-2，百步赚-3") Integer projectType, @ApiParam(value = "包名") String appPackage) {
        if (actDay == null) {
            return ResultMap.error("参数：激活天数[actDay]不能为空");
        }
        if (projectType == null) {
            return ResultMap.error("参数：项目[projectType]不能为空, 对应值：来电-1，视频-2，百步赚-3");
        }
        if (StringUtils.isEmpty(appPackage)) {
            return ResultMap.error("参数：包名[appPackage]不能为空");
        }


        String actDate = LocalDate.now().minusDays(actDay - 1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<PushDataIterArcticle> arcticleList = npushIterationService.getAlternativeArcticleList(projectType, actDay, 0);

        String exculdeChannelStr = Global.getValue("push_ddt_screen_channel");
        List<String> exculdeChannels = StringUtils.isEmpty(exculdeChannelStr) ? null : Arrays.asList(exculdeChannelStr.split(SymbolConstants.comma));
        List<Map<String, Object>> userInfoList = dwdUserInfoMapper.selectMapByPackageNameAndFirstVisitDate(appPackage, actDate, exculdeChannels, null, null);
        Map<String, Object> result = new HashMap<>();
        result.put("arcticle", arcticleList);
        result.put("user", userInfoList);
        return ResultMap.success(result);
    }

    @ApiOperation(value = "测试发送")
    @PostMapping("/npush/send")
    public ResultMap npushSend(@ApiParam(value = "日期时间") String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        npushIterationTask.doNpush(localDateTime);
        return ResultMap.success();
    }

    @ApiOperation(value = "测试推送设备初始化")
    @PostMapping("/npush/distinct/init")
    public ResultMap npushDidstinctInit(@ApiParam(value = "激活日期") String actDay) {
        npushIterationService.initDistinctNpushStateOfSomeActDay(actDay);
        return ResultMap.success();
    }

    @ApiOperation(value = "测试推送设备初始化")
    @PostMapping("/update/npushChannel")
    public ResultMap updateNpushChannel(@ApiParam(value = "video/laidian") String appType, @ApiParam(value = "设备ID") String deviceId, @ApiParam(value = "推送渠道 -1 无 1 友盟 2 华为 3 vivo 4 oppo 5 小米 int") String pushChannel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appType", appType);
        jsonObject.put("deviceId", deviceId);
        jsonObject.put("pushChannel", pushChannel);
        appDeviceService.updateDistinctNPushChannel(jsonObject);
        return ResultMap.success();
    }

}
