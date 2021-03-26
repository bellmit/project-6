package com.miguan.bigdata.task;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.mapper.VideoCountMapper;
import com.miguan.bigdata.mapper.VideoLeaderboardMapper;
import com.miguan.bigdata.vo.VideoCountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频排行帮任务
 */
@Slf4j
@Component
public class ViewLeaderboardTask {

    @Resource
    private VideoLeaderboardMapper videoLeaderboardMapper;
    @Resource
    private VideoCountMapper videoCountMapper;

    /**
     * 获取全站有效播放率最高的前5个分类，每个分类下按照有效播放数降序，选取前20个视频，一共取100个视频
     * 注：后台获取的100个视频，7天内不再获取，7天后可再次获取到
     */
    @Scheduled(cron = "30 30 0 * * ?")
    public void reloadLeaderboar() {
        log.info("=======================================热门视频排行榜任务开始=======================================");
        LocalDate localDate = LocalDate.now();
        int yesterdayInt = Integer.parseInt(localDate.minusDays(1L).format(DateTimeFormatter.BASIC_ISO_DATE));
        int sevenDaysAgoInt = Integer.parseInt(localDate.minusDays(7L).format(DateTimeFormatter.BASIC_ISO_DATE));
        String sevenDaysAgoStr = localDate.minusDays(7L).format(DateTimeFormatter.BASIC_ISO_DATE);

        // 分类有效播放率前5
        List<VideoCountVo> catVplayRate = videoCountMapper.countCatVplayRate(yesterdayInt, null);
        log.debug("热门视频排行榜分类有效播放率>>{}", JSONObject.toJSONString(catVplayRate));
        if(CollectionUtils.isEmpty(catVplayRate)){
            return;
        }

        // 7日内在热门排行榜出现过的视频
        List<Integer> excludeVideos = videoLeaderboardMapper.findDistinctVideoidsFromOneDay(2, sevenDaysAgoStr);
        log.debug("热门视频排行榜7日内出现过的视频>>{}", JSONObject.toJSONString(excludeVideos));

        int leaderboarSize = 100;
        List<VideoCountVo> top100 = new ArrayList<VideoCountVo>();
        for (VideoCountVo catRate : catVplayRate){
            List<VideoCountVo> videoVplayRate = videoCountMapper.countVideoVplayRate(sevenDaysAgoInt, catRate.getCatid(), excludeVideos, Math.min(20, leaderboarSize));
            if(CollectionUtils.isEmpty(videoVplayRate)){
                continue;
            }
            top100.addAll(videoVplayRate);
            leaderboarSize -= videoVplayRate.size();
            if(leaderboarSize<=0){
                break;
            }
        }

        List<Map<String, Object>> leaderboarVideo = new ArrayList<Map<String, Object>>();
        top100.forEach(e ->{
            Map<String, Object> entityMap = new HashMap<String, Object>(5);
            entityMap.put("dd", yesterdayInt);
            entityMap.put("dh", 0);
            entityMap.put("type", 2);
            entityMap.put("video_id", e.getVideoId());
            entityMap.put("number", e.getVplayCount());
            leaderboarVideo.add(entityMap);
        });
        log.debug("热门视频排行榜新增数据>>{}", JSONObject.toJSONString(leaderboarVideo));
        if(!CollectionUtils.isEmpty(leaderboarVideo)){
            videoLeaderboardMapper.insertBatch(leaderboarVideo);
            log.debug("热门视频排行榜删除无用数据");
        }
        Map<String, Object> deleteQuery = new HashMap<String, Object>();
        deleteQuery.put("dd", Integer.parseInt(localDate.minusDays(8L).format(DateTimeFormatter.BASIC_ISO_DATE)));
        deleteQuery.put("type", 2);
        videoLeaderboardMapper.deleteData(deleteQuery);
        log.info("=======================================热门视频排行榜任务结束=======================================");
    }

}
