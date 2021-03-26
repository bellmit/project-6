package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.AdvertService;
import com.miguan.laidian.vo.AdvertCodeVo;
import com.miguan.laidian.vo.AdvertPositionListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "广告controll", tags = {"广告接口"})
@RestController
@RequestMapping("/api/advertCode")
public class AdvertController {

    @Resource
    private AdvertService advertService;


    @ApiOperation("广告信息列表接口 V2.6.0 5分种缓存")
    @PostMapping("/infoList")
    public ResultMap<List<AdvertCodeVo>> weatherInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                               @ApiParam(value = "广告位置类型") String postitionType,
                                               @ApiParam(value = "是否有IMEI权限：0否 1是") String adPermission) {
        if (StringUtils.isEmpty(postitionType)) {
            return ResultMap.error("广告位置类型不能为空！");
        }
        if (StringUtils.isEmpty(commomParams.getChannelId())) {
            return ResultMap.error("渠道ID不能为空！");
        }
        if (StringUtils.isEmpty(commomParams.getAppType())) {
            return ResultMap.error("马甲包类型不能为空！");
        }
        if (StringUtils.isEmpty(commomParams.getMobileType())) {
            return ResultMap.error("手机类型不能为空！");
        }
        if (StringUtils.isEmpty(commomParams.getDeviceId())) {
            return ResultMap.error("设备id号不能为空！");
        }
        List<AdvertCodeVo> list = advertService.queryAdertList(commomParams, postitionType, adPermission);
        return ResultMap.success(list);
    }

    @ApiOperation("广告位置信息列表接口 V2.6.0 5分种缓存")
    @PostMapping("/advPositionInfoList")
    public ResultMap<AdvertPositionListVo> advPositionInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams) {
        if (StringUtils.isEmpty(commomParams.getAppType())) {
            return ResultMap.error("马甲包类型不能为空！");
        }
        if (StringUtils.isEmpty(commomParams.getMobileType())) {
            return ResultMap.error("手机类型不能为空！");
        }
        AdvertPositionListVo resultVo = advertService.advPositionInfo(commomParams);
        return ResultMap.success(resultVo);
    }
}
