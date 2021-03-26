package com.miguan.reportview.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.entity.VideoSta;
import com.miguan.reportview.service.IVideoStaService;
import com.miguan.reportview.service.IVideosService;
import com.miguan.reportview.vo.FirstVideosStaVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-07 
 *
 */
@Component
@Slf4j
public class StatVideoAddAndOffline {
    @Autowired
    private IVideosService videosService;
    @Autowired
    private IVideoStaService videoStaService;
    @Value("${spring.profiles.active}")
    private String activeProfiles;

    /**
     * 实时统计video_sta表的数据
     */
    @Scheduled(cron = "${task.scheduled.cron.sta-video-add-offline}")
    public void sta() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("实时统计video_sta表的数据（start）");
        String today = DateUtil.yyyy_MM_dd();
        sta(today);
        log.info("实时统计video_sta表的数据（end）");
    }

    /**
     * 重新统计昨天video_sta的数据
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void staYes() {
        if (!"prod".equals(activeProfiles) && !"localdev".equals(activeProfiles)) {
            return;
        }
        log.info("重新统计昨天video_sta的数据(start)");
        String today = DateUtil.yedyyyy_MM_dd();
        sta(today);
        log.info("重新统计昨天video_sta的数据(end)");
    }

    public void sta(String date) {
        List<FirstVideosStaVo> list1 = videosService.staAddVideo(date);
        list1.addAll(videosService.staOfflineVideo(date));
        list1.addAll(videosService.staAllVideo());
        list1.addAll(videosService.staNewOnlineVideo(date));
        list1.addAll(videosService.staNewOfflineVideo(date));
        list1.addAll(videosService.staOlineVideo(date));
        list1.addAll(videosService.staNewCollectVideo(date));

        LocalDate ldate = DateUtil.yyyy_MM_dd(date);
        List<VideoSta> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list1)) {
            list1.forEach(e -> {
                VideoSta sta = new VideoSta();
                sta.setId(IdWorker.getId());
                sta.setCatId(e.getCatid());
                sta.setNum(e.getVideoNum());
                sta.setType(e.getType());
                sta.setDate(ldate);
                list.add(sta);
            });
        }

        if (!CollectionUtils.isEmpty(list)) {
            LambdaQueryWrapper<VideoSta> delWrap = Wrappers.<VideoSta>lambdaQuery().eq(VideoSta::getDate, date);
            videoStaService.remove(delWrap);
            videoStaService.saveBatch(list);
        }
    }
}
