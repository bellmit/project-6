package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.miguan.reportview.entity.FirstVideos;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.entity.VideosCat;
import com.miguan.reportview.mapper.FirstVideosMapper;
import com.miguan.reportview.mapper.VideosCatMapper;
import com.miguan.reportview.service.IVideosService;
import com.miguan.reportview.vo.FirstVideosStaVo;
import com.miguan.reportview.vo.FirstVideosVo;
import com.miguan.reportview.vo.LdVideosVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import tool.util.StringUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频类别表 服务实现类
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
@Service
@DS("video-db")
@Slf4j
public class VideosServiceImpl implements IVideosService {
    @Resource
    private VideosCatMapper videosCatMapper;
    @Resource
    private FirstVideosMapper firstVideosMapper;
    private Map<Long, String> cacheCat;

    @PostConstruct
    public void init() {
        try {
            DynamicDataSourceContextHolder.push("video-db");
            List<VideosCat> list = getVideosCat();
            cacheCat = list.stream().collect(Collectors.toMap(VideosCat::getId, VideosCat::getName));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            DynamicDataSourceContextHolder.poll();
        }

    }

    @Override
    public String getCatName(String catid) {
        long catidL = Long.valueOf(catid).longValue();
        if (cacheCat == null || !cacheCat.containsKey(catidL)) {
            return catid;
        }
        return cacheCat.get(catidL);
    }

    @Override
    public List<VideosCat> getVideosCat() {
        LambdaQueryWrapper<VideosCat> wrapper = Wrappers.<VideosCat>lambdaQuery().select(VideosCat::getId, VideosCat::getName).orderByAsc(VideosCat::getSort);
        return videosCatMapper.selectList(wrapper);
    }

    @Override
    public List<FirstVideosStaVo> staAddVideo(String date) {
        return firstVideosMapper.staAdd(buildParam(date));
    }

    @Override
    public List<FirstVideosStaVo> staOfflineVideo(String date) {
        return firstVideosMapper.staoffline(buildParam(date));
    }

    @Override
    public List<FirstVideosStaVo> staAllVideo() {
        return firstVideosMapper.staAll();
    }

    @Override
    public List<FirstVideosStaVo> staNewOnlineVideo(String date) {
        return firstVideosMapper.staNewOnlineVideo(buildParam(date));
    }

    @Override
    public List<FirstVideosStaVo> staNewOfflineVideo(String date) {
        return firstVideosMapper.staNewOfflineVideo(buildParam(date));
    }

    @Override
    public List<FirstVideosStaVo> staOlineVideo(String date) {
        return firstVideosMapper.staOlineVideo(buildParam(date));
    }

    @Override
    public List<FirstVideosStaVo> staNewCollectVideo(String date) {
        return firstVideosMapper.staNewCollectVideo(buildParam(date));
    }

    private Map<String, Object> buildParam(String date) {
        String startDate = date.concat(" 00:00:00");
        String endDate = date.concat(" 23:59:59");
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(3);
        try {
            params.put("startDate", DateUtils.parseDate(startDate, tool.util.DateUtil.DATEFORMAT_STR_001));
            params.put("endDate", DateUtils.parseDate(endDate, tool.util.DateUtil.DATEFORMAT_STR_001));
            params.put("date", date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return params;
    }

    /**
     * 从mysql的first_videos中同步视频数据到clickhouse的video_info中
     */
     public void syncVideoInfo() {
        String maxUpdateTime = firstVideosMapper.findMaxVideoUpdatedTime();
         maxUpdateTime = (maxUpdateTime == null ? "0000-00-00 00:00:00" : maxUpdateTime);

         int count = firstVideosMapper.countNewVideos(maxUpdateTime);
         int index = 0;
         int pageSize = 5000;

         Map<String, Object> params = new HashMap<>();
         params.put("pageSize", pageSize);
         params.put("maxUpdateTime", maxUpdateTime);
         while (index < count) {
             params.put("startRow", index);
             List<FirstVideosVo> dataList = firstVideosMapper.queryNewVideos(params);  //在mysql查询出视频数据

             if(dataList != null && !dataList.isEmpty()) {
                 firstVideosMapper.deleteVideoInfoById(dataList);   //删除clickhouse中已经存在的视频数据
                 firstVideosMapper.batchInsertUpdateVideo(dataList);  //重新同步最新的视频数据到clickhouse中
             }
             index = index + pageSize;
             log.info("同步视频数据，记录：{}", index);
         }
     }

    /**
     * 汇总视频明细数据到clickhouse的video_detail表中
     * @param day
     */
    public void syncVideoDetail(String day) {
         if(StringUtil.isBlank(day)) {
             return;
         }

         firstVideosMapper.deleteVideoDetail(day);
         firstVideosMapper.batchSaveVideoDetail(day);
     }

    /**
     * 从mysql来电库中的videos中同步来电秀数据到clickhouse的ld_video_info中
     */
    public void syncLdVideoInfo() {
        String maxUpdateTime = firstVideosMapper.findMaxLdVideoUpdatedTime();
        maxUpdateTime = (maxUpdateTime == null ? "0000-00-00 00:00:00" : maxUpdateTime);

        int count = firstVideosMapper.countNewLdVideos(maxUpdateTime);
        int index = 0;
        int pageSize = 5000;

        Map<String, Object> params = new HashMap<>();
        params.put("pageSize", pageSize);
        params.put("maxUpdateTime", maxUpdateTime);
        while (index < count) {
            params.put("startRow", index);
            List<LdVideosVo> dataList = firstVideosMapper.queryNewLdVideos(params);  //在mysql查询出视频数据

            if(dataList != null && !dataList.isEmpty()) {
                firstVideosMapper.deleteLdVideoInfoById(dataList);   //删除clickhouse中已经存在的来电秀视频数据
                firstVideosMapper.batchInsertUpdateLdVideo(dataList);  //重新同步最新的来电秀数据到clickhouse中
            }
            index = index + pageSize;
            log.info("同步来电秀视频数据，记录：{}", index);
        }
    }
}
