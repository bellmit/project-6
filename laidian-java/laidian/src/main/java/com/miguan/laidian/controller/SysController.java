package com.miguan.laidian.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.service.AboutUsService;
import com.miguan.laidian.service.ClMenuConfigService;
import com.miguan.laidian.service.SysConfigService;
import com.miguan.laidian.service.WarnKeywordService;
import com.miguan.laidian.service.impl.ChannelServiceImpl;
import com.miguan.laidian.vo.AboutUsVo;
import com.miguan.laidian.vo.ClMenuConfigVo;
import com.miguan.laidian.vo.DoubleElevenPopupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * 系统参数Controller
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Api(value = "系统配置Api", tags = {"系统配置接口"})
@RestController
@RequestMapping("/api/app")
public class SysController {

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private AboutUsService aboutUsService;

    @Resource
    private ClMenuConfigService clMenuConfigService;

    @Resource
    private WarnKeywordService warnKeywordService;

    @Resource
    private ChannelServiceImpl channelServiceImpl;

    @Resource
    private RedisService redisService;

    /**
     * PHP开关配置点击刷新缓存调用
     *
     * @return
     */
    @ApiOperation(value = "更新服务器的缓存", notes = "更新分布式服务的每个服务器的缓存", httpMethod = "GET")
    @GetMapping("/config/reload/all")
    public ResultMap reloadAll() {
        sysConfigService.reloadAll();
        return ResultMap.success();
    }

