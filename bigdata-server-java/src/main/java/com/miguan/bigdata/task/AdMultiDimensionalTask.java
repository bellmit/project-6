package com.miguan.bigdata.task;

import com.miguan.bigdata.mapper.AdDataMapper;
import com.miguan.bigdata.service.AdDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 从ck统计数据同步到allvideoadv库ad_multi_dimensional_data的表
 **/
@Slf4j
@Component
public class AdMultiDimensionalTask {

    @Resource
    private AdDataService adDataService;
    @Resource
    private AdDataMapper adDataMapper;

    /**
     * 每天凌晨3天同步数据
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void syncAdMultiDimensionalData() {
        log.info("定时同步ad_multi_dimensional_data的数据（start）");
        String date = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -1)); //获取前一天日期
        adDataService.syncAdMultiDimensionalData(date);

        //历史数据只保留欠天数据
        String oldDd = DateUtil.dateStr2(DateUtil.rollDay(new Date(), -3)); //获取前三天日期
        Map<String, Object> params = new HashMap<>();
        params.put("dd", oldDd);
        params.put("type", 2); //type=2表示删除历史数据
        adDataMapper.deleteAdMultiDimensionalData(params);
        adDataMapper.optimizeMulti();
        log.info("定时同步ad_multi_dimensional_data的数据（end）");
    }

}
