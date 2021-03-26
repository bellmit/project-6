package com.miguan.report.task;

import com.miguan.report.service.sync.SyncDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 同步数据到hour_data(点击数和展现数)
 * @Author zhangbinglin
 * @Date 2020/6/30 15:58
 **/
@Slf4j
@Component
public class SyncHourDataTask {

    @Resource
    private SyncDataService syncDataService;

    /**
     * 每天同步数据到hour_data(点击数和展现数)
     */
  /*  @Scheduled(cron = "${task.scheduled.cron.hour-data}")*/
    public void SyncHourData() {
        log.info("同步数据到hour_data(点击数和展现数)");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        syncDataService.deleteHourDataByDate(DateUtil.dateStr2(yesterday));   //统计前先删除昨天的数据
        syncDataService.saveHourDataExt(yesterday);  //统计视频数据
        syncDataService.updateClickRate(DateUtil.dateStr2(yesterday));  //计算错误率
        log.info("同步数据到hour_data(点击数和展现数)");
    }

}
