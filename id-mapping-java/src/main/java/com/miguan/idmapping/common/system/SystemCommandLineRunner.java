package com.miguan.idmapping.common.system;

import com.miguan.idmapping.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 项目启动初始化数据统一管理
 * @Author shixh
 * @Date 2019/11/28
 **/
@Slf4j
@Component
public class SystemCommandLineRunner implements ApplicationRunner {

    @Resource
    private SysConfigService sysConfigService;

    private void initSystemConfig() {
        sysConfigService.initSysConfig();
        log.info("---------------初始化系统内存成功!-------------------");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化系统配置
        initSystemConfig();
    }
}
