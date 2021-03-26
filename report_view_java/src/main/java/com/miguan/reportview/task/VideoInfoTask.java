package com.miguan.reportview.task;

import com.miguan.reportview.service.IVideosService;
import com.miguan.reportview.service.PushLdService;
import com.miguan.reportview.service.PushVideoService;
import com.miguan.reportview.service.SyncUserContentService;
import com.miguan.reportview.service.impl.VideosServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * 定时从mysql同步视频信息数据到video_info
 * @author zhangbinglin
 *
 */
@Component
@Slf4j
public class VideoInfoTask {
    @Resource
    private IVideosService videosService;
    @Resource
    private PushVideoService pushVideoService;
    @Resource
    private PushLdService pushLdService;
    @Value("${spring.profiles.active}")
    private String activeProfiles;

    /**
     * 实时同步视频信息数据
     */
//    @Scheduled(cron = "${task.scheduled.cron.video-info}")  模块以及迁移到 大数据部的bigdata-server服务中
    public void syncVideoInfo() {
        log.info("定时从mysql同步视频信息数据到video_info, activeProfiles:{}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("定时从mysql同步视频信息数据到video_info(start)");
        videosService.syncVideoInfo();
        log.info("定时从mysql同步视频信息数据到video_info（end）");
    }


    /**
     * 实时同步来电秀视频信息数据
     */
//    @Scheduled(cron = "${task.scheduled.cron.video-info}")  模块以及迁移到 大数据部的bigdata-server服务中
    public void syncLdVideoInfo() {
        log.info("定时从mysql同步来电秀视频信息数据到ld_video_info, activeProfiles:{}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("定时从mysql同步来电秀信息数据到video_info(start)");
        pushLdService.syncLdVideoCat();  //同步来电分类
        pushLdService.syncPushLdPlayCount();  //同步push来电秀库的播放数（同步到mysql库）
        videosService.syncLdVideoInfo();
        log.info("定时从mysql同步来电秀信息数据到video_info（end）");
    }


    /**
     * 实时同步视频明细数据
     */
//    @Scheduled(cron = "${task.scheduled.cron.video-detail}")  模块以及迁移到 大数据部的bigdata-server服务中
    public void syncVideoDetail() {
        log.info("实时同步视频明细数据, activeProfiles:{}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("实时同步视频明细数据(start)");
        String date = DateUtil.dateStr2(new Date());
        videosService.syncVideoDetail(date);
        pushVideoService.syncPushVideos();  //同步push视频库视频的播放数，有效播放数，完播数
        log.info("实时同步视频明细数据(end)");
    }

    /**
     * 重新统计昨日视频明细数据
     */
//    @Scheduled(cron = "0 40 0 * * ?")  模块以及迁移到 大数据部的bigdata-server服务中
    public void syncVideoDetailYes() {
        log.info("同步昨天视频明细数据, activeProfiles:{}", activeProfiles);
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("同步昨天视频明细数据(start)");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        String date = DateUtil.dateStr2(calendar.getTime());
        videosService.syncVideoDetail(date);
        log.info("同步昨天视频明细数据(end)");
    }
}