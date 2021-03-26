package com.miguan.ballvideo.controller.common;

import com.miguan.ballvideo.service.dsp.nadmin.BudgetSmoothService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "手动触发定时器接口",tags = {"手动触发定时器接口"})
@Slf4j
@RestController
public class HandTriggerController {

    @Resource
    private BudgetSmoothService budgetSmoothService;

    @ApiOperation("手动触计算预算平滑算法、参竞率")
    @PostMapping("/api/hand/trigger/budgetSmooth")
    public void budgetSmooth() {
        log.info("手动触计算预算平滑算法、参竞率（start）");
        budgetSmoothService.budgetSmooth();
        log.info("手动触计算预算平滑算法、参竞率（end）");
    }

}
