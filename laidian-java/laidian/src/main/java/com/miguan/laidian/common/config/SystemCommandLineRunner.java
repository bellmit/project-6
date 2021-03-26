package com.miguan.laidian.common.config;

import com.miguan.laidian.service.SpringTaskService;
import com.miguan.laidian.service.SysConfigService;
import com.miguan.laidian.service.WarnKeywordService;
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

    @Override
    public void run(String... strings) throws Exception {
        //初始化系统配置
        initSystemConfig();
        //初始化敏感词
        initWarnKeyword();
        //初始化推送定时器
        //initPushTask();
    }

    private void initWarnKeyword() {
        warnKeywordService.initWarnKeyword();
        log.info("---------------初始化敏感词成功!-------------------");
    }

    private void initSystemConfig() {
       sysConfigService.initSysConfig();
        log.info("---------------初始化系统内存成功!-------------------");
    }

    private void initPushTask() {
        springTaskService.initPushTask();
        log.info("---------------初始化推送定时器成功!-------------------");
    }
}
