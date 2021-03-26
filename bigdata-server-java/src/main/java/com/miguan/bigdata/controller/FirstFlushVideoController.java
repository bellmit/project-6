package com.miguan.bigdata.controller;

import com.miguan.bigdata.task.FirstFlushVideoCountTask;
import com.miguan.bigdata.vo.ResultMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/firstFlushVideo")
public class FirstFlushVideoController {

    @Resource
    private FirstFlushVideoCountTask firstFlushVideoCountTask;

    @GetMapping("/count")
    public ResultMap count() {
        firstFlushVideoCountTask.doCount();
        return ResultMap.success();
    }
}
