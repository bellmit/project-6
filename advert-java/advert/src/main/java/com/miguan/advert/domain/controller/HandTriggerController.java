package com.miguan.advert.domain.controller;

import com.miguan.advert.domain.service.AdAutoSortService;
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
    private AdAutoSortService adAutoSortService;

    @ApiOperation("手动触发代码位自动排序")
    @PostMapping("/api/hand/trigger/handAdIdAutoSort")
    public void handAdIdAutoSort() {
        log.info("手动触发代码位自动排序（start）");
        adAutoSortService.adAutoSort();
        log.info("手动触发代码位自动排序（end）");
    }

    @ApiOperation("实时监测自动排序代码位展现量是否超过阀值，超过则将该代码位id回退至昨日排名")
    @PostMapping("/api/hand/trigger/updateSortWhenGtThreshold")
    public void updateSortWhenGtThreshold() {
        log.info("手动触发，实时监测自动排序代码位展现量是否超过阀值（start）");
        adAutoSortService.updateSortWhenGtThreshold();
        log.info("手动触发，实时监测自动排序代码位展现量是否超过阀值（end）");
    }
}
