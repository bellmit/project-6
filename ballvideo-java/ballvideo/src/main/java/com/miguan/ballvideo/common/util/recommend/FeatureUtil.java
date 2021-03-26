package com.miguan.ballvideo.common.util.recommend;

import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.config.cache.RecommendCachePool;
import com.miguan.ballvideo.common.constants.RecommendConstant;
import com.miguan.ballvideo.entity.recommend.PublicInfo;
import com.miguan.ballvideo.entity.recommend.UserFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class FeatureUtil {
    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;

    public List<Map<String,Object>> makeFeatureList(List<String> videoList,UserFeature userFeature,Map<String,Integer> videoCatMap,boolean isABTest){
        PublicInfo publicInfo = userFeature.getPublicInfo();

        // 暂时不使用活跃天数和离线兴趣度特征
//        userFeature.setActiveDay(0);
//        userFeature.setCatFav(new LinkedHashMap<>());

        Map<String,Object> commonFeature = getCommonFeature(userFeature);
        Map<String,Map<String,Integer>> activeShowPlayList = getVideoListShowPlay(RecommendConstant.count_active+userFeature.getActiveDay()
                ,videoList);
        Map<String,Map<String,Integer>> modelShowPlayList = getVideoListShowPlay(publicInfo.getModel(),videoList);
        Map<String,Map<String,Integer>> cityShowPlayList = getVideoListShowPlay(userFeature.getCity(),videoList);
        Map<String,Map<String,Integer>> packageShowPlayList = getVideoListShowPlay(publicInfo.getPackageName(),videoList);
        Map<String,Map<String,Integer>> channelShowPlayList = getVideoListShowPlay(publicInfo.getChannel(),videoList);
        Map<String,Map<String,Integer>> osShowPlayList = getVideoListShowPlay(publicInfo.getOs(),videoList);
        Map<String,Map<String,Integer>> isNewShowPlayList = getVideoListShowPlay(publicInfo.isNew()?"true":"false",videoList);

        List<Map<String,Object>> listFeature = new ArrayList<>();
        for(String videoId:videoList){
            Map<String,Object> videoFeature = new LinkedHashMap<>();
            videoFeature.put("model_show", MapUtils.getInteger(modelShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("model_play", MapUtils.getInteger(modelShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("city_show", MapUtils.getInteger(cityShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("city_play", MapUtils.getInteger(cityShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("package_show", MapUtils.getInteger(packageShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("package_play", MapUtils.getInteger(packageShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("channel_show", MapUtils.getInteger(channelShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("channel_play", MapUtils.getInteger(channelShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("os_show", MapUtils.getInteger(osShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("os_play", MapUtils.getInteger(osShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("active_show", MapUtils.getInteger(activeShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("active_play", MapUtils.getInteger(activeShowPlayList.get(videoId),RecommendConstant.count_play,0));
            videoFeature.put("isnew_old_show", MapUtils.getInteger(isNewShowPlayList.get(videoId),RecommendConstant.count_show,0));
            videoFeature.put("isnew_old_play", MapUtils.getInteger(isNewShowPlayList.get(videoId),RecommendConstant.count_play,0));

            int catId = videoCatMap.get(videoId);
            videoFeature.put("cat_id", catId);
            double off_catFav = MapUtils.getDoubleValue(userFeature.getCatFav(), catId);
            videoFeature.put("off_catfav",off_catFav);
            double real_catFav = this.calculateRealTimeCatFav(userFeature.getCatPoolList(), catId);
            videoFeature.put("real_catfav",real_catFav);
            videoFeature.putAll(commonFeature);

            videoFeature.put("video_id",videoId);

            listFeature.add(videoFeature);
        }

        return listFeature;
    }

    private  Map<String,Object> getCommonFeature(UserFeature userFeature){
        Map<String,Object> commonFeature = new LinkedHashMap<>();
        PublicInfo publicInfo = userFeature.getPublicInfo();
        commonFeature.put("is_new",publicInfo.isNew()?1:0);
        commonFeature.put("package_name",publicInfo.getPackageName());
        commonFeature.put("os",publicInfo.getOs());
        commonFeature.put("channel",publicInfo.getChannel());
        commonFeature.put("active_day",userFeature.getActiveDay());
        commonFeature.put("city",userFeature.getCity());

        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        commonFeature.put("hour",hour);
        commonFeature.put("week",dayOfWeek);
        return commonFeature;
    }

    @SuppressWarnings("unchecked")
    public Map<String,Map<String,Integer>> getShowPlay(String keySuffix,List<String> videoList, List<Integer> dayList){
        Map<String,String> cacheKeyList = new LinkedHashMap<>();
        Map<String,Map<String,Integer>> rsMap = new LinkedHashMap<>();
        Cache localCache = RecommendCachePool.getRecommendCachePool().getCache("video_stat");
        videoList.forEach(videoId->{
            String localCacheKey = String.format(RecommendConstant.video_click_rate_count, "", videoId,keySuffix);
            Map<String,Integer> valFromLocalCache = (Map<String,Integer>) localCache.get(localCacheKey,Map.class);
            if(valFromLocalCache==null){
                for(int day:dayList){
                    String cacheKey = String.format(RecommendConstant.video_click_rate_count, day, videoId,keySuffix);
                    cacheKeyList.put(cacheKey,videoId);
                }
            }else{
                rsMap.put(videoId, valFromLocalCache);
            }
        });
//        log.warn("特征获取，prefix"+keySuffix+"redis："+cacheKeyList.size()/dayList.size()+", localcache:"+rsMap.size());

        if(cacheKeyList.isEmpty()){
            return rsMap;
        }

        try (Jedis con = jedisPool.getResource()) {
            Pipeline pipeline = con.pipelined();
            List<String> tmpVideoList = Lists.newArrayList();
            for (String ck : cacheKeyList.keySet()) {
                pipeline.hgetAll(ck);
                tmpVideoList.add(cacheKeyList.get(ck));
            }
            List<Object> results = pipeline.syncAndReturnAll();

            Map<String,Map<String,Integer>> tmpRsMap = new LinkedHashMap<>();
            for(int i=0;i<results.size();i++){
                Map<String,Integer> mapValue = (Map<String,Integer>)results.get(i);
                String vid = tmpVideoList.get(i);
                int showCount =  MapUtils.getIntValue(mapValue,RecommendConstant.count_show,0);
                int playCount = MapUtils.getIntValue(mapValue,RecommendConstant.count_play,0);
                if(rsMap.containsKey(vid)){
                    Map<String,Integer> srcMapValue = rsMap.get(vid);
                    showCount += MapUtils.getIntValue(srcMapValue, RecommendConstant.count_show,0);
                    playCount += MapUtils.getIntValue(srcMapValue,RecommendConstant.count_play,0);
                }

                mapValue.put(RecommendConstant.count_show,showCount);
                mapValue.put(RecommendConstant.count_play,playCount);
                rsMap.put(vid, mapValue);
                tmpRsMap.put(vid,mapValue);
            }

            for(String vid:tmpRsMap.keySet()){
                String localCacheKey = String.format(RecommendConstant.video_click_rate_count, "", vid,keySuffix);
                localCache.put(localCacheKey, tmpRsMap.get(vid));
            }
        }
        return rsMap;
    }

    public Map<String,Map<String,Integer>> getVideoListShowPlay(String keySuffix, List<String> videoList){
        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();
        List<Integer> dayList = Arrays.asList(todayOfMonth,yesterDayOfMonth);
        return getShowPlayMulti(keySuffix,videoList,dayList);
    }

    public Map<String,Map<String,Integer>> getShowPlayMulti(String keySuffix,List<String> videoList, List<Integer> dayList){
        return getShowPlay(keySuffix,videoList, dayList);
    }

    private double calculateRealTimeCatFav(List<String> catPoolList, Integer catId) {
        if (CollectionUtils.isEmpty(catPoolList)) {
            return 0.0D;
        }
        int index = catPoolList.indexOf(catId.toString());
        return index == -1 ? 0:new BigDecimal(0.9).pow(index).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public Map<String,Map<String,Integer>> getPlayAndTime(List<String> videoList, int dayOfMonth){
        List<String> cacheKeyList = new ArrayList<>();
        videoList.stream().forEach(videoId->{
            String cacheKey = String.format(RecommendConstant.video_count_detail, dayOfMonth, videoId);
            cacheKeyList.add(cacheKey);
        });
        Map<String,Map<String,Integer>> rsMap = new LinkedHashMap<>();
        try (Jedis con = jedisPool.getResource()) {
            Pipeline pipeline = con.pipelined();
            for (String ck : cacheKeyList) {
                pipeline.hgetAll(ck);
            }
            List<Object> results = pipeline.syncAndReturnAll();
            for(int i=0;i<results.size();i++){
                Map<String,Integer> hGetAllRs = (Map<String,Integer>)results.get(i);
                rsMap.put(videoList.get(i), hGetAllRs);
            }
        }
        return rsMap;
    }

    public Map<String,Map<String,Integer>> getPlayAndTimeMulti(List<String> videoList, List<Integer> dayList){
        Map<String,Map<String,Integer>> allDayMap = null;
        for(int day:dayList){
            Map<String,Map<String,Integer>> map = getPlayAndTime(videoList,day);
            if(null == allDayMap){
                allDayMap = map;
                continue;
            }
            for(Map.Entry<String,Map<String,Integer>> entry : map.entrySet()){
                String mapKey = entry.getKey();
                Map<String,Integer> mapValue = entry.getValue();
                Map<String,Integer> srcMapValue = allDayMap.get(mapKey);
                int playCount = MapUtils.getIntValue(srcMapValue, RecommendConstant.count_play,0)
                        + MapUtils.getIntValue(mapValue,RecommendConstant.count_play,0);
                int playTime = MapUtils.getIntValue(srcMapValue,RecommendConstant.count_play_time_r,0)
                        + MapUtils.getIntValue(mapValue,RecommendConstant.count_play_time_r,0);
                mapValue.put(RecommendConstant.count_play,playCount);
                mapValue.put(RecommendConstant.count_play_time_r,playTime);
                allDayMap.put(mapKey, mapValue);
            }
        }
        return allDayMap;
    }

    public Map<String,Map<String,Integer>> getVideoListPlayAndTime(List<String> videoList){
        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();
        List<Integer> dayList = Arrays.asList(todayOfMonth,yesterDayOfMonth);
        return getPlayAndTimeMulti(videoList, dayList);
    }

    public Map<String,Long> getVideoListPerPlayTime(List<String> videoList,UserFeature userFeature){
        Map<String,Map<String,Integer>> playAndTimeList = getVideoListPlayAndTime(videoList);
        List<Long> allPlayAndTime = getPlayAndTimeSmooth(userFeature);
        long playSmooth= allPlayAndTime.get(0);
        long playTimeSmooth = allPlayAndTime.get(1);
        log.warn("playsmooth:"+playSmooth+",playTimeSmooth:"+playTimeSmooth);

        long maxPPT = 20;
        Map<String,Long> videoPerPlayTimeMap = new LinkedHashMap<>();
        for (String vid:playAndTimeList.keySet()){
            Map<String,Integer> v = playAndTimeList.get(vid);
            Integer tmpPlay = v.get(RecommendConstant.count_play);
            Integer tmpPlayTime = v.get(RecommendConstant.count_play_time_r);
            Long tmpPPT = (tmpPlayTime+playTimeSmooth)/(tmpPlay+playSmooth);
            tmpPPT = Math.min(tmpPPT ,maxPPT);
            videoPerPlayTimeMap.put(vid, tmpPPT);
        }
        return videoPerPlayTimeMap;
    }

    public List<Long> getPlayAndTimeSmooth(UserFeature userFeature){
        long playBase = 10;
        long timeBase = 40;

        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();

        String allPlayKey = String.format(RecommendConstant.all_video_play, todayOfMonth);
        String playTimeRKey = String.format(RecommendConstant.all_video_play_time_r, todayOfMonth);
        String yesAllPlayKey = String.format(RecommendConstant.all_video_play, yesterDayOfMonth);
        String yesPlayTimeRKey = String.format(RecommendConstant.all_video_play_time_r, yesterDayOfMonth);
        try (Jedis con = jedisPool.getResource()) {
            List<String> valueList =con.mget(allPlayKey,playTimeRKey,yesAllPlayKey,yesPlayTimeRKey);
            long playTotal =Long.parseLong(valueList.get(0)) +Long.parseLong(valueList.get(2));
            long playTimeTotal =Long.parseLong(valueList.get(1)) +Long.parseLong(valueList.get(3));
            log.warn("playsmooth:playTotal:"+playTotal+",playTimeSmooth:"+playTimeTotal+",v1:"+valueList.get(0)+",v2:"+valueList.get(1));
            long timeBaseFromRedis = playTimeTotal/playTotal;
            if(playTotal <=0 ||  timeBaseFromRedis < timeBase || timeBaseFromRedis > timeBase * 3){
                timeBaseFromRedis = timeBase;
            }
            return Arrays.asList(playBase,playBase*timeBaseFromRedis);
        }
    }

}
