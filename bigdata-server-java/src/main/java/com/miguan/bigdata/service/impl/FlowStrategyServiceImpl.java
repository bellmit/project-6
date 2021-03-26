package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.base.Joiner;
import com.miguan.bigdata.common.constant.RedisKeyConstants;
import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.entity.SysConfig;
import com.miguan.bigdata.mapper.FlowStrategyMapper;
import com.miguan.bigdata.service.FlowStrategyService;
import com.miguan.bigdata.service.SysConfigService;
import com.miguan.bigdata.vo.IncentiveVideoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import tool.util.ArrayUtil;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 流量策略需要的数据service
 **/
@Slf4j
@Service
public class FlowStrategyServiceImpl implements FlowStrategyService {

    @Resource
    private FlowStrategyMapper flowStrategyMapper;

    @Resource
    private RedisClient redisClient;

    @Resource
    SysConfigService sysConfigService;

    /**
     * 每个分类最少保留的激励视频数
     */
    private static final int INCENTIVE_VIDEO_CAT_MIN_COUNT = 10;

    /**
     * 统计出需要入库的激励视频
     * @param limtNum 每个分类每日入库数
     * @return
     */
    public List<Long> staInIncentiveVideos(Integer limtNum) {
        List<IncentiveVideoVo> list = this.staInIncentiveVideosList(limtNum);
        List<Long> incentiveVideos = list.stream().map(IncentiveVideoVo::getVideoId).collect(Collectors.toList());
        return incentiveVideos;
    }

    /**
     * 获取入库视频缓存
     *
     * @return
     */
    public List<IncentiveVideoVo> getInIncentiveVideosCache() {
        String cacheKey = this.getInIncentiveVideoCacheKey();
        String cacheResult = redisClient.get(cacheKey);
        List<IncentiveVideoVo> incentiveVideos = null;
        if (StringUtils.isNotBlank(cacheResult)) {
            JSONArray jsonArray = JSONArray.parseArray(cacheResult);
            incentiveVideos = jsonArray.toJavaList(IncentiveVideoVo.class);
        }
        return incentiveVideos;
    }

    List<Long> getUnInVideoList() {
        SysConfig config = sysConfigService.getConfig("XYSP_NEW_USER_APPOINT_VIDEO");
        List<Long> data = new ArrayList<>();
        if (config != null) {
            String value = (String) config.getValue();
            Map maps = (Map)JSON.parse(value);
            if (CollectionUtils.isNotEmpty(maps)) {
                for (Object map : maps.entrySet()){
                    String key = (String) ((Map.Entry)map).getKey();
                    List<String> list = (List<String>) ((Map.Entry)map).getValue();
                    if (!"排除分类".equals(key)) {
                        list.forEach( str -> {
                            Long videoId = Long.parseLong(str);
                            data.add(videoId);
                        });
                    }
                }
            }

        }
        return data;
    }

    /**
     * 计出需要入库的激励视频列表
     *
     * @param limtNum
     * @return
     */
    private List<IncentiveVideoVo> staInIncentiveVideosList(Integer limtNum) {
        limtNum = (limtNum == null ? 1 : limtNum);
        Date today = new Date();
        int startDay = Integer.parseInt(DateUtil.dateStr7(DateUtil.rollDay(today, -300)));
        int endDay = Integer.parseInt(DateUtil.dateStr7(DateUtil.rollDay(today, -1)));

        List<Long> unInVideoList = getUnInVideoList();
        List<IncentiveVideoVo> list = flowStrategyMapper.staInIncentiveVideos(unInVideoList, startDay, endDay); //统计入库的激励视频基础数据(近3天的播放数大于500的数据)
        Map<Long, List<IncentiveVideoVo>> map = list.stream().collect(Collectors.groupingBy(IncentiveVideoVo::getCatId));
        List<IncentiveVideoVo> incentiveVideoList = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            return incentiveVideoList;
        }

        //取播放率在各分类前30的视频
        for (Map.Entry<Long, List<IncentiveVideoVo>> entry : map.entrySet()) {
            List<IncentiveVideoVo> ivList = entry.getValue();
            //根据播放率降序排序
            ivList = ivList.stream().sorted(Comparator.comparing(IncentiveVideoVo::getVplayRate, Comparator.reverseOrder())).collect(Collectors.toList());
            int index = ivList.size() > 30 ? 30 : ivList.size();
            ivList = ivList.subList(0, index);
            map.put(entry.getKey(), ivList);
        }

        //在播放率的排名基础上,取完播率25-30的视频(由新增视频数决定，视频数为2，则取倒数2个视频）)
        for (Map.Entry<Long, List<IncentiveVideoVo>> entry : map.entrySet()) {
            List<IncentiveVideoVo> ivList = entry.getValue();
            //根据完播放率升序排序
            ivList = ivList.stream().sorted(Comparator.comparing(IncentiveVideoVo::getAllPlayRate)).collect(Collectors.toList());
            int index = ivList.size() > limtNum ? limtNum : ivList.size();
            ivList = ivList.subList(0, index);
            List<Long> catIvList = ivList.stream().map(IncentiveVideoVo::getVideoId).collect(Collectors.toList());
            incentiveVideoList.addAll(ivList);
        }

