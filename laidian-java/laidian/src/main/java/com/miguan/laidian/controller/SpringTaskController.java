package com.miguan.laidian.controller;


import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.service.SpringTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @author shixh
 */
@Slf4j
@Api(value = "动态定时器controller", tags = {"动态定时器接口"})
@RestController
@RequestMapping("/api/springTask")
public class SpringTaskController {

    @Resource
    private SpringTaskService springTaskService;

    /**
     * 启动推送定时器（定时推送）
     * @return
     */
    @ApiOperation("启动推送定时器(PHP调用)")
    @PostMapping("/initPushTask")
    public ResultMap initPushTask() {
        //springTaskService.initPushTask();
        return ResultMap.success();
    }

    /**
     * 停止推送定时器（PHP删除定时任务调用）
     * @return
     */
    @ApiOperation("停止推送定时器")
    @PostMapping("/stopPushTask")
    public ResultMap stopPushTask(Long id) {
        //springTaskService.stopPushTask(id);
        return ResultMap.success();
    }

}
