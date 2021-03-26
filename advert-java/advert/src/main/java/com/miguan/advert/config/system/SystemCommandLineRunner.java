package com.miguan.advert.config.system;

import com.miguan.advert.common.task.AbExpTask;
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
    private AbExpTask abExpTask;

    @Override
    public void run(String... strings) throws Exception {
        //初始化定时器
        abExpTask();
    }

    private void abExpTask() {
        abExpTask.initTask();
        log.info("---------------初始化定时器成功!-------------------");
    }

}
