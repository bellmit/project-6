package com.miguan.advert.common.task;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.HttpUtil;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.RobotUtil;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class DingTalkTask {


    @Value("${ding.robot.dataInfoUrl}")
    private String dataInfoUrl;

    @Value("${ding.robot.secret}")
    private String secret;


    @Value("${ding.robot.accessToken}")
    private String accessToken;

    /**
     * 每半小时预警
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void dingTalkHour() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.DING_TALK_AD_MONITORY_HOUR, RedisKeyConstant.DING_TALK_AD_MONITORY_SECONDS);
        if (redisLock.lock()) {
            dingTalkAdMonitory(0);
        }
    }

    /**
     * 每天10点预警
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void dingTalkDay() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.DING_TALK_AD_MONITORY_DAY, RedisKeyConstant.DING_TALK_AD_MONITORY_SECONDS);
        if (redisLock.lock()) {
            dingTalkAdMonitory(1);
        }
    }

    /**
     *
     * @param warnType 预警类型，0--每半小时，1--每24小时
     */
    public void dingTalkAdMonitory(Integer warnType) {
        if(StringUtils.isEmpty(secret) || "none".equals(secret)){
            log.info("======该服务器无机器人=======");
            return ;
        }
        if(warnType == null) {
            log.info("======预警类型不能为空=======");
            return;
        }
        log.info("======机器人要开始讲话了=======");
        //1.从大数据那边。获得数据
        ResultMap resultMap = HttpUtil.httpSend(dataInfoUrl + "?warnType=" + warnType, null);
        String text = "";
        if(resultMap.getCode() == 200){
            Object o  = resultMap.getData();
            if(o != null){
                text = o.toString();
                if(StringUtils.isEmpty(text)){
                    text = "目前无异常数据！";
                }
            } else {
                text = "目前无异常数据！";
            }
        } else {
            text = "系统遇到未知错误。需要排查下！";
        }
        //2:钉钉对话
        RobotUtil.talkText(text,secret,accessToken);
        log.info("======机器人讲话完毕=======");
    }
}
