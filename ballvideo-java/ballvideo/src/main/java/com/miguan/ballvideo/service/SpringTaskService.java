package com.miguan.ballvideo.service;

/**
 * Created by shixh on 2019/9/20.
 */
public interface SpringTaskService {

    void initPushTask();

    void stopPushTask(Long id);

    void stopTask(String key);

    void initTask();

    void initCleanPushTask();
}
