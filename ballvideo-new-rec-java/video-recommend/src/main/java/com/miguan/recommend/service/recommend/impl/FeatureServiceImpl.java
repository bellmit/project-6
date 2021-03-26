package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.*;
import com.miguan.recommend.entity.mongo.UserRawTags;
import com.miguan.recommend.service.IPService;
import com.miguan.recommend.service.mongo.ScenairoVideoService;
import com.miguan.recommend.service.mongo.UserOfflineLabelService;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import com.miguan.recommend.service.recommend.FeatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("featureService")
public class FeatureServiceImpl implements FeatureService, Runnable {

    @Resource(name = "recDB0Pool")
    private JedisPool recDB0Pool;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Resource(name = "recDB11Pool")
    private JedisPool recDB11Pool;
    @Autowired
    private IPService ipService;
    @Resource
    private UserOfflineLabelService userOfflineLabelService;
    @Resource
    private UserRawTagsService userRawTagsService;
    @Resource
    private ScenairoVideoService scenairoVideoService;

    private String uuid;
    private UserFeature userFeature;
    private List<String> hotVideos;
    private Map<String, Integer> hotVideosCat;
    private List<String> incentiveVideos;
    private Map<String, Integer> incentiveVideosCat;

    @Override
    public UserFeature initUserFeature(String userTag, String ip) {

        int activeDay = 0;
        int showCount = 0;
        int playCount = 0;
        Map<Integer, Double> catFav = new HashMap<Integer, Double>();
        Map<Integer, Double> catFavT = new HashMap<Integer, Double>();
        Map<Integer, Double> sceneFav = new HashMap<Integer, Double>();
        Map<Integer, Double> sceneFavT = new HashMap<Integer, Double>();
        List<UserRawTags> userRawTags = userRawTagsService.findByUUid(userTag);
        if (!CollectionUtils.isEmpty(userRawTags)) {
            Map<Integer, List<UserRawTags>> userRawTagsMap = userRawTags.stream().collect(Collectors.groupingBy(UserRawTags::getTag_id));
            log.debug("{} 推荐初始化用户特征数据>>{}", userTag, userRawTagsMap);
            if (userRawTagsMap.containsKey(MongoConstants.user_active_day)) {
                activeDay = userRawTagsMap.get(MongoConstants.user_active_day).get(0).getTag_value();
                log.debug("{} 推荐初始化用户特征-活跃天数>>{}", userTag, JSONObject.toJSONString(activeDay));
            }

            if (userRawTagsMap.containsKey(MongoConstants.cat_fav)) {
                List<UserRawTags> catFavList = userRawTagsMap.get(MongoConstants.cat_fav);
                catFavList.stream().forEach(e -> {
                    catFav.put(e.getTag_value(), e.getWeight());
                });
                log.debug("{} 推荐初始化用户特征-离线分类兴趣度>>{}", userTag, JSONObject.toJSONString(catFav));
            }
            if (userRawTagsMap.containsKey(MongoConstants.cat_fav_t)) {
                List<UserRawTags> catFavList = userRawTagsMap.get(MongoConstants.cat_fav_t);
                catFavList.stream().forEach(e -> {
                    catFavT.put(e.getTag_value(), e.getWeight());
                });
                log.debug("{} 推荐初始化用户特征-离线分类兴趣度>>{}", userTag, JSONObject.toJSONString(catFavT));
            }
            if (userRawTagsMap.containsKey(MongoConstants.scene_fav)) {
                List<UserRawTags> sceneFavList = userRawTagsMap.get(MongoConstants.scene_fav);
                sceneFavList.stream().forEach(e -> {
                    sceneFav.put(e.getTag_value(), e.getWeight());
                });
                log.debug("{} 推荐初始化用户特征-离线场景兴趣度>>{}", userTag, JSONObject.toJSONString(sceneFav));
            }
            if (userRawTagsMap.containsKey(MongoConstants.scene_fav_t)) {
                List<UserRawTags> sceneFavList = userRawTagsMap.get(MongoConstants.scene_fav_t);

                log.debug("{} 推荐初始化用户特征-离线场景兴趣度>>{}", userTag, JSONObject.toJSONString(sceneFavT));
            }

            if (userRawTagsMap.containsKey(MongoConstants.SHOW_COUNT)) {
                List<UserRawTags> showCountList = userRawTagsMap.get(MongoConstants.SHOW_COUNT);
                showCount = showCountList.get(0).getTag_value();
            }

            if (userRawTagsMap.containsKey(MongoConstants.PLAY_COUNT)) {
                List<UserRawTags> playCountList = userRawTagsMap.get(MongoConstants.PLAY_COUNT);
                playCount = playCountList.get(0).getTag_value();
            }
        }

        String[] ipInfo = ipService.getIpInfo(ip);
        String city = ipInfo.length > 2 ? ipInfo[3] : "";

        List<String> catPoolList = null;
        List<String> scenePoolList = null;
        List<String> catPoolListT = null;
        List<String> scenePoolListT = null;
        List<String> videoPoolList = null;
        try (Jedis con = recDB9Pool.getResource()) {
            // 获取用户标签池
            String catPool = con.hget(RedisCountConstant.USER_LIKE_CAT_POOL, userTag);
            String catPoolT = con.hget(RedisCountConstant.USER_LIKE_CAT_POOL_T, userTag);
            String scenePool = con.hget(RedisCountConstant.user_like_scene_pool, userTag);
            String scenePoolT = con.hget(RedisCountConstant.user_like_scene_pool_t, userTag);
            String videoPool = con.hget(RedisCountConstant.user_video_near_play, userTag);
            con.close();
            catPoolList = StringUtil.isEmpty(catPool) ? null : Arrays.asList(catPool.split(","));
            catPoolListT = StringUtil.isEmpty(catPoolT) ? null : Arrays.asList(catPoolT.split(","));
            // 获取用户场景池
            scenePoolList = StringUtil.isEmpty(scenePool) ? null : Arrays.asList(scenePool.split(","));
            scenePoolListT = StringUtil.isEmpty(scenePoolT) ? null : Arrays.asList(scenePoolT.split(","));
            // 获取用户最近播放视频池
            videoPoolList = StringUtil.isEmpty(videoPool) ? null : Arrays.asList(videoPool.split(","));
        }

        try(Jedis con = recDB11Pool.getResource()){
            if(con.exists(RedisUserConstant.user_play_count)){
                String userPlayCount = con.hget(RedisUserConstant.user_play_count, uuid);
                if(StringUtils.isNoneEmpty(userPlayCount)){
                    playCount = Integer.parseInt(userPlayCount);
                }
            }

            if(con.exists(RedisUserConstant.user_show_count)){
                String userShowCount = con.hget(RedisUserConstant.user_show_count, uuid);
                if(StringUtils.isNoneEmpty(userShowCount)){
                    showCount = Integer.parseInt(userShowCount);
                }
            }
        }
        log.info("{} 推荐获取到的mongo特征数据：activeDay>>{}, catFav>>{},catFavT>>{}, sceneFav>>{},sceneFavT>>{}, city>>{}",
                userTag, activeDay, catFav, catFavT, sceneFav, sceneFavT, city);
        return new UserFeature(activeDay, catPoolList, catPoolListT, scenePoolList, scenePoolListT, videoPoolList, catFav,
                sceneFav, city, catFavT, sceneFavT, showCount, playCount);
    }

