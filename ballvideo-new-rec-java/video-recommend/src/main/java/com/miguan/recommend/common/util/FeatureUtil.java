package com.miguan.recommend.common.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.*;
import com.miguan.recommend.common.config.cache.RecommendCachePool;
import com.miguan.recommend.common.constants.CommenConstant;
import com.miguan.recommend.common.constants.RedisCountConstant;
import com.miguan.recommend.entity.es.VideoEmbeddingEs;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.mapper.FirstVideosMapper;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.ScenairoVideoService;
import com.miguan.recommend.service.mongo.VideoHotspotService;
import com.miguan.recommend.service.recommend.EmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FeatureUtil {
    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;
    @Resource(name = "redisDB9Service")
    private RedisService redisDB9Service;
    @Resource(name = "redisDB11Service")
    private RedisService redisDB11Service;
    @Resource
    private ScenairoVideoService scenairoVideoService;
    @Resource
    private EmbeddingService embeddingService;
    @Resource
    private VideoHotspotService videoHotspotService;
    @Resource
    private FirstVideosMapper firstVideosMapper;

    /**
     * 前2日视频的统计信息
     */
    public static Map<String, VideoCount> twoDaysBeforeCount = new HashMap<String, VideoCount>();

    public List<Map<String, Object>> makeFeatureList(PredictDto predictDto, List<VideoHotspotVo> videoVoList) {
        List<String> vectorList = new ArrayList<String>();
        vectorList.add("");
        vectorList.add("");

        boolean isCtr = false;
        boolean isCvr = false;

        return this.baseMakeFeatureList(videoVoList, predictDto.getDevice_id(), predictDto.getUserFeature(), vectorList,
                predictDto.getChannel(), predictDto.getPackage_name(), predictDto.is_first(), predictDto.getModel(), predictDto.getOs(),
                isCtr, isCvr);
    }

    public List<Map<String, Object>> makeFeatureList(BaseDto baseDto, List<VideoHotspotVo> videoVoList, boolean isCtr, boolean isCvr) {
        PublicInfo publicInfo = baseDto.getPublicInfo();
        UserFeature userFeature = baseDto.getUserFeature();
        List<String> vectorList = new ArrayList<String>();
        vectorList.add("");
        vectorList.add("");
        //获取用户、视频向量
        if(isCtr || isCvr){
            vectorList = embeddingService.userEmbedding(publicInfo);
        }
        return this.baseMakeFeatureList(videoVoList, publicInfo.getUuid(),userFeature, vectorList,
                publicInfo.getChannel(), publicInfo.getPackageName(), publicInfo.isNew(), publicInfo.getModel(), publicInfo.getOs(), isCtr, isCvr);
    }

    private List<Map<String, Object>> baseMakeFeatureList(List<VideoHotspotVo> videoVoList, String userTag, UserFeature userFeature,
                                                          List<String> vectorList, String channel, String packageName, boolean isNew,
                                                          String model, String os, boolean isCtr, boolean isCvr) {
        String userVector = vectorList.get(0);
        if(StringUtil.isBlank(userVector)){
            userVector = "[0,0,0,0,0,0,0,0]";
        }
        String appVector = vectorList.get(1);
        if(StringUtil.isBlank(appVector)){
            appVector = "[0,0,0,0,0,0,0,0]";
        }


        String todayStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String userCatKey = String.format(RedisCountConstant.user_cat_count, todayStr, userTag);
        Map<String, String> userCatShow = redisDB9Service.hgetAll(userCatKey);

        Map<String, List<VideoHotspotVo>> videoMap = videoVoList.stream().collect(Collectors.groupingBy(VideoHotspotVo::getVideo_id));
        List<String> videoList = videoVoList.stream().map(VideoHotspotVo::getVideo_id).distinct().collect(Collectors.toList());
//        List<VideoHotspotVo> recVideosVoList = videoHotspotService.findFromMongoById(videoList);
//        Map<String,String> id2UrlMap = recVideosVoList.stream().collect(Collectors.toMap(VideoHotspotVo::getVideo_id, VideoHotspotVo::getVideo_url));
        Map<String, Object> commonFeature = this.getCommonFeature(userFeature, channel, packageName, isNew, os);
        Map<String, Map<String, Integer>> activeShowPlayList = getVideoListShowPlay(RedisCountConstant.count_active + userFeature.getActiveDay(), videoList);
        Map<String, Map<String, Integer>> modelShowPlayList = getVideoListShowPlay(model, videoList);
        Map<String, Map<String, Integer>> cityShowPlayList = getVideoListShowPlay(userFeature.getCity(), videoList);

        Map<String, Map<String, Integer>> packageShowPlayList = getVideoListShowPlay(packageName, videoList);
        Map<String, Map<String, Integer>> channelShowPlayList = getVideoListShowPlay(channel, videoList);
        Map<String, Map<String, Integer>> osShowPlayList = getVideoListShowPlay(os, videoList);
        Map<String, Map<String, Integer>> isNewShowPlayList = getVideoListShowPlay(isNew ? "true" : "false", videoList);

        List<Map<String, Object>> listFeature = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();
        long pt_start = System.currentTimeMillis();
        for (String videoId : videoList) {
            Map<String, Object> videoFeature = new LinkedHashMap<>();
            videoFeature.put("video_id", videoId);
            videoFeature.putAll(commonFeature);

            videoFeature.put("model_show", MapUtils.getInteger(modelShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("model_play", MapUtils.getInteger(modelShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("model_transfer", MapUtils.getInteger(modelShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("city_show", MapUtils.getInteger(cityShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("city_play", MapUtils.getInteger(cityShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("city_transfer", MapUtils.getInteger(cityShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("package_show", MapUtils.getInteger(packageShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("package_play", MapUtils.getInteger(packageShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("package_transfer", MapUtils.getInteger(packageShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("channel_show", MapUtils.getInteger(channelShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("channel_play", MapUtils.getInteger(channelShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("channel_transfer", MapUtils.getInteger(channelShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("os_show", MapUtils.getInteger(osShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("os_play", MapUtils.getInteger(osShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("os_transfer", MapUtils.getInteger(osShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("active_show", MapUtils.getInteger(activeShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("active_play", MapUtils.getInteger(activeShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("active_transfer", MapUtils.getInteger(activeShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));
            videoFeature.put("isnew_old_show", MapUtils.getInteger(isNewShowPlayList.get(videoId), RedisCountConstant.count_show, 0));
            videoFeature.put("isnew_old_play", MapUtils.getInteger(isNewShowPlayList.get(videoId), RedisCountConstant.count_play, 0));
            videoFeature.put("isNew_old_transfer", MapUtils.getInteger(isNewShowPlayList.get(videoId), RedisCountConstant.count_transfer, 0));

            VideoHotspotVo vo = videoMap.get(videoId).get(0);
            videoFeature.put("cat_id", vo.getCatid());
            double off_catFav = MapUtils.getDoubleValue(userFeature.getCatFav(), vo.getCatid());
            videoFeature.put("off_catfav", off_catFav);
            double off_catFav_t = MapUtils.getDoubleValue(userFeature.getCatFavT(), vo.getCatid());
            videoFeature.put("off_catFav_t", off_catFav_t);
            double real_catFav = this.calculateRealTimeCatFav(userFeature.getCatPoolList(), vo.getCatid());
            videoFeature.put("real_catfav", real_catFav);
            double real_catFav_t = this.calculateRealTimeCatFav(userFeature.getCatPoolListT(), vo.getCatid());
            videoFeature.put("real_catFav_t", real_catFav_t);

            videoFeature.put("video_time", vo.getVideo_time());
            videoFeature.put("weight0", vo.getWeights());
            videoFeature.put("weight1", vo.getWeights1());

            //场景特征暂时没使用，先注释
//            Integer videoSceneNum = scenairoVideoService.findScenarioNumIdFromMongoOrCache(Integer.parseInt(vo.getVideo_id()));
            videoFeature.put("cat_show", MapUtils.getIntValue(userCatShow, vo.getCatid().toString(), 0));
//            videoFeature.put("off_scenefav", videoSceneNum == null ? 0.0D : MapUtils.getDoubleValue(userFeature.getSceneFav(), videoSceneNum, 0.0D));
//            videoFeature.put("real_scenefav", videoSceneNum == null ? 0.0D : this.calculateRealTimeCatFav(userFeature.getScenePoolList(), videoSceneNum));
//            videoFeature.put("off_sceneFav_t", videoSceneNum == null ? 0.0D : MapUtils.getDoubleValue(userFeature.getSceneFavT(), videoSceneNum, 0.0D));
//            videoFeature.put("real_sceneFav_t", videoSceneNum == null ? 0.0D : this.calculateRealTimeCatFav(userFeature.getScenePoolListT(), videoSceneNum));

            //playTimeAvg  playOverCount  overShow
            VideoCount twoDaysCount = findVideoDaysCount(videoId, todayOfMonth, yesterDayOfMonth);
            long playTimeAvg = twoDaysCount.getVaildPlay() == 0 ? 0 : twoDaysCount.getPlayTimeR() / twoDaysCount.getVaildPlay();
            long playOverCount = twoDaysCount.getPlayOver();
            long overShow = twoDaysCount.getShow();
            videoFeature.put("play_time_avg", playTimeAvg);
            videoFeature.put("over_show", overShow);
            videoFeature.put("over_play", playOverCount);

            if(isCtr || isCvr){
                // 余弦相似度、app列表向量
                String videoVector = "[0,0,0,0,0,0,0,0]";
                String videoUrl = videoMap.get(videoId).get(0).getVideo_url();
                if(StringUtil.isNotBlank(videoUrl)){
                    VideoEmbeddingEs video = embeddingService.getVideoEmbeddingVector(String.valueOf(videoId), videoUrl);
                    if(video!= null && StringUtil.isNotBlank(video.getVector())){
                        videoVector = video.getVector();
                    }
                }

                Double userVideoSimilar = cal_cos(userVector, videoVector);
                videoFeature.put("user_vector", userVector);
                videoFeature.put("app_vector", appVector);
                videoFeature.put("video_vector", videoVector);
                videoFeature.put("user_video_similar", userVideoSimilar);
            }

            listFeature.add(videoFeature);
        }
        long pt_end = System.currentTimeMillis();
        log.info("视频个数{},获取视频特征时长{}",videoList.size(),(pt_end - pt_start));

        return listFeature;
    }

    private Double cal_cos(String videoEmbedding,String userEmbedding){
        List<Double> list1 = JSON.parseArray(videoEmbedding,Double.class);
        List<Double> list2 = JSON.parseArray(userEmbedding,Double.class);
        double sum =0;
        double sq1 = 0;
        double sq2 = 0;
        Double result = 0d;
        for (int i =0;i<list1.size();i++){
            sum +=list1.get(i)*list2.get(i);
            sq1 += list1.get(i)*list1.get(i);
            sq2 += list2.get(i)*list2.get(i);
        }
        result = sum/(Math.sqrt(sq1)*Math.sqrt(sq2));
//        System.out.println("余弦相似度="+result);
        return result.isNaN()?0d:result;
    }

    private Map<String, Object> getCommonFeature(UserFeature userFeature, String channel, String packageName, boolean isNew, String os) {
        Map<String, Object> commonFeature = new LinkedHashMap<>();
        commonFeature.put("is_new", isNew ? 1 : 0);
        commonFeature.put("package_name", packageName);
        commonFeature.put("os", os);
        commonFeature.put("channel", channel);
        commonFeature.put("active_day", userFeature.getActiveDay());
        commonFeature.put("city", userFeature.getCity());

        LocalDateTime localDateTime = LocalDateTime.now();
        int hour = localDateTime.getHour();
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        commonFeature.put("hour", hour);
        commonFeature.put("week", dayOfWeek);
        return commonFeature;
    }

    public Map<String, Map<String, Integer>> getShowPlay(String keySuffix, List<String> videoList, List<Integer> dayList) {
        Map<String, String> cacheKeyList = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> rsMap = new LinkedHashMap<>();
        Cache localCache = RecommendCachePool.getRecommendCachePool().getCache("video_stat");
        videoList.forEach(videoId -> {
            String localCacheKey = String.format(RedisCountConstant.video_click_rate_count, "", videoId, keySuffix);
            Map<String, Integer> valFromLocalCache = (Map<String, Integer>) localCache.get(localCacheKey, Map.class);
            if (valFromLocalCache == null) {
                for (int day : dayList) {
                    String cacheKey = String.format(RedisCountConstant.video_click_rate_count, day, videoId, keySuffix);
                    cacheKeyList.put(cacheKey, videoId);
                }
            } else {
                rsMap.put(videoId, valFromLocalCache);
            }
        });
//        log.warn("特征获取，prefix"+keySuffix+"redis："+cacheKeyList.size()/dayList.size()+", localcache:"+rsMap.size());

        if (cacheKeyList.isEmpty()) {
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

            Map<String, Map<String, Integer>> tmpRsMap = new LinkedHashMap<>();
            for (int i = 0; i < results.size(); i++) {
                Map<String, Integer> mapValue = (Map<String, Integer>) results.get(i);
                String vid = tmpVideoList.get(i);
                int showCount = MapUtils.getIntValue(mapValue, RedisCountConstant.count_show, 0);
                int playCount = MapUtils.getIntValue(mapValue, RedisCountConstant.count_play, 0);
                int transferCount = MapUtils.getIntValue(mapValue, RedisCountConstant.count_transfer, 0);
                if (rsMap.containsKey(vid)) {
                    Map<String, Integer> srcMapValue = rsMap.get(vid);
                    showCount += MapUtils.getIntValue(srcMapValue, RedisCountConstant.count_show, 0);
                    playCount += MapUtils.getIntValue(srcMapValue, RedisCountConstant.count_play, 0);
                    transferCount += MapUtils.getIntValue(srcMapValue, RedisCountConstant.count_transfer, 0);
                }

                mapValue.put(RedisCountConstant.count_show, showCount);
                mapValue.put(RedisCountConstant.count_play, playCount);
                mapValue.put(RedisCountConstant.count_transfer, transferCount);
                rsMap.put(vid, mapValue);
                tmpRsMap.put(vid, mapValue);
            }

            for (String vid : tmpRsMap.keySet()) {
                String localCacheKey = String.format(RedisCountConstant.video_click_rate_count, "", vid, keySuffix);
                localCache.put(localCacheKey, tmpRsMap.get(vid));
            }
        }catch (Exception e){
            return rsMap;
        }
        return rsMap;
    }

    public Map<String, Map<String, Integer>> getVideoListShowPlay(String keySuffix, List<String> videoList) {
        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();
        List<Integer> dayList = Arrays.asList(todayOfMonth, yesterDayOfMonth);
        return getShowPlayMulti(keySuffix, videoList, dayList);
    }

    public Map<String, Map<String, Integer>> getShowPlayMulti(String keySuffix, List<String> videoList, List<Integer> dayList) {
        return getShowPlay(keySuffix, videoList, dayList);
    }

    private double calculateRealTimeCatFav(List<String> catPoolList, Integer catId) {
        if (CollectionUtils.isEmpty(catPoolList)) {
            return 0.0D;
        }
        int index = catPoolList.indexOf(catId.toString());
        return index == -1 ? 0 : new BigDecimal(0.9).pow(index).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public Map<String, Map<String, Integer>> getPlayAndTime(List<String> videoList, int dayOfMonth) {
        List<String> cacheKeyList = new ArrayList<>();
        videoList.stream().forEach(videoId -> {
            String cacheKey = String.format(RedisCountConstant.video_count_detail, dayOfMonth, videoId);
            cacheKeyList.add(cacheKey);
        });
        Map<String, Map<String, Integer>> rsMap = new LinkedHashMap<>();
        try (Jedis con = jedisPool.getResource()) {
            Pipeline pipeline = con.pipelined();
            for (String ck : cacheKeyList) {
                pipeline.hgetAll(ck);
            }
            List<Object> results = pipeline.syncAndReturnAll();
            for (int i = 0; i < results.size(); i++) {
                Map<String, Integer> hGetAllRs = (Map<String, Integer>) results.get(i);
                rsMap.put(videoList.get(i), hGetAllRs);
            }
        }
        return rsMap;
    }

    public Map<String, Map<String, Integer>> getPlayAndTimeMulti(List<String> videoList, List<Integer> dayList) {
        Map<String, Map<String, Integer>> allDayMap = null;
        for (int day : dayList) {
            Map<String, Map<String, Integer>> map = getPlayAndTime(videoList, day);
            if (null == allDayMap) {
                allDayMap = map;
                continue;
            }
            for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                String mapKey = entry.getKey();
                Map<String, Integer> mapValue = entry.getValue();
                Map<String, Integer> srcMapValue = allDayMap.get(mapKey);
                int playCount = MapUtils.getIntValue(srcMapValue, RedisCountConstant.count_play, 0)
                        + MapUtils.getIntValue(mapValue, RedisCountConstant.count_play, 0);
                int playTime = MapUtils.getIntValue(srcMapValue, RedisCountConstant.count_play_time_r, 0)
                        + MapUtils.getIntValue(mapValue, RedisCountConstant.count_play_time_r, 0);
                mapValue.put(RedisCountConstant.count_play, playCount);
                mapValue.put(RedisCountConstant.count_play_time_r, playTime);
                allDayMap.put(mapKey, mapValue);
            }
        }
        return allDayMap;
    }

    public Map<String, Map<String, Integer>> getVideoListPlayAndTime(List<String> videoList) {
        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();
        List<Integer> dayList = Arrays.asList(todayOfMonth, yesterDayOfMonth);
        return getPlayAndTimeMulti(videoList, dayList);
    }

    public Map<String, Long> getVideoListPerPlayTime(List<String> videoList, UserFeature userFeature) {
        Map<String, Map<String, Integer>> playAndTimeList = getVideoListPlayAndTime(videoList);
        List<Long> allPlayAndTime = getPlayAndTimeSmooth(userFeature);
        long playSmooth = allPlayAndTime.get(0);
        long playTimeSmooth = allPlayAndTime.get(1);
        log.warn("playsmooth:" + playSmooth + ",playTimeSmooth:" + playTimeSmooth);

        long maxPPT = 20;
        Map<String, Long> videoPerPlayTimeMap = new LinkedHashMap<>();
        for (String vid : playAndTimeList.keySet()) {
            Map<String, Integer> v = playAndTimeList.get(vid);
            Integer tmpPlay = v.get(RedisCountConstant.count_play);
            Integer tmpPlayTime = v.get(RedisCountConstant.count_play_time_r);
            Long tmpPPT = (tmpPlayTime + playTimeSmooth) / (tmpPlay + playSmooth);
            tmpPPT = Math.min(tmpPPT, maxPPT);
            videoPerPlayTimeMap.put(vid, tmpPPT);
        }
        return videoPerPlayTimeMap;
    }

    public List<Long> getPlayAndTimeSmooth(UserFeature userFeature) {
        long playBase = 10;
        long timeBase = 40;

        LocalDate localDate = LocalDate.now();
        int todayOfMonth = localDate.getDayOfMonth();
        int yesterDayOfMonth = localDate.minusDays(1L).getDayOfMonth();

        String allPlayKey = String.format(RedisCountConstant.all_video_play, todayOfMonth);
        String playTimeRKey = String.format(RedisCountConstant.all_video_play_time_r, todayOfMonth);
        String yesAllPlayKey = String.format(RedisCountConstant.all_video_play, yesterDayOfMonth);
        String yesPlayTimeRKey = String.format(RedisCountConstant.all_video_play_time_r, yesterDayOfMonth);
        try (Jedis con = jedisPool.getResource()) {
            List<String> valueList = con.mget(allPlayKey, playTimeRKey, yesAllPlayKey, yesPlayTimeRKey);
            long playTotal = Long.parseLong(valueList.get(0)) + Long.parseLong(valueList.get(2));
            long playTimeTotal = Long.parseLong(valueList.get(1)) + Long.parseLong(valueList.get(3));
            log.warn("playsmooth:playTotal:" + playTotal + ",playTimeSmooth:" + playTimeTotal + ",v1:" + valueList.get(0) + ",v2:" + valueList.get(1));
            long timeBaseFromRedis = playTimeTotal / playTotal;
            if (playTotal <= 0 || timeBaseFromRedis < timeBase || timeBaseFromRedis > timeBase * 3) {
                timeBaseFromRedis = timeBase;
            }
            return Arrays.asList(playBase, playBase * timeBaseFromRedis);
        }
    }

    private VideoCount findVideoDaysCount(String videoId, int... days) {
        int todayOfMonth = LocalDate.now().getDayOfMonth();
        List<VideoCount> videoCounts = new ArrayList();
        for (int day : days) {
            VideoCount dayCount = null;
            if (todayOfMonth == day) {
                dayCount = this.findVideoCount(videoId, todayOfMonth, null, true);
            } else {
                dayCount = this.findVideoCount(videoId, day, null, false);
            }
            videoCounts.add(dayCount);
        }
        VideoCount[] videoCounts1 = {};
        videoCounts1 = videoCounts.toArray(videoCounts1);
        return sumVideoCount(videoCounts1);
    }

    private VideoCount findVideoCount(String videoId, int day, String createDay, boolean isToday) {
        String mapKey = videoId + "_" + day;
        VideoCount bo = null;
        if (twoDaysBeforeCount.containsKey(mapKey)) {
            bo = twoDaysBeforeCount.get(mapKey);
        } else {
            String key = String.format(RedisCountConstant.video_count_detail, day, videoId);
            Map<String, String> videoCount = redisDB9Service.hgetAll(key);
            if (CollectionUtils.isEmpty(videoCount)) {
                bo = new VideoCount(videoId, 0, 0, 0, 0, 0);
            } else {
                long show = StringUtils.isEmpty(videoCount.get(CommenConstant.count_show)) ? 0 : Long.parseLong(videoCount.get(CommenConstant.count_show));
                long play = StringUtils.isEmpty(videoCount.get(CommenConstant.count_play)) ? 0 : Long.parseLong(videoCount.get(CommenConstant.count_play));
                long vaildPlay = StringUtils.isEmpty(videoCount.get(CommenConstant.count_vaild_play)) ? 0 : Long.parseLong(videoCount.get(CommenConstant.count_vaild_play));
                long playTimeR = StringUtils.isEmpty(videoCount.get(CommenConstant.count_play_time_r)) ? 0 : Long.parseLong(videoCount.get(CommenConstant.count_play_time_r));
                long playOver = StringUtils.isEmpty(videoCount.get(CommenConstant.count_play_over)) ? 0 : Long.parseLong(videoCount.get(CommenConstant.count_play_over));
                bo = new VideoCount(videoId, show, play, vaildPlay, playTimeR, playOver);
            }
            if (!isToday) {
                twoDaysBeforeCount.put(mapKey, bo);
            }
        }
        return bo;
    }

    private VideoCount sumVideoCount(VideoCount... counts) {
        long show = 1L;
        long play = 0L;
        long vaildPlay = 0L;
        long playTimeR = 0L;
        long playOver = 0L;
        for (VideoCount bo : counts) {
            show += bo.getShow();
            play += bo.getPlay();
            vaildPlay += bo.getVaildPlay();
            playTimeR += bo.getPlayTimeR();
            playOver += bo.getPlayOver();
        }
        return new VideoCount(show, play, vaildPlay, playTimeR, playOver);
    }
}
