package com.miguan.ballvideo.common.config.system;

import com.miguan.ballvideo.service.SysConfigService;
import com.miguan.ballvideo.service.SysService;
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
    private SysService sysService;

    @Override
    public void run(String... strings) throws Exception {
        //初始化系统配置
        initSystemConfig();
        //初始化广告配置信息
        initAdPositionConfig();
    }


    private void initSystemConfig() {
        sysConfigService.initSysConfig();
        log.info("---------------初始化系统内存成功!-------------------");
    }

    private void initAdPositionConfig() {
        sysService.updateAdConfigCache();
        sysService.updateAdLadderCache();
        log.info("---------------切片广告配置信息初始化成功!-------------------");
    }
}
