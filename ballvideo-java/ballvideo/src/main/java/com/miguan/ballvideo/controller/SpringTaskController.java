package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.PushArticleService;
import com.miguan.ballvideo.service.SpringTaskService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author shixh
 */
@Slf4j
@Api(tags = "消息推送定时器接口", description = "/api/springTask")
@RestController
@RequestMapping("/api/springTask")
public class SpringTaskController {

    @Resource
    private SpringTaskService springTaskService;

    @Resource
    private PushArticleService pushArticleService;

    /**
     * 初始化定时器（定时推送PHP调用）
     * @return
     */
    @GetMapping("/initPushTask")
    public ResultMap initPushTask() {
        //springTaskService.initPushTask();
        return ResultMap.success();
    }


    /**
     * 停止推送定时器（PHP后台开关配置修改推送间隔时调用）
     * @return
     */
    @GetMapping("/stopPushTask")
    public ResultMap stopPushTask(Long id) {
        //springTaskService.stopPushTask(id);
        return ResultMap.success();
    }

    /**
     * 停止推送定时器(Test)
     * @return
     */
    @GetMapping("/stopTask")
    public ResultMap stopTask(String key) {
        //springTaskService.stopTask(key);
        return ResultMap.success();
    }

}
