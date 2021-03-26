package com.miguan.bigdata.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.constant.RedisKeyConstants;
import com.miguan.bigdata.common.constant.SymbolConstants;
import com.miguan.bigdata.common.util.Global;
import com.miguan.bigdata.dto.NpushIterationDto;
import com.miguan.bigdata.entity.npush.PushDataIterArcticle;
import com.miguan.bigdata.entity.npush.PushDataIterConf;
import com.miguan.bigdata.service.AppDeviceService;
import com.miguan.bigdata.service.NpushIterationService;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 迭代推任务定时器
 */
@Slf4j
@Component
public class NpushIterationTask {

    @Resource
    private AppDeviceService appDeviceService;
    @Resource
    private NpushIterationService npushIterationService;
    @Value("${spring.profiles.active}")
    private String activeProfiles;

    private static int i = 0;

    @Scheduled(cron = "0 30 0 * * ?")
    public void npushChanelUpdate(){
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        String actDay = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        RedisLock redislock = new RedisLock(RedisKeyConstants.npush_channel_lock + actDay);
        if (redislock.lock()) {
            log.debug("同步推送渠道任务开始");
            npushIterationService.initDistinctNpushStateOfSomeActDay(actDay);
            log.debug("同步推送渠道任务结束");
        }
        redislock.unlock();
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void npush() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        this.doNpush(localDateTime);
    }

    public void doNpush(LocalDateTime localDateTime) {
        long timestamp = localDateTime.toEpochSecond(ZoneOffset.of("+8"));
        String currentTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        RedisLock redislock = new RedisLock(RedisKeyConstants.npush_iteration_lock + currentTime, 1800);
        if (redislock.lock()) {
            log.info("NpushIter 迭代推任务开始");
            int dayOfWeek = localDateTime.getDayOfWeek().getValue();
            List<NpushIterationDto> needProcessList = new ArrayList<NpushIterationDto>();
            // 根据星期X获取需要推送时间
            List<PushDataIterConf> confList = npushIterationService.getTaskFromConfig(dayOfWeek);
            confList.forEach(e -> {
                this.packageDto(timestamp, e, dayOfWeek, currentTime, needProcessList);
            });
            confList.clear();

            if (isEmpty(needProcessList)) {
                log.info("NpushIter 迭代推无需要处理队列,任务结束");
                return;
            }

            String exculdeChannelStr = Global.getValue("push_ddt_screen_channel");
            List<String> exculdeChannels = StringUtils.isEmpty(exculdeChannelStr) ? null : Arrays.asList(exculdeChannelStr.split(SymbolConstants.comma));

            // 发送推送信息到MQ
            log.info("NpushIter 迭代推需要处理队列>>{}", JSONObject.toJSONString(needProcessList));
            needProcessList.forEach(e -> {
                this.confimActDayByBatchNumToPush(e, exculdeChannels);
            });
            log.info("NpushIter 迭代推任务结束");
        }
    }

    private void packageDto(Long timestamp, PushDataIterConf conf, int dayOfWeek, String currentTime, List<NpushIterationDto> needProcessList) {
        String pushTimeConf = null;
        switch (dayOfWeek) {
            case 1:
                pushTimeConf = conf.getMonday();
                break;
            case 2:
                pushTimeConf = conf.getTuesday();
                break;
            case 3:
                pushTimeConf = conf.getWednesday();
                break;
            case 4:
                pushTimeConf = conf.getThursday();
                break;
            case 5:
                pushTimeConf = conf.getFriday();
                break;
            case 6:
                pushTimeConf = conf.getSaturday();
                break;
            case 7:
                pushTimeConf = conf.getSunday();
                break;
        }

        List<String> pushTimeList = Arrays.asList(pushTimeConf.split(","));
        int maxBatchNum = pushTimeList.size();
        int index = 0;
        for (; index < maxBatchNum; index++) {
            String pushTime = pushTimeList.get(index);
            if (pushTime.startsWith(currentTime)) {
                log.info("NpushIter 当前时间[{}]符合推送时间[{}], 入推送队列", currentTime, pushTime);
                log.info("NpushIter 推送配置>>{}", JSONObject.toJSONString(conf));
                List<String> packageNames = Arrays.asList(conf.getAppPackage().split(","));
                for (int i = 0; i < packageNames.size(); i++) {
                    needProcessList.add(new NpushIterationDto(timestamp, packageNames.get(i), conf.getProjectType(), Integer.parseInt(conf.getUsrActCnt()), index + 1, pushTimeList.size()));
                }
            } else {
                log.info("NpushIter 当前时间[{}]不符合推送时间[{}], 不入推送队列", currentTime, pushTime);
            }
        }
    }

    /**
     * 根据推送批次，确定哪些激活天数进行推送
     *
     * @param dto
     */
    public void confimActDayByBatchNumToPush(NpushIterationDto dto, List<String> exculdeChannels) {
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= dto.getUsrActCnt(); i++) {
                List<PushDataIterArcticle> arcticle = npushIterationService.getPushDateIterArcticleByActDatAndSortNum(dto.getProjectType(), i, dto.getBatchNum());
                if (isEmpty(arcticle)) {
                    log.info("NpushIter 应用{} - {}天激活，{}批次推送的无推送素材，结束该天激活用户的推送", dto.getAppPackage(), i, dto.getBatchNum());
                    continue;
                }

                log.info("NpushIter 应用{} - {}天激活，{}批次推送的素材>>{}", dto.getAppPackage(), i, dto.getBatchNum(), JSONObject.toJSONString(arcticle.get(0)));
                //npushIterationService.pushUserAsSomeActDayFromMongo(dto, arcticle.get(0), exculdeChannels);
                npushIterationService.pushAnyUserAsSomeOneActDayFromMongo(dto, arcticle.get(0), exculdeChannels);
            }
        });
        thread.start();
    }

}