    @Override
    public void saveFeatureToRedis(BaseDto baseDto, VideoRecommendDto recommendDto) {
        this.uuid = baseDto.getUuid();
        this.userFeature = baseDto.getUserFeature();
        this.hotVideos = recommendDto.getRecommendVideo();
        this.hotVideosCat = recommendDto.getRecommendVideoCat();
        this.incentiveVideos = recommendDto.getJlvideo();
        this.incentiveVideosCat = recommendDto.getJlvideoCat();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        // 获取用户离线计算的活跃天数、分类兴趣度
        int activeDay = userFeature.getActiveDay();
        Map<Integer, Double> catFav = userFeature.getCatFav();
        Map<Integer, Double> catFavT = userFeature.getCatFavT();
        List<String> catPoolList = userFeature.getCatPoolList();
        List<String> catPoolListT = userFeature.getCatPoolListT();
        Map<Integer, Double> sceneFav = userFeature.getSceneFav();
        Map<Integer, Double> sceneFavT = userFeature.getSceneFavT();
        List<String> scenePoolList = userFeature.getScenePoolList();
        List<String> scenePoolListT = userFeature.getScenePoolListT();
        try (Jedis con = recDB0Pool.getResource()) {
            hotVideos.stream().forEach(r -> set(con, uuid, r, hotVideosCat.get(r), activeDay, catFav, catPoolList, sceneFav, scenePoolList, catPoolListT, scenePoolListT, catFavT, sceneFavT));
            incentiveVideos.stream().forEach(r -> set(con, uuid, r, incentiveVideosCat.get(r), activeDay, catFav, catPoolList, sceneFav, scenePoolList, catPoolListT, scenePoolListT, catFavT, sceneFavT));
        }
    }

