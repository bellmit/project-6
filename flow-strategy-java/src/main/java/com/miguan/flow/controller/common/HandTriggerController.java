package com.miguan.flow.controller.common;

import com.miguan.flow.task.InceVideoStorageTask;
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
    private InceVideoStorageTask inceVideoStorageTask;

    @ApiOperation("手动触计激励视频出入库")
    @PostMapping("/api/hand/trigger/storageTask")
    public void budgetSmooth() {
        log.info("手动触计激励视频出入库（start）");
        inceVideoStorageTask.storageTask();
        log.info("手动触计激励视频出入库（end）");
    }

}
