package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.aop.RequestCache;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.service.AdvertOldService;
import com.miguan.ballvideo.vo.AdvertVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="广告controller",tags={"广告接口"})
@RestController
@RequestMapping("/api/advert")
public class AdvertOldController {

    @Resource
    private AdvertOldService advertOldService;

    @ApiOperation("广告信息列表接口（5分钟缓存）")
    @PostMapping("/infoList")
    public ResultMap<List<AdvertVo>> weatherInfo(@ApiParam(value = "广告位置类型,空则查询全部")String positionType,
                                                 @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                                 @ApiParam(value = "渠道ID")String channelId,
                                                 @ApiParam(value = "0没有开启，1开启")String permission,
                                                 @ApiParam(value = "app版本号") String appVersion,
                                                 @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("positionType", positionType);
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.getChannelId(channelId, mobileType));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("permission", permission);
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        List<AdvertVo> list = advertOldService.queryAdertList(param);
        return ResultMap.success(list);
    }

    @ApiOperation("游戏广告信息列表接口（5分钟缓存）")
    @PostMapping("/infoListGame")
    @RequestCache
    public ResultMap weatherInfoByGame(
            @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
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
        List<AdvertVo> list = advertOldService.queryAdertListGame(param);
        return ResultMap.success(list);
    }

    @ApiOperation("锁屏广告判断接口")
    @PostMapping("/lockScreenInfo")
    public ResultMap lockScreenInfo( @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                     @ApiParam(value = "渠道ID")String channelId,
                                     @ApiParam(value = "app版本号") String appVersion,
                                     @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.filterChannelId(channelId));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        return ResultMap.success(advertOldService.lockScreenAdertList(param));
    }

    @ApiOperation("根据指定N个广告位置返回所有广告")
    @PostMapping("/getAdversByPositionTypes")
    public ResultMap getAdversByPositionTypes(@ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
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
        return ResultMap.success(advertOldService.getAdversByPositionTypes(param));
    }

    @ApiOperation("banner广告信息列表接口（5分钟缓存）")
    @PostMapping("/bannerInfoList")
    public ResultMap<List<AdvertVo>> bannerInfo(@ApiParam(value = "广告位置类型,空则查询全部")String positionType,
                                                @ApiParam(value = "手机类型：1-ios，2：安卓")String mobileType,
                                                @ApiParam(value = "渠道ID")String channelId,
                                                @ApiParam(value = "0没有开启，1开启")String permission,
                                                @ApiParam(value = "app版本号") String appVersion,
                                                @ApiParam(value = "作用包") String appPackage) {
        Map<String, Object> param = new HashMap<>();
        param.put("positionType", positionType);
        param.put("mobileType", mobileType);
        param.put("marketChannelId", ChannelUtil.getChannelId(channelId, mobileType));
        param.put("channelId", ChannelUtil.filter(channelId));
        param.put("permission", permission);
        param.put("appVersion", VersionUtil.getVersion(appVersion));
        param.put("appPackage", PackageUtil.getAppPackage(appPackage, mobileType));
        List<AdvertVo> list = advertOldService.bannerInfo(param);
        return ResultMap.success(list);
    }
}
