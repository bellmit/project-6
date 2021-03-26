package com.miguan.advert.domain.controller;

import com.alibaba.fastjson.JSON;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.domain.service.PositionInfoService;
import com.miguan.advert.domain.vo.request.ConfigAddVo;
import com.miguan.advert.domain.vo.result.CodeListVo;
import com.miguan.advert.domain.vo.result.PositionInfoVo;
import com.miguan.advert.domain.vo.result.PositionListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "广告位置信息",tags = {"广告位置信息"})
@RestController
@RequestMapping("/api/advert_config")
public class PositionInfoController {

    @Resource
    private PositionInfoService positionInfoService;

    //@LoginAuth
    @ApiOperation("获取广告配置列表")
    @GetMapping("/getData")
    public ResultMap<PositionListVo> getData(HttpServletRequest request,
                                         @ApiParam("筛选类型") Integer type,
                                         @ApiParam("筛选数据") String s_data,
                                         @ApiParam("当前页面") @RequestParam Integer page,
                                         @ApiParam("页面数量") @RequestParam Integer page_size) {
        PositionListVo result = positionInfoService.getData(request, type, s_data, page, page_size);
        return ResultMap.success(result);
    }

    //@LoginAuth
    @ApiOperation("获取广告配置信息")
    @GetMapping("/getPosition")
    public ResultMap<List<PositionInfoVo>> getPosition(@ApiParam("马甲包") @RequestParam String app_package,
                                                       @ApiParam("广告位置Id") @RequestParam Integer position_id) {
        List<PositionInfoVo> resultList = positionInfoService.getPosition(position_id);
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("获取已配置信息")
    @GetMapping("/getCode")
    public ResultMap<List<CodeListVo>> getCode(@ApiParam("马甲包") @RequestParam String app_package,
                                               @ApiParam("广告位置Id") @RequestParam Integer position_id) {
        List<CodeListVo> resultList = positionInfoService.getCode(position_id);
        return ResultMap.success(resultList);
    }

    //@LoginAuth
    @ApiOperation("保存配置信息")
    @PostMapping("/finnalSave")
    public ResultMap finnalSave(@ApiParam("新增配置Id") String insert_str,
                                @ApiParam("删除配置Id") String delete_str,
                                @ApiParam("配置数据") @RequestBody String rate_data) {
        ConfigAddVo rateData = JSON.parseObject(rate_data, ConfigAddVo.class);
        if (rateData != null) {
            if (rateData.getComputer() == null) {
                return ResultMap.error(202,"算法不能为空或者必填");
            }
            if (rateData.getConfig_id() == null) {
                return ResultMap.error(202,"配置Id不能为空或者必填");
            }
            return positionInfoService.saveConfigInfo(insert_str, delete_str, rateData);
        } else {
            return ResultMap.error("","提交失败:rate_data错误");
        }
    }
}

