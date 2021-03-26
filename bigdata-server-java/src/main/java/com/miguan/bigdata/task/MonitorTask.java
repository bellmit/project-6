package com.miguan.bigdata.task;

import com.miguan.bigdata.common.util.RobotUtil;
import com.miguan.bigdata.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description 预警定时器
 * @Author zhangbinglin
 * @Date 2020/11/6 10:40
 **/
@Slf4j
@Component
public class MonitorTask {

    @Resource
    private MonitorService monitorService;
    @Value("${ding.robot.secret}")
    private String secret;
    @Value("${ding.robot.accessToken}")
    private String accessToken;

    /**
     * 钉钉预警埋点数据中uuid为空的数据
     */
    @Scheduled(cron = "0 0,30 * * * ?")
    public void dingTalkAdMonitory() {
        log.info("======预警uuid为空，机器人讲话完毕(start)=======");
        String monitorResult = monitorService.getUuidNullMonitor();
        RobotUtil.talkText(monitorResult,secret,accessToken);  //调用钉钉机器人，往群里发送消息
        log.info("======预警uuid为空，机器人讲话完毕(end)=======");
    }
}
