package com.miguan.recommend.common.system;

import com.miguan.recommend.service.xy.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目启动初始化数据统一管理
 *
 * @Author shixh
 * @Date 2019/11/28
 **/
@Slf4j
@Component
public class SystemCommandLineRunner implements CommandLineRunner {

    @Resource
    private SysConfigService sysConfigService;

    @Override
    public void run(String... strings) throws Exception {
        //初始化系统配置
        initSystemConfig();
    }

    private void initSystemConfig() {
        sysConfigService.initSysConfig();
        log.info("---------------初始化系统内存成功!-------------------");
    }
}
