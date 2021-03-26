package com.miguan.reportview.task;

import com.miguan.reportview.mapper.ChannelCostMapper;
import com.miguan.reportview.mapper.ChannelDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class ChannelDetailTask {

    @Resource
    private ChannelDetailMapper channelDetailMapper;

    @Value("${spring.profiles.active}")
    private String activeProfiles;

    @Resource
    private ChannelCostMapper channelCostMapper;

    /**
     * 同步视频用户渠道明细数据
     */
    @Scheduled(cron = "${task.scheduled.cron.sync-channel-detail}")
    public void syncChannelDetailRealTime() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("无法开始聚合视频用户渠道明细数据 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("聚合视频用户渠道明细数据开始");
        String day = DateUtil.dateStr2(new Date());
        channelDetailMapper.deleteChannelDetail(day);  //删除day日期的数据
        channelDetailMapper.batchSaveChannelDetail(day);  //同步day天的渠道明细数据到channel_detail表
        log.info("聚合视频用户渠道明细数据结束");
    }

    /**
     * 重新统计昨日用户渠道明细数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncChannelDetailYes() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("无法开始聚合视频用户渠道明细数据 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("聚合视频用户渠道明细数据开始");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String yesDay = DateUtil.dateStr2(calendar.getTime());

        channelDetailMapper.deleteChannelDetail(yesDay);  //删除day日期的数据
        channelDetailMapper.batchSaveChannelDetail(yesDay);  //同步day天的渠道明细数据到channel_detail表
        log.info("聚合视频用户渠道明细数据结束");
    }


    /**
     * 生成渠道成本表昨天数据
     */
    @Scheduled(cron = "0 1 7 * * ?")
    public void dateCostGenerate() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            log.info("无法开始生成渠道成本表 当前开启的策略为 {}", activeProfiles);
            return;
        }
        log.info("生成渠道成本表开始");
        channelCostMapper.dateCostGenerate();
        log.info("生成渠道成本表结束");
    }

}
