package com.miguan.report.task;

import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.service.sync.SyncDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description 同步数据到banner_data_ext(请求数和错误数)
 * @Author zhangbinglin
 * @Date 2020/6/30 15:58
 **/
@Slf4j
@Component
public class SyncBannerDataExtTask {

    @Resource
    private SyncDataService syncDataService;

    /**
     * 同步数据到banner_data_ext(请求数和错误数)
     */
/*    @Scheduled(cron = "${task.scheduled.cron.banner-data-ext}")*/
    public void syncUmengData() {
        log.info("同步数据到banner_data_ext(请求数和错误数)");
        Date yesterday = DateUtils.addDays(new Date(), -1);
        syncDataService.deleteBannerDataExt(DateUtil.dateStr2(yesterday));   //统计前先删除昨天的数据
        syncDataService.saveBannerDataExt(yesterday, CommonConstant.VIDEO_APP_TYPE);  //统计视频数据
        syncDataService.saveBannerDataExt(yesterday, CommonConstant.CALL_APP_TYPE);   //统计来电数据
        syncDataService.updateBannerExtErrRate(DateUtil.dateStr2(yesterday));  //计算错误率
        //新增umeng_data友盟数据
        log.info("同步数据到banner_data_ext(请求数和错误数)结束");
    }

}
