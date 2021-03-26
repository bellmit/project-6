package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.AdvertOldService;
import com.miguan.laidian.vo.Advert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "广告controll", tags = {"广告接口"})
@RestController
@RequestMapping("/api/advert")
public class AdvertOldController {

    @Resource
    private AdvertOldService advertOldService;


    @ApiOperation("广告信息列表接口")
    @PostMapping("/infoList")
    public ResultMap<List<Advert>> weatherInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                               @ApiParam(value = "广告位置类型,空则查询全部") String postitionType,
                                               @ApiParam(value = "是否有IMEI权限：0否 1是") String adPermission) {
        List<Advert> list = advertOldService.queryAdertList(commomParams, postitionType, adPermission);
        return ResultMap.success(list);
    }
}