        String cacheKey = this.getInIncentiveVideoCacheKey();
        redisClient.set(cacheKey, JSON.toJSONString(incentiveVideoList), RedisKeyConstants.IN_INCENTIVE_CACHE_EXPIRES);
        return incentiveVideoList;
    }

    /**
     *出库视频数
     *
     * @param retainNum 保留数
     * @return
     */
    public List<Long> staOutIncentiveVideos(int retainNum) {

        List<Long> incentiveVideos = new ArrayList<>();
        List<IncentiveVideoVo> videoList = flowStrategyMapper.getIncentiveVideoList(); //总激励视频数
        if (videoList == null || videoList.size() <= retainNum) {
            return incentiveVideos;
        }
        int incentiveVideoCount = videoList.size();
        int outNum = incentiveVideoCount - retainNum; //出库视频数

        Date today = new Date();
        int startDay = Integer.parseInt(DateUtil.dateStr7(DateUtil.rollDay(today, -30)));
        int endDay = Integer.parseInt(DateUtil.dateStr7(DateUtil.rollDay(today, -1)));
        List<IncentiveVideoVo> statList = flowStrategyMapper.staOutIncentiveVideos(startDay, endDay); //统计近一个月的播放数据等
        if (statList == null) {
            statList = new ArrayList<>();
        }
        List<IncentiveVideoVo> inList = this.getInIncentiveVideosCache(); //每天入库列表
        if (inList == null) {
            inList = new ArrayList<>();
        }

        Map<Long, IncentiveVideoVo> inVideoMap = inList.stream().collect(Collectors.toMap(vo -> vo.getVideoId(), vo -> vo)); //videoId关联入库视频
        Map<Long, IncentiveVideoVo> statVideoMap = statList.stream().collect(Collectors.toMap(IncentiveVideoVo::getVideoId, vo -> vo)); //统计视频id关联
        videoList.forEach(videoVo -> {
            videoVo.setAllPlayRate(0d);
            videoVo.setVplayRate(0d);
            videoVo.setIsNew(0);
            Long videoId = videoVo.getVideoId();
            IncentiveVideoVo newInVideoVo = inVideoMap.get(videoId);
            IncentiveVideoVo statVideoVo = statVideoMap.get(videoId);
            if (newInVideoVo != null) {
                videoVo.setIsNew(1);
            }
            if (statVideoVo != null) {
                videoVo.setAllPlayRate(statVideoVo.getAllPlayRate());
                videoVo.setVplayRate(statVideoVo.getVplayRate());
            }
        });
        Map<Long, List<IncentiveVideoVo>> allVideoMap = videoList.stream().collect(Collectors.groupingBy(IncentiveVideoVo::getCatId)); //总视频
        Map<Long, List<IncentiveVideoVo>> inCatMap = inList.stream().collect(Collectors.groupingBy(IncentiveVideoVo::getCatId)); //catId关联入库视频

        //保留每个分类的前十个激励视频
        List<IncentiveVideoVo> outList = new ArrayList<>();
        for (Map.Entry<Long, List<IncentiveVideoVo>> entry : allVideoMap.entrySet()) {
            Long catId = entry.getKey();

            List<IncentiveVideoVo> catList = entry.getValue();
            catList = catList.stream().sorted(Comparator.comparing(IncentiveVideoVo::getIsNew).thenComparing(IncentiveVideoVo::getVplayRate).reversed()).collect(Collectors.toList());
            int newCatInVideoCount = 0; //该分类新入库数
            List<IncentiveVideoVo> newInVideoList = inCatMap.get(catId);
            if (newInVideoList != null && newInVideoList.size() > 0) {
                newCatInVideoCount = newInVideoList.size();
            }

            int retainCount = FlowStrategyServiceImpl.INCENTIVE_VIDEO_CAT_MIN_COUNT - newCatInVideoCount;
            retainCount = retainCount > 0 ? retainCount : newCatInVideoCount;
            if (catList.size() > retainCount) {
                catList = catList.subList(retainCount, catList.size());
                outList.addAll(catList);
            }
        }
        //取有效播放率倒数的2 * outNum数
        outList = outList.stream().sorted(Comparator.comparing(IncentiveVideoVo::getVplayRate)).collect(Collectors.toList());
        int doubleOutNum = outList.size() > 2 * outNum ? 2 * outNum : outList.size();
        outList = outList.subList(0, doubleOutNum);
        //取完播率倒数outNum数
        outList = outList.stream().sorted(Comparator.comparing(IncentiveVideoVo::getAllPlayRate)).collect(Collectors.toList());
        outNum = outList.size() > outNum ? outNum : outList.size();
        outList = outList.subList(0, outNum);
        outList.forEach(incentiveVideoVo -> {
            incentiveVideos.add(incentiveVideoVo.getVideoId());
        });

        return incentiveVideos;
    }


    /**
     * 获取入库激励视频缓存key
     *
     * @return
     */
    public String getInIncentiveVideoCacheKey() {
        Date today = new Date();
        String date = DateUtil.dateStr7(today);
        String cacheKey = RedisKeyConstants.IN_INCENTIVE_CACHE_PREFIX + date;
        return cacheKey;
    }



}