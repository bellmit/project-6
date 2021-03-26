package com.miguan.bigdata.task;

import com.miguan.bigdata.mapper.ManulPushDistinctMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description 删除手动推送冗余数据
 **/
@Slf4j
@Component
public class ManualPushTask {

    @Resource
    private ManulPushDistinctMapper manulPushDistinctMapper;

    @Scheduled(cron = "1 0 2 * * ?")
    public void changeBloomFilter() {
        log.info("删除手动推送冗余数据开始");
        Integer dt = Integer.parseInt(LocalDate.now().minusDays(7).format(DateTimeFormatter.BASIC_ISO_DATE));
        manulPushDistinctMapper.deleteByDt(dt);
        log.info("删除手动推送冗余数据结束");
    }
}
