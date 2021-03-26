package com.miguan.ballvideo.common.config.system;

import com.miguan.ballvideo.service.SpringTaskService;
import com.miguan.ballvideo.service.SysConfigService;
import com.miguan.ballvideo.service.SysService;
import com.miguan.ballvideo.service.WarnKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目启动初始化数据统一管理
 * @Author shixh
 * @Date 2019/11/28
 **/
@Slf4j
@Component
public class SystemCommandLineRunner implements CommandLineRunner {

    @Resource
    private SysConfigService sysConfigService;

    @Resource
    private WarnKeywordService warnKeywordService;

    @Resource
    private SpringTaskService springTaskService;

    @Resource
    private SysService sysService;

    @Override
    public void run(String... strings) throws Exception {
        //初始化系统配置
        initSystemConfig();
        //初始化敏感词
        initWarnKeyword();
        initWarnKeywordIos();
        //初始化定时器
        //initTask();
        //初始化广告配置信息
        initAdPositionConfig();
    }

    private void initWarnKeyword() {
        warnKeywordService.initWarnKeyword();
        log.info("---------------初始化敏感词成功!-------------------");
    }

    private void initWarnKeywordIos(){
        warnKeywordService.initWarnKeywordIos();
        log.info("---------------初始化IOS敏感词成功!-------------------");
    }

    private void initSystemConfig() {
        sysConfigService.initSysConfig();
        log.info("---------------初始化系统内存成功!-------------------");
    }

    private void initTask() {
        springTaskService.initTask();
        log.info("---------------初始化定时器成功!-------------------");
    }

    private void initAdPositionConfig() {
        sysService.updateAdConfigCache();
        sysService.updateAdLadderCache();
        log.info("---------------切片广告配置信息初始化成功!-------------------");
    }
}
