package com.miguan.idmapping.controller;

import com.miguan.idmapping.common.utils.DateUtil;
import com.miguan.idmapping.task.BloomFilterTask;
import com.miguan.idmapping.task.MongosynTask;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhongli
 * @date 2020-08-27 
 *
 */
@RestController
@Api(hidden = true)
@Slf4j
public class BloomController {
    @Resource
    private BloomFilterTask bloomFilterTask;
    @Resource
    private MongosynTask mongosynTask;
    ExecutorService executor = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());

    @PostMapping("/api/init/bloom")
    public String initBloom() {
        executor.execute(() -> {
            log.warn("开始初始化旧设备信息至布隆过滤器");
            String date = DateUtil.yyyy_MM_dd();
            date = date.concat(" 00:00:00");
            bloomFilterTask.add2Bloom(false, 0L, date);
            log.warn("初始化旧设备信息至布隆过滤器-结束");
        });
        return "任务添加成功";
    }

    @PostMapping("/api/rebuild")
    public String regulate() {
        executor.execute(() -> {
            log.warn("开始初始化旧设备信息至布隆过滤器");
            bloomFilterTask.newAndswtich(true, null);
            log.warn("初始化旧设备信息至布隆过滤器-结束");
        });
        return "执行任务成功";
    }

    @PostMapping("/api/sysn/uuid")
    public String sysuuid(String collName, @RequestParam(required = false) String from) {
        if (StringUtils.isBlank(collName)) {
            return "collName不能为空";
        }
        executor.execute(() -> {
            log.warn("同步uuid");
            if (StringUtils.isNotBlank(from)) {
                mongosynTask.fromSysnuuid(from, collName);
            } else {
                mongosynTask.sysnuuid(null, collName);
            }
            log.warn("同步uuid-结束");
        });
        return "同步uuid任务成功";
    }
}
