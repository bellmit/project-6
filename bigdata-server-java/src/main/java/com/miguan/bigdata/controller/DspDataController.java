package com.miguan.bigdata.controller;

import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.service.DspDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@Api(value = "DSP系统数据接口", tags = {"DSP系统数据接口"})
@RestController
public class DspDataController {
    @Resource
    private DspDataService dspDataService;

    @Resource
    private AdDataService adDataService;

    @ApiOperation(value = "统计近7天每个时间段的日活数占比")
    @PostMapping("/api/dspdata/getUserRatio")
    public LinkedHashMap<String, Double> getUserRatio() {
        return dspDataService.getUserRatio();
    }



    @ApiOperation(value = "获取计划的日预算")
    @GetMapping("/api/dspdata/getPlanConsumption")
    public BigDecimal getPlanConsumption(Long planId) {
        if(planId == null){
            return null;
        }
        return adDataService.findPlanConsumption(planId);
    }
}