    private void set(Jedis con, String uuid, String videoId, Integer catid, int activeDay, Map<Integer, Double> catFav, List<String> catPoolList,
                     Map<Integer, Double> sceneFav, List<String> scenePoolList, List<String> catPoolListT, List<String> scenePoolListT,
                     Map<Integer, Double> catFavT, Map<Integer, Double> sceneFavT) {
        double off_catFav = 0.0D;
        double real_catFav = 0.0D;
        double off_sceneFav = 0.0D;
        double real_sceneFav = 0.0D;
        //转化
        double off_catFav_t = 0.0D;
        double real_catFav_t = 0.0D;
        double off_sceneFav_t = 0.0D;
        double real_sceneFav_t = 0.0D;
        if (catid != null) {
            if (!CollectionUtils.isEmpty(catFav)) {
                off_catFav = MapUtils.getDoubleValue(catFav, catid);
                off_catFav_t = MapUtils.getDoubleValue(catFavT, catid);
            }
            real_catFav = this.calculateRealTimeCatFav(catPoolList, catid.toString());
            real_catFav_t = this.calculateRealTimeCatFav(catPoolListT, catid.toString());

            // 场景兴趣度
            try {
                Integer sceneNum = scenairoVideoService.findScenarioNumIdFromMongoOrCache(Integer.parseInt(videoId));
                if (sceneNum != null) {
                    if (!CollectionUtils.isEmpty(sceneFav)) {
                        off_sceneFav = MapUtils.getDoubleValue(sceneFav, sceneNum);
                        off_sceneFav_t = MapUtils.getDoubleValue(sceneFavT, sceneNum);
                        log.debug("{} 推荐获取场景[{}]离线兴趣度>>{},{}", uuid, sceneNum, off_sceneFav, off_sceneFav_t);
                    }
                    real_sceneFav = this.calculateRealTimeSceneFav(scenePoolList, sceneNum.toString());
                    real_sceneFav_t = this.calculateRealTimeSceneFav(scenePoolListT, sceneNum.toString());
                }
                log.debug("{} 推荐获取场景[{}]实时兴趣度>>{},{}", uuid, sceneNum, real_sceneFav, real_sceneFav_t);
            } catch (Exception e) {
                log.error("{} 推荐获取视频[{}]场景ID异常", uuid, videoId);
            }
        }
        String key = String.format(RedisRecommendConstants.video_snapshoot, uuid, videoId);
        String value = activeDay + "," + off_catFav + "," + real_catFav + "," + off_sceneFav + "," + real_sceneFav + "," + real_catFav_t + "," + real_sceneFav_t + "," + off_catFav_t + "," + off_sceneFav_t;
        log.debug("推荐接口快照:key>>{}, value>>{}", key, value);
        con.setex(key, ExistConstants.thirty_minutes_seconds, value);
    }

    private double calculateRealTimeCatFav(List<String> catPoolList, String catId) {
        if (CollectionUtils.isEmpty(catPoolList)) {
            return 0.0D;
        }
        int index = catPoolList.indexOf(catId);
        return index == -1 ? 0 : new BigDecimal(0.9).pow(index).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private double calculateRealTimeSceneFav(List<String> scenePoolList, String sceneNum) {
        if (CollectionUtils.isEmpty(scenePoolList)) {
            return 0.0D;
        }
        int index = scenePoolList.indexOf(sceneNum.toString());
        return index == -1 ? 0 : new BigDecimal(0.9).pow(index).setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
