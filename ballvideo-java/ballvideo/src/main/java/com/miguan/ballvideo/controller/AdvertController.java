package com.miguan.ballvideo.controller;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.aop.RequestCache;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(value="广告Controller",tags={"新广告接口"})
@RestController
@RequestMapping("/api/advertCode")
public class AdvertController {

    @Resource
    private AdvertService advertService;

    @ApiOperation("广告信息列表接口 V2.5.0")
    @PostMapping("/infoList")
    public ResultMap<List<AdvertCodeVo>> advCodeInfoList(@AbTestAdvParams AbTestAdvParamsVo queueVo,
                                                         @ApiParam(value = "广告位置类型,空则查询全部")String positionType,
                                                         @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                                         @ApiParam(value = "渠道ID")String channelId,
                                                         @ApiParam(value = "0没有开启，1开启")String permission,
                                                         @ApiParam(value = "app版本号") String appVersion,
                                                         @ApiParam(value = "作用包") String appPackage) {
        if(StringUtils.isBlank(positionType) || StringUtils.isBlank(appPackage) || StringUtils.isBlank(appVersion) || StringUtils.isBlank(mobileType)){
            return ResultMap.success(Lists.newArrayList());
        }
        Map<String, Object> param = new HashMap<>();
        param.put("positionType", positionType);
        param.put("mobileType", mobileType);
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("permission", permission);
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        List<AdvertCodeVo> list = advertService.commonSearch(queueVo, param);
        if(list==null){
            return ResultMap.success(Lists.newArrayList());
        }
        return ResultMap.success(list);
    }

    @ApiOperation("广告信息列表接口 V2.5.3")
    @PostMapping("/infoLimitList")
    public ResultMap<List<List<AdvertCodeVo>>> advCodeInfoLimitList(@AbTestAdvParams AbTestAdvParamsVo queueVo,
                                                                    @ApiParam(value = "广告位置类型,空则查询全部")String positionType,
                                                                    @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                                                    @ApiParam(value = "渠道ID")String channelId,
                                                                    @ApiParam(value = "0没有开启，1开启")String permission,
                                                                    @ApiParam(value = "app版本号") String appVersion,
                                                                    @ApiParam(value = "作用包") String appPackage) {
        if(StringUtils.isBlank(positionType) || StringUtils.isBlank(appPackage) || StringUtils.isBlank(appVersion)){
            return ResultMap.success(Lists.newArrayList());
        }
        Map<String, Object> param = new HashMap<>();
        param.put("positionType", positionType);
        param.put("mobileType", mobileType);
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("permission", permission);
        param.put("appVersion", appVersion);
        param.put("appPackage", appPackage);
        List<AdvertCodeVo> list1 = advertService.commonSearch(queueVo, param);
        if(list1==null){
            return ResultMap.success(Lists.newArrayList());
        }else if (list1.size() > Constant.ADV_MAX_NUM) {
            list1 = list1.subList(0, Constant.ADV_MAX_NUM);
        }
        List<AdvertCodeVo> list2 = advertService.commonSearch(queueVo, param);
        if (list2.size() > Constant.ADV_MAX_NUM) {
            list2 = list2.subList(0, Constant.ADV_MAX_NUM);
        }
        List<List<AdvertCodeVo>> resultList = Lists.newArrayList();
        resultList.add(list1);
        resultList.add(list2);
        return ResultMap.success(resultList);
    }

    @ApiOperation("游戏广告信息列表接口（5分钟缓存），每个广告位置只需返回一个广告给第三方调用")
    @PostMapping("/infoListGame")
    @RequestCache
    public ResultMap weatherInfoByGame( @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
            @ApiParam(value = "渠道ID")String channelId,
            @ApiParam(value = "0没有开启，1开启")String permission,
            @ApiParam(value = "app版本号") String appVersion,
            @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.filterChannelId(channelId));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("permission", permission);
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        List<AdvertCodeVo> datas= advertService.getAdertsByGame(null, param);
        return ResultMap.success(datas);
    }

    @ApiOperation("锁屏广告判断接口 V2.5.0")
    @PostMapping("/lockScreenInfo")
    public ResultMap advCodeLockScreenInfo(@ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                     @ApiParam(value = "渠道ID")String channelId,
                                     @ApiParam(value = "app版本号") String appVersion,
                                     @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.filterChannelId(channelId));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        return ResultMap.success(advertService.getLockScreenInfo(null, param));
    }

    @ApiOperation("根据指定N个广告位置返回所有广告")
    @PostMapping("/getAdversByPositionTypes")
    public ResultMap getAdvListByPositionTypes(@AbTestAdvParams AbTestAdvParamsVo queueVo,
                                               @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                               @ApiParam(value = "渠道ID")String channelId,
                                               @ApiParam(value = "app版本号") String appVersion,
                                               @ApiParam(value = "广告位置多个逗号隔开") String positionTypes,
                                               @ApiParam(value = "0没有开启，1开启")String permission,
                                               @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.getChannelId(channelId, mobileType));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("positionTypes", StringUtil.strToList(positionTypes));
        param.put("permission",permission);
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        return ResultMap.success(advertService.getAdversByPositionTypes(queueVo, param));
    }

    @ApiOperation("获取appId(IOS用到)")
    @GetMapping("/getAppId")
    public ResultMap getAppId(@ApiParam(value = "作用包") String appPackage) {
       return ResultMap.success(advertService.getAppIdByAppPackage(appPackage));
    }

}
