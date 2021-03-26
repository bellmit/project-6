package com.miguan.bigdata.task;

import com.miguan.bigdata.entity.NewUserSelection;
import com.miguan.bigdata.mapper.NewUserSelectionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * 首刷视频统计定时器
 */
@Slf4j
@Component
public class FirstFlushVideoCountTask {

    @Resource
    private NewUserSelectionMapper newUserSelectionMapper;

    @Scheduled(cron = "10 0 3 * * ?")
    public void count() {
        this.doCount();
    }

    public void doCount() {
        log.info("首刷视频统计任务开始");
        int yesterdayInt = Integer.parseInt(LocalDateTime.now().minusDays(1L).format(DateTimeFormatter.BASIC_ISO_DATE));
        List<NewUserSelection> selectionList = newUserSelectionMapper.findAll();
        if(!isEmpty(selectionList)){
            selectionList.forEach(e ->{
                Map<String, Object> videoCountInfo = newUserSelectionMapper.findCountInfoByVideoId(yesterdayInt, e.getVideoId());
                if(!isEmpty(videoCountInfo)){
                    int playCount = Integer.parseInt(videoCountInfo.get("play_count").toString());
                    int vplayCount = Integer.parseInt(videoCountInfo.get("vplay_count").toString());
                    int playover_count = Integer.parseInt(videoCountInfo.get("playover_count").toString());
                    e.setShowCount(Integer.parseInt(videoCountInfo.get("show_count").toString()));
                    e.setPlayCount(playCount);
                    e.setVplayCount(vplayCount);
                    e.setVplayRate(playCount == 0 ? 0D : new BigDecimal(vplayCount).divide(new BigDecimal(playCount), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    e.setAllPlayRate(playCount == 0 ? 0D : new BigDecimal(playover_count).divide(new BigDecimal(playCount), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    e.setUpdatedAt(new Date());
                    newUserSelectionMapper.updateCountInfo(e);
                } else {
                    e.setShowCount(0);
                    e.setPlayCount(0);
                    e.setVplayCount(0);
                    e.setVplayRate(0D);
                    e.setAllPlayRate(0D);
                    newUserSelectionMapper.updateCountInfo(e);
                }
            });
        }
        log.info("首刷视频统计任务结束");
    }

}
