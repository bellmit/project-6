package com.miguan.ballvideo.common.task;

import com.miguan.ballvideo.service.FirstVideosOldService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class VideoRealWeightRunTask implements Runnable {

    private FirstVideosOldService firstVideosOldService;

    @Override
    public void run() {
        log.info("更新真实权重值，定时器开启。");
        firstVideosOldService.updateVideoRealWeightByRedis();
        log.info("更新真实权重值，定时器结束。");
    }
}