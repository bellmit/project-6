package com.miguan.advert.domain.controller;

import com.alibaba.fastjson.JSON;
import com.miguan.advert.common.constants.CommonConstant;
import com.miguan.advert.common.constants.FlowGroupConstant;
import com.miguan.advert.common.nadmin.interceptor.LoginAuth;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.AccountService;
import com.miguan.advert.domain.service.AdAdvertCodeService;
import com.miguan.advert.domain.service.PositionInfoService;
import com.miguan.advert.domain.service.PublicInfoService;
import com.miguan.advert.domain.vo.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import com.miguan.advert.domain.mapper.DistrictMapper;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "公共信息",tags = {"公共信息"})
@RestController
@RequestMapping("/api/public_api")
public class PublicInfoController {

    @Resource
    private AccountService accountService;

    @Resource
    private PublicInfoService publicInfoService;

    @Resource
    private PositionInfoService positionInfoService;

    @Resource
    private AdAdvertCodeService adAdvertCodeService;

    @Resource
    private DistrictMapper districtMapper;


    //@LoginAuth
    @ApiOperation("获取应用列表")
    @GetMapping("/getApp")
    public ResultMap<List<AppInfoVo>> getApp() {
        List<AppInfoVo> resultList = publicInfoService.getApp();
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("获取账户列表")
    @GetMapping("/getAccountList")
    public ResultMap<List<AccountInfoVo>> getAccountList() {
        List<AccountInfoVo> resultList = accountService.getAccountList();
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("获取广告平台列表")
    @GetMapping("/getAdPlat")
    public ResultMap<List<AdPlatVo>> getAdPlat() {
        List<AdPlatVo> resultList = publicInfoService.getAdPlat();
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("获取广告平台列表")
    @GetMapping("/getAppTypeList")
    public ResultMap<List<AppTypeVo>> getAppTypeList() {
        List<AppTypeVo> resultList = JSON.parseArray(CommonConstant.APP_TYPE, AppTypeVo.class);
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("获取广告位置列表信息")
    @GetMapping("/getAppAdPositionName")
    public ResultMap<List<AppAdPositionVo>> getAppAdPositionName(@ApiParam("包名") String app_package,
                                                                 @ApiParam("手机类型") Integer mobile_type) {
        List<AppAdPositionVo> resultList = positionInfoService.getAppAdPositionName(app_package, mobile_type);
        return ResultMap.success(resultList);
    }

    @ApiOperation("获取广告码")
    @GetMapping("/getAdCode")
    public ResultMap getAdCode(String ad_id) {
        String[] adIds = adAdvertCodeService.findAdCodeAdId(ad_id);
        return ResultMap.success(adIds);
    }

    @ApiOperation("获取城市列表")
    @GetMapping("/getCityList")
    public ResultMap<List<AdCityVo>> getCityList() {
        try {
            List<AdCityVo> cityList = districtMapper.getCityList(FlowGroupConstant.DISTRICT_CITY);
            return ResultMap.success(cityList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }

}