    /**
     * 获取系统版本配置信息
     */
    @ApiOperation("获取系统版本配置信息")
    @PostMapping(value = "/findSysVersionInfo.htm")
    public ResultMap findSysVersionInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> restMap = new HashMap<>();
        String appType = commomParams.getAppType();
        restMap.put("forceUpdate", Global.getValue("force_update", appType));  //是否强制更新：10--否，20--是
        restMap.put("updateContent", Global.getValue("update_content", appType));  //手机app最新版本更新内容
        restMap.put("androidVersion", Global.getValue("android_version", appType));  //最新android版本
        restMap.put("androidAddress", Global.getValue("android_address", appType));   //最新android版本下载地址
        restMap.put("iosVersion", Global.getValue("ios_version", appType));  //最新ios版本
        restMap.put("iosAddress", Global.getValue("ios_address", appType));   //最新ios版本下载地址
        return ResultMap.success(restMap);
    }

    /**
     * 获取关于我们的信息
     */
    @ApiOperation("获取关于我们的信息")
    @PostMapping(value = "/findAboutUsInfo.htm")
    public ResultMap findAboutUsInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        Map<String, Object> params = new HashMap<>();
        String appType = commomParams.getAppType();
        if (StringUtils.isEmpty(appType)) {
            params.put("appType", Constant.appXld);
        } else {
            params.put("appType", appType);
        }
        List<AboutUsVo> aboutUsList = aboutUsService.findAboutUsList(params);
        if (aboutUsList != null && aboutUsList.size() > 0) {
            return ResultMap.success(aboutUsList.get(0));
        }
        return ResultMap.success();
    }

    @ApiOperation("菜单栏配置信息(5分钟缓存)")
    @PostMapping(value = "/findMenuConfigInfo.htm")
    public ResultMap findMenuConfigInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                        @ApiParam(value = "是否有IMEI权限：0否 1是") String adPermission) {
        Map<String, Object> params = new HashMap<>();
        params.put("appType", commomParams.getAppType());// 马甲包类型
        params.put("channelId", commomParams.getChannelId());
        params.put("adPermission", StringUtils.isBlank(adPermission) ? "0" : adPermission);
        params.put("appVersion", commomParams.getAppVersion());
        params.put("mobileType", commomParams.getMobileType());
        List<ClMenuConfigVo> datas = clMenuConfigService.findClMenuConfigInfo(params);
        if (CollectionUtils.isNotEmpty(datas)) {
            //2.6.0之前版本不显示铃声，2.6.0之后不显示小视频 add shixh0527
            boolean isHigh = VersionUtil.compareIsHigh(commomParams.getAppVersion(), Constant.APPVERSION_260);
            List<ClMenuConfigVo> newDatas = Lists.newArrayList();
            for (ClMenuConfigVo vo : datas) {
                if (isHigh && "smallVideo".equals(vo.getKey())) {
                    continue;
                } else if (!isHigh && "ring".equals(vo.getKey())) {
                    continue;
                }
                newDatas.add(vo);
            }
            return ResultMap.success(newDatas);
        }
        return ResultMap.success();
    }

    /**
     * 获取当前提现人数
     */
    @ApiOperation("获取当前提现人数")
    @PostMapping(value = "/getWithdrawPerson.htm")
    public ResultMap getWithdrawPerson() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int secound = cal.get(Calendar.SECOND);
        Random rand = new Random();
        long baseNum = (hour * 60 * 60 + minute * 60 + secound) * 257;
        long randNum = rand.nextInt(201) + 100; //随机生成100-300的数值
        long withdrawPerson = baseNum + randNum;
        return ResultMap.success(withdrawPerson);
    }

    @ApiOperation("根据手机号查询出手机归属地")
    @GetMapping("/queryPhoneAscription")
    public ResultMap queryPhoneAscription(@ApiParam("手机号") String phoneNumber) {
        if (StringUtils.isEmpty(phoneNumber)) {
            return ResultMap.error();
        }
        //当前接口无用，临时注释 add shixh 0520
        /*JSONObject result = LaidianUtils.queryPhoneAscription(phoneNumber);
        if (result != null) {
            return ResultMap.success(result);
        } else {
            return ResultMap.error();
        }*/
        return ResultMap.success();
    }

    /**
     * 敏感词配置初始化，全量更新
     */
    @ApiOperation("敏感词配置初始化")
    @PostMapping(value = "/updateSensitiveWord.htm")
    public ResultMap updateSensitiveWord() {
        warnKeywordService.initWarnKeyword();
        return ResultMap.success();
    }


    /**
     * 渠道初始化，全量更新
     */
    @ApiOperation("渠道初始化")
    @PostMapping(value = "/updateChannelId")
    public ResultMap updateChannelId() {
        channelServiceImpl.ChannelInit();
        return ResultMap.success();
    }

    /**
     * 获取系统开关配置信息
     *
     * @param commomParams
     * @return
     */
    @ApiOperation("获取系统开关配置信息")
    @PostMapping(value = "/findSysConfigInfo")
    public ResultMap findSysConfigInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        return ResultMap.success(sysConfigService.findSysConfigInfo(commomParams.getAppType()));
    }

    @ApiOperation("根据Key删除Redis緩存")
    @GetMapping(value = "/delRedisByKey")
    public ResultMap delRedisByKey(String key) {
        if (StringUtils.isBlank(key) || key.contains("*")) {
            return ResultMap.error("参数不存在或者参数带*，删除失败！");
        }
        if ("queryAdertList".equals(key)) {
            key = "queryAdertList::laidian:cacheAble:queryAdertList:*";
        }
        //删除炫来电 安卓视频缓存  2020年3月24日16:21:02   HYL
        if ("videosList".equals(key)) {
            key = "laidian:requestCache:com.miguan.laidian.controller.VideoController.videosList*";
        }
        redisService.delRedisByKey(key);
        return ResultMap.success();
    }


    @ApiOperation("根据Key删除无效Redis緩存")
    @GetMapping(value = "/delErrorRedis")
    public ResultMap delErrorRedis() {
        //根据key模糊查询删除
        Set<String> set = redisService.keys("laidian:initPushTask*");
        for (String key : set) {
            redisService.delRedisByKey(key);
        }
        return ResultMap.success();
    }


    @ApiOperation(value = "上报版本更新")
    @PostMapping("/reportSysVersionInfo.htm")
    public ResultMap reportSysVersionInfo(@ApiParam("公共参数") @CommonParams CommonParamsVo commonParams) {
        int i = sysConfigService.reportSysVersionInfo(commonParams);
        if (i == 0) {
            ResultMap.error();
        }
        return ResultMap.success();
    }

    @ApiOperation("获取双十一淘宝红包弹窗信息和淘口令剪贴板")
    @PostMapping(value = "/doubleElevenPopup")
    public DoubleElevenPopupVo doubleElevenPopup() {
        String doubleEleven = Global.getObject("double_eleven_activity", Constant.appXld) == null ? "" : Global.getValue("double_eleven_activity", Constant.appXld);
        if(StringUtils.isBlank(doubleEleven)) {
            return null;
        }
        doubleEleven = doubleEleven.replaceAll("[\\t\\n\\r]", " ").trim();  //去掉姓名的制表符，换行符，回车符。
        JSONObject json = JSONObject.parseObject(doubleEleven);

        DoubleElevenPopupVo dto = new DoubleElevenPopupVo();
        dto.setBigImageUrl(json.getString("big_image_url"));
        dto.setSmallImageUrl(json.getString("small_image_url"));
        dto.setDeeplink(json.getString("deeplink"));
        dto.setTaobaoPassword(json.getString("taobao_password"));
        dto.setMaxCount(json.getInteger("max_count"));
        return dto;
    }

    @ApiOperation("查看项目情况接口")
    @GetMapping(value = "/projectCondition")
    public ResultMap projectCondition() {
        return sysConfigService.projectCondition();
    }
}
