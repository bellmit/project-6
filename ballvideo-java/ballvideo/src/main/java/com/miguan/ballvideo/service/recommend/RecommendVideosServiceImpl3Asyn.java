package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.RecommendConstant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.PackageUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.recommend.FeatureUtil;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.entity.recommend.UserFeature;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import com.miguan.ballvideo.vo.video.FirstVideosVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 推荐0.3将用户兴趣标签的2个视频替换成使用spark计算出来的视频
 * 推荐视频
 */
@Service
@Slf4j
public class RecommendVideosServiceImpl3Asyn {

    @Resource
    private VideoCacheService videoCacheService;
    @Resource
    private ClUserService clUserService;
    @Resource
    private AdvertService advertService;
    @Resource
    private VideoGatherService videoGatherService;
    @Resource
    private VideosCatMapper videosCatMapper;
    @Resource
    private MarketAuditService marketAuditService;
    @Autowired
    private FindRecommendVideosServiceImpl findRecommendVideosService;
    @Autowired
    private FindRecommendPoolVideosServiceImpl findRecommendPoolVideosService;
    @Autowired
    private FindRecommendJLVideosServiceImpl findRecommendJLVideosService;
    @Autowired
    private FindRecommendCatidServiceImpl findRecommendCatidService;
    @Autowired
    private FindRecommendEsServiceImpl findRecommendEsService;
    @Autowired
    private ClUserVideosService clUserVideosService;
    @Resource
    private RedisService redisService;
    @Autowired
    private FeatureUtil featureUtil;
    @Autowired
    private PredictServiceImpl predictService;
    @Resource(name = "recDB9Pool")
    private JedisPool jedisPool;
    @Autowired
    private BloomFilterService bloomFilterService;

    Function<List<Integer>, Integer> function = catids -> {
        int rn = RandomUtils.nextInt(1, 7);
        if (rn < 4) {
            return catids.get(0);
        } else if (rn < 6) {
            return catids.size() < 2 ? catids.get(catids.size() - 1) : catids.get(1);
        }
        return catids.size() < 3 ? catids.get(catids.size() - 1) : catids.get(2);
    };
    ExecutorService executor = new ThreadPoolExecutor(32, 200, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    /**
     * 推荐
     *
     * @param params
     * @return
     */
    public FirstVideos161Vo getRecommendVideos(VideoParamsDto params, AbTestAdvParamsVo queueVo) {
        String uuid = params.getUuid();
        if (StringUtils.isBlank(uuid)) {
            throw new NullPointerException("未获取到uuid");
        }
        long pt = System.currentTimeMillis();
        FirstVideos161Vo vo = getVideos(uuid, params, queueVo);
        log.warn("{} 推荐0.3总耗时：{}", uuid, (System.currentTimeMillis() - pt));
        return vo;
    }

    private FirstVideos161Vo getVideos(String uuid, VideoParamsDto params, AbTestAdvParamsVo queueVo) {
        //过滤市场审核开关屏蔽的分类
        MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(params.getChannelId(), params.getAppVersion());
        //屏蔽不生效分类
        List<String> catIds = videosCatMapper.queryCatIdsList(params.getAppPackage());
        if (catIds == null) {
            catIds = Lists.newArrayList();
        }
        List<Integer> excludeCatid = null;
        List<Integer> excludeCollectid = null;
        if (marketAudit != null && isNotBlank(marketAudit.getCatIds())) {
            String[] excludeCatidStrArray = marketAudit.getCatIds().split(",");
            CollectionUtils.addAll(catIds, excludeCatidStrArray);
        }
        if (marketAudit != null && isNotBlank(marketAudit.getGatherIds())) {
            String[] excludeCollectidArray = marketAudit.getGatherIds().split(",");
            excludeCollectid = Stream.of(excludeCollectidArray).map(Integer::valueOf).collect(Collectors.toList());
        }
        if (!catIds.isEmpty()) {
            excludeCatid = catIds.stream().filter(e -> e != null).map(Integer::valueOf).collect(Collectors.toList());
        }
        //获取用户3个标签，不足3个，在6个热度标签中取
        List<Integer> userCatids = findRecommendCatidService.getUserCatids(uuid, params.getChannelId());
        int firstCatid = userCatids.get(0);
        //过滤市场屏蔽的分类
        if (!isEmpty(excludeCatid)) {
            userCatids.removeAll(excludeCatid);
        }
        //用第1个标签获取此标签最接近的热度标签(热度标签)6个，排除屏蔽的标签
        List<Integer> hotspotCatids = findRecommendCatidService.getHotspotCatids(firstCatid, excludeCatid);
        int diffCount = 3 - userCatids.size();
        //补齐用户标签,不足3个，在6个热度标签中取
        if (diffCount > 0 && !isEmpty(hotspotCatids)) {
            if (hotspotCatids.size() < diffCount) {
                diffCount = hotspotCatids.size();
            }
            for (int i = 0; i < diffCount; i++) {
                userCatids.add(hotspotCatids.get(i));
            }
        }
        //热度标签去除用户标签。注：为什么要这样写代码，是因为上一步要用热度标签补足用户兴趣标签
        hotspotCatids = ListUtils.subtract(hotspotCatids, userCatids);
        if (isEmpty(userCatids)) {
            userCatids.add(firstCatid);
        }
        if (isEmpty(hotspotCatids)) {
            hotspotCatids.add(firstCatid);
        }
        List<IncentiveVideoHotspot> jlvideos = new ArrayList<>();

        //激励视频配置参数
        Map<String, Object> incentiveMap = AdvUtils.getIncentiveInfo(params.getMobileType(), params.getAppPackage(), queueVo,1);
        Integer isShowIncentive = incentiveMap.get("isShowIncentive")==null? 0 : Integer.valueOf(incentiveMap.get("isShowIncentive").toString());
        Integer position = incentiveMap.get("position")==null? 0 : Integer.valueOf(incentiveMap.get("position").toString());
        String incentiveVideoRate = incentiveMap.get("incentiveVideoRate")==null? "0" : incentiveMap.get("incentiveVideoRate").toString();

        long pt = System.currentTimeMillis();
        //获取推荐视频
        boolean isNewUUID = redisService.exits(RedisKeyConstant.UUID_KEY + uuid);
        List<String> searchVideo = getRecommendVideo(params, hotspotCatids, userCatids, excludeCatid, excludeCollectid, jlvideos, isNewUUID, params.isABTest());
        log.warn("{} 推荐0.3耗时：{}", uuid, (System.currentTimeMillis() - pt));
        //根据视频id查询es
        List<Videos161Vo> rvideos = findRecommendEsService.list(searchVideo, incentiveVideoRate);
        List<Videos161Vo> firstVideos = new ArrayList<>();
        if (isNotEmpty(rvideos)) {
            //lzhong 新排序
            // 获取所有非激励视频
            List<Videos161Vo> notjl = rvideos.stream().filter(e -> e.getIncentiveVideo() == null || e.getIncentiveVideo().intValue() != 1).collect(Collectors.toList());
            firstVideos.addAll(notjl);

            List<Videos161Vo> jl = rvideos.stream().filter(e -> e.getIncentiveVideo() != null && e.getIncentiveVideo().intValue() == 1).collect(Collectors.toList());
            // 实验中的新用户，激励视频放到最后
            if (isNewUUID) {
                firstVideos.addAll(jl);
            } else {
                //获取所有激励视频,并将其放到后台配置得相应位置
                position = position > searchVideo.size() ? searchVideo.size() -1 : position <= 0 ? 0 : (position - 1);
                firstVideos.addAll(position, jl);
            }
            jl.clear();
        }
        //查询视频数据和广告数据, 记录已经浏览ID
        if (isNotEmpty(firstVideos)) {


            fillLoginInfo(params, firstVideos);
            //如果大于2.5版本，则不返回集合其余信息
            if (VersionUtil.compareIsHigh(Constant.APPVERSION_250, params.getAppVersion())) {
                //2.1.0新增视频合集展示
                fillGatherVideos(firstVideos);
            }
            videoCacheService.fillParams(firstVideos);
            //视频作者头像和名字取用户表（虚拟用户或者真是用户）
            clUserService.packagingUserAndVideos(firstVideos);
        }

        FirstVideos161Vo firstVideosNewVo = new FirstVideos161Vo();
        //是否返回广告
        if (VersionUtil.isBetween(Constant.APPVERSION_253, Constant.APPVERSION_257, params.getAppVersion()) && Constant.ANDROID.equals(params.getMobileType())) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
            firstVideos.clear();
        } else {
            //随机2个广告
            String channelId = params.getChannelId();
            params.setChannelId(ChannelUtil.filter(channelId));
            Map<String, Object> adverMap = new HashMap<>();
            String appVersion = params.getAppVersion();
            adverMap.put("marketChannelId", params.getChannelId());
            adverMap.put("channelId", params.getChannelId());
            adverMap.put("appVersion", appVersion);
            adverMap.put("positionType", params.getPositionType());
            adverMap.put("mobileType", params.getMobileType());
            adverMap.put("permission", params.getPermission());
            adverMap.put("appPackage", PackageUtil.getAppPackage(params.getAppPackage(), params.getMobileType()));
            //V2.5.0走新广告逻辑
            packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, adverMap, queueVo);
        }
        //重新返回占比给前端
        firstVideosNewVo.setVideoDuty("4,3,1");
        findRecommendVideosService.recordHistory(uuid, searchVideo);
        findRecommendJLVideosService.recordHistory(uuid, jlvideos);
        return firstVideosNewVo;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRecommendVideo(VideoParamsDto inputParams,
                                 List<Integer> hotspotCatids, List<Integer> userCatids,
                                 List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                 List<IncentiveVideoHotspot> jlvideos,
                                 boolean isNewUUID, boolean isABTest) {
        String uuid = inputParams.getUuid();
        List<String> recVideoList = new ArrayList<>();

        List<Object> videoInfoForRecList;
        videoInfoForRecList = getVideoFromCache(inputParams,hotspotCatids,userCatids,excludeCatid,excludeCollectid,jlvideos,isNewUUID,isABTest);

        if(videoInfoForRecList == null || videoInfoForRecList.isEmpty()){
            return recVideoList;
        }

        Map<String,BigDecimal> videoPlayRateMap = (Map<String,BigDecimal>)videoInfoForRecList.get(0);
        Map<String,Integer> videoCatMap = (Map<String,Integer>)videoInfoForRecList.get(1);

        int needCount = 7;
        int oneCatLimit = 2;
        int limitMulti = 20;
        if(inputParams.getUserFeature().getPublicInfo().isNew()){
            oneCatLimit = 3;
            limitMulti = 2;
        }

        recVideoList= videoTopK(videoPlayRateMap,videoCatMap,needCount,oneCatLimit, limitMulti);
        recVideoList.addAll(jlvideos.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        log.warn("推荐0.3此次给uuid({})推荐了 {} 个视频，其中激励视频 {} 个, 推荐视频ID集：{}", uuid, recVideoList.size(), jlvideos.size(), JSON.toJSONString(recVideoList));
        return recVideoList;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getVideoFromCache(VideoParamsDto inputParams,
                                          List<Integer> hotspotCatids, List<Integer> userCatids,
                                          List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                          List<IncentiveVideoHotspot> jlvideos,
                                          boolean isNewUUID, boolean isABTest){
        List<Object> videoInfoForRecList = null;
        boolean needFilter = false;
        int cacheExpireTime = isNewUUID? 30 : 60;
        // 获取缓存数据
        try (Jedis con = jedisPool.getResource()) {
            String key = String.format(RecommendConstant.key_user_rec_video_list, inputParams.getUuid());
            String cacheList = con.get(key);
            if(cacheList== null || cacheList.isEmpty()){
                videoInfoForRecList = getAsync(inputParams,hotspotCatids,userCatids,excludeCatid,excludeCollectid,jlvideos,isNewUUID,isABTest);
                String cacheValue = JSON.toJSONString(videoInfoForRecList);
                con.setex(key,cacheExpireTime,cacheValue);
            }else{
                videoInfoForRecList = (List<Object>)JSON.parse(cacheList);
                needFilter = true;
            }
            Map<String,BigDecimal> videoPlayRateMap = (Map<String,BigDecimal>)videoInfoForRecList.get(0);
            Map<String,Integer> videoCatMap = (Map<String,Integer>)videoInfoForRecList.get(1);
            if(needFilter){
                videoPlayRateMap = filterSortedMap(videoPlayRateMap, inputParams.getUuid());
                CompletableFuture<Integer> f4 = rule4Video(inputParams.getUuid(), hotspotCatids, excludeCatid, excludeCollectid, jlvideos, inputParams.isABTest());
                f4.join();
            }
            return Arrays.asList(videoPlayRateMap,videoCatMap);
        }
    }

    public Map<String,BigDecimal> filterSortedMap(Map<String,BigDecimal> sortedVideoMap, String uuid){
        List<String> vidList = Lists.newArrayList(sortedVideoMap.keySet());
        List<String> filteredVidList = bloomFilterService.containMuil(vidList.size(),uuid,vidList);
        Map<String,BigDecimal> filteredMap = new LinkedHashMap<>();
        filteredVidList.stream().forEach(vid ->filteredMap.put(vid,sortedVideoMap.get(vid)));
        return filteredMap;
    }

    public List<Object> getAsync(VideoParamsDto inputParams,
                                 List<Integer> hotspotCatids, List<Integer> userCatids,
                                 List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                 List<IncentiveVideoHotspot> jlvideos,
                                 boolean isNewUUID, boolean isABTest) {
        UserFeature userFeature = inputParams.getUserFeature();
        String uuid = userFeature.getPublicInfo().getUuid();
        List<Integer> jlHotspotCatids = Lists.newArrayList(hotspotCatids);
        List<VideoHotspotVo> cfRecomendVideos = Lists.newArrayList();
        Map<Integer, List<VideoHotspotVo>> catVideoMap = Maps.newHashMapWithExpectedSize(400);
        if(CollectionUtils.isNotEmpty(hotspotCatids)){
            int hotspotCatidSize = hotspotCatids.size();
            userCatids.addAll(hotspotCatids.subList(0, hotspotCatidSize> 3 ? 3 : hotspotCatidSize));
        }
        CompletableFuture<Void> fCatVideos = rule1Video(uuid, userCatids, excludeCollectid, catVideoMap, inputParams.isABTest());
        CompletableFuture fOfflineCf = rule2Video(uuid, excludeCatid, cfRecomendVideos, isABTest);
        CompletableFuture<Integer> f4 = rule4Video(uuid, jlHotspotCatids, excludeCatid, excludeCollectid, jlvideos, inputParams.isABTest());
        CompletableFuture<Void> f6 = CompletableFuture.allOf(fOfflineCf, f4, fCatVideos);
        long pt = System.currentTimeMillis();
        f6.join();

        List<String> videoList = new ArrayList<>();
        Map<String,Integer> videoCatMap = new LinkedHashMap<>();
        Map<String,VideoHotspotVo> voMap = new LinkedHashMap<>();
        for(VideoHotspotVo vo:cfRecomendVideos){
            videoList.add(vo.getVideo_id());
            videoCatMap.put(vo.getVideo_id(),vo.getCatid());
            voMap.put(vo.getVideo_id(),vo);
        }
        int catSum = 0;
        for(Integer catId: catVideoMap.keySet()){
            List<VideoHotspotVo> catVideoList =catVideoMap.get(catId);
            for(VideoHotspotVo vo:catVideoList){
                videoList.add(vo.getVideo_id());
                videoCatMap.put(vo.getVideo_id(),catId);
                voMap.put(vo.getVideo_id(),vo);
            }
            catSum+=catVideoList.size();
        }
        List<String> uniqVideoList = videoList.stream().distinct().collect(Collectors.toList());

        long pt2 = System.currentTimeMillis();
        log.warn("{} 推荐0.3时长，召回：{}，召回数量：{}，其中热门召回：{}，协同过滤召回：{}", uuid, (pt2- pt), uniqVideoList.size(),catSum,cfRecomendVideos.size());

        // 获取排序值并排序
        int needCount = 7;
        Map<String,BigDecimal>  sortedVideoMap = getVideoListPlayRate(uniqVideoList,userFeature,videoCatMap, needCount,inputParams.isABTest(), voMap);
        long pt3 = System.currentTimeMillis();
        log.warn("推荐0.3时长，排序总时长："+(pt3-pt2));

        return Arrays.asList(sortedVideoMap,videoCatMap);
    }

    public Map<String,BigDecimal> sortVideoMap(Map<String,BigDecimal> videoPlayRateMap,int needCount, int limitMulti){
        Map<String,BigDecimal> sortedMap = new LinkedHashMap<>();
        videoPlayRateMap.entrySet().stream()
                .sorted((p1,p2)-> p2.getValue().compareTo(p1.getValue()))
                .limit(needCount * limitMulti)
                .collect(Collectors.toList())
                .forEach(ele -> sortedMap.put(ele.getKey(), ele.getValue()));
        return sortedMap;
    }

    /**
     * 获取列表的预估播放率
     */
    public Map<String,BigDecimal> getVideoListPlayRate(List<String> videoList, UserFeature userFeature, Map<String,Integer> videoCatMap, int needCount, boolean isABTest, Map<String,VideoHotspotVo> voMap){
        // 根据排序值排序返回、相同类目间隔返回?
        long pt1 = System.currentTimeMillis();
        List<Map<String,Object>> listFeature = featureUtil.makeFeatureList(videoList,userFeature,videoCatMap,isABTest);
        long pt2 = System.currentTimeMillis();
        log.warn("推荐0.3时长，获取特征："+(pt2-pt1));
        Map<String,BigDecimal> videoPlayRateMap = predictService.predictPlayRate(listFeature, isABTest);
        long pt3 = System.currentTimeMillis();
        log.warn("推荐0.3时长，预估播放率："+(pt3-pt2));
        if(videoPlayRateMap == null && videoPlayRateMap.size() != listFeature.size()){
            return null;
        }

        if(isABTest){
//            caculateScore(videoList, videoPlayRateMap, voMap, userFeature);
        }

        return videoPlayRateMap;
    }

    private final Map<Integer,Double> videoTimeScoreMap = new LinkedHashMap<Integer,Double>(){
        {
            put(3,2D);
            put(4,1.5);
            put(2,1D);
            put(5,0.9);
            put(6,0.8);
        }
    };
    private void caculateScore(List<String> videoList, Map<String,BigDecimal> videoPlayRateMap, Map<String,VideoHotspotVo> voMap,UserFeature userFeature){
        Map<String,BigDecimal> scoreMap = new LinkedHashMap<>();
        // 获取视频每播放播放时长
        Map<String,Long> perPlayTimeMap =featureUtil.getVideoListPerPlayTime(videoList, userFeature);
        int i = 0;
        for(String vid:videoPlayRateMap.keySet()){
//            VideoHotspotVo vo = voMap.get(vid);
//            Integer second= vo.getVideo_time();
//            double scoreMulti = second>=90 && second<=480 ? 2 : 1;
            double scoreMulti = perPlayTimeMap.get(vid);
            BigDecimal score = videoPlayRateMap.get(vid).multiply(new BigDecimal(scoreMulti)).setScale(4, BigDecimal.ROUND_HALF_UP);
            videoPlayRateMap.put(vid, score);
            if(i++ < 1){
                log.warn("推荐0.3排序值，原值："+videoPlayRateMap.get(vid)+"，pp second："+scoreMulti+",score:"+score);
            }
        }
    }

    private List<String> videoTopK(Map<String,BigDecimal> videoPlayRateMap,Map<String,Integer> videoCatMap,int needCount, int oneCatLimit, int limitMulti){
        Map<String,BigDecimal> sortedMap = sortVideoMap(videoPlayRateMap,needCount,limitMulti);
        if(sortedMap.size() <= needCount){
            return new ArrayList<>(sortedMap.keySet());
        }

        // 间隔下发
        String printLine = "";
        List<String> recVideoList = new ArrayList<>();
        Map<Integer,Integer> catVideoCount = new LinkedHashMap<>();
        for(String vid:sortedMap.keySet()){
            int tmpCatId = videoCatMap.get(vid);
            int catCountInList = MapUtils.getIntValue(catVideoCount,tmpCatId,0);
            if(catCountInList >= oneCatLimit){
                continue;
            }
            recVideoList.add(vid);
            catVideoCount.put(tmpCatId, catCountInList+1);
            if(recVideoList.size() >= needCount){
                break;
            }
            printLine += ","+vid+":"+sortedMap.get(vid);
        }
        log.warn("排序后的视频列表："+printLine);
        if(recVideoList.size() < needCount){
            // 补充完整
            for(String vid:sortedMap.keySet()){
                if(recVideoList.contains(vid)){
                    continue;
                }
                recVideoList.add(vid);
                if(recVideoList.size() >= needCount){
                    break;
                }
            }
        }

        return recVideoList;
    }

    /**
     * 规则1获取推荐视频
     *
     * @param uuid
     * @param userCatids
     * @param excludeCollectid
     * @param searchVideoMap
     * @return
     */
    private CompletableFuture<Void> rule1Video(String uuid, List<Integer> userCatids, List<Integer> excludeCollectid, Map<Integer, List<VideoHotspotVo>> searchVideoMap, boolean isABTest) {
        //按分类计算每个分类要获取的热度视频数 key = catid, value = 数量
        Map<Integer, Long> catidDivide = new HashMap<>();
        long catFindCount = 90;
        for(Integer catId:userCatids){
            if(catFindCount <= 20) {
                catFindCount = 20;
            }
            catidDivide.put(catId,catFindCount);
            catFindCount -= 20;
        }
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            //lzhong 根据 catidDivide 获取第1规则的6个视频
            List<VideoHotspotVo> videoId1 = findRecommendVideosService.getVideoInfo(uuid, e[0], e[1].intValue(), null,excludeCollectid, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则1获取的视频：{}", uuid, isEmpty(videoId1) ? null : JSON.toJSONString(videoId1));
            }
            if (!isEmpty(videoId1)) {
                searchVideoMap.put(e[0], videoId1);
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则1时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catidDivide.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);
    }

    /**
     * 获取离线计算的推荐视频
     *
     * @return
     */
    private CompletableFuture<Integer> rule2Video(String uuid, List<Integer> catIds, List<VideoHotspotVo> searchPoolVideo, boolean isABTest) {
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第2规则的1个视频
            long pt = System.currentTimeMillis();
            List<VideoHotspotVo> videoId2 = findRecommendPoolVideosService.getVideoInfo(uuid, 100, catIds, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则2获取的视频：{}", uuid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2.subList(0,10)));
            }
            if (!isEmpty(videoId2)) {
                searchPoolVideo.addAll(videoId2);
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则2时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return searchPoolVideo.size();
        }, executor);
    }

    /**
     * 规则4推荐视频（激励视频）
     *
     * @param uuid
     * @param jlHotspotCatids
     * @param excludeCatid
     * @param excludeCollectid
     * @param jlvideos
     * @return
     */
    private CompletableFuture<Integer> rule4Video(String uuid, List<Integer> jlHotspotCatids, List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                                  List<IncentiveVideoHotspot> jlvideos, boolean isABTest) {
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
            long pt = System.currentTimeMillis();
            int jlCatid = function.apply(jlHotspotCatids);
            List<IncentiveVideoHotspot> jlvideo = findRecommendJLVideosService.getVideoId(uuid, jlCatid, 1, excludeCatid, excludeCollectid, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则4获取的激励视频：{}", uuid, isEmpty(jlvideo) ? null : JSON.toJSONString(jlvideo));
            }
            if (isEmpty(jlvideo)) {
                log.warn("推荐0.3规则4未找到 uuid={} 的推荐激励视频", uuid);
            } else {
                jlvideos.addAll(jlvideo);
                jlvideo = null;
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3规则4时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return jlvideos.size();
        }, executor);
    }

    /**
     * 替补视频
     *
     * @return
     */
    private int substituteVideo(String uuid, List<Integer> userCatids, List<Integer> hotspotCatids, List<Integer> excludeCollectid, Map<Integer, List<String>> searchVideoMap, List<String> searchVideo, boolean isABTest) {
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.3推荐标签各获取视频情况：{}", uuid, JSON.toJSONString(searchVideoMap));
        }
        long pt = System.currentTimeMillis();
        //补足方案，差多少个视频
        //为什么要做这么复杂的计算，不直接将视频放入searchVideo呢，因为有排序要求，需要按用户兴趣标签进行排序视频返回前端
        userCatids.forEach(d -> {
            if (searchVideoMap.containsKey(d)) {
                List<String> tmpL = searchVideoMap.get(d);
                searchVideo.addAll(tmpL);
                searchVideoMap.remove(d);
            }
        });
        //再补，因为searchVideoMap可能不是全部是用户的兴趣标签还有其它热门标签
        if (!searchVideoMap.isEmpty()) {
            searchVideoMap.values().forEach(d -> {
                searchVideo.addAll(d);
            });
            searchVideoMap.clear();
        }
        int diffCount = 7 - searchVideo.size();
        if (diffCount > 0) {
            log.warn("{} 推荐0.3不够返回视频数量，需要补足推荐视频，补充数量 {}", uuid, diffCount);
            List<String> suppVideos = findRecommendVideosService.getVideoId(uuid, diffCount, hotspotCatids, excludeCollectid, pt + 3000, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.3补足视频mongo查询耗时：{}", uuid, System.currentTimeMillis() - pt);
            }
            if (isEmpty(suppVideos) || suppVideos.size() < diffCount) {
                log.warn("推荐0.3设备标识(uuid={})没有补全视频", uuid);
            } else {
                searchVideo.addAll(suppVideos);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.3补足视频耗时：{}", uuid, System.currentTimeMillis() - pt);
        }
        return 0;
    }

    private void fillLoginInfo(VideoParamsDto params, List<Videos161Vo> firstVideos) {
        boolean isLogin = StringUtils.isNotBlank(params.getUserId()) && !"0".equals(params.getUserId());
        if (isLogin) {
            List<ClUserVideoInfoVo> list = clUserVideosService.findUserVideo(params.getUserId(), firstVideos.stream().map(Videos161Vo::getId).collect(Collectors.toList()));
            if (!isEmpty(list)) {
                firstVideos.forEach(d -> {
                    Optional<ClUserVideoInfoVo> op = list.stream().filter(e -> e.getVideoId().longValue() == d.getId().longValue()).findFirst();
                    if (op.isPresent()) {
                        ClUserVideoInfoVo tmp = op.get();
                        d.setCollection(tmp.getCollection());
                        d.setLove(tmp.getLove());
                    }
                });
            }
        }
    }

    //V2.5.0走新广告与视频组合
    private void packagNewAdvertAndVideos(FirstVideos161Vo firstVideosNewVo, List<Videos161Vo> firstVideos, Map<String, Object> adverMap, AbTestAdvParamsVo queueVo) {
        List<AdvertCodeVo> advertCodeVos = advertService.commonSearch(queueVo, adverMap);
        List<FirstVideosVo> firstVideosVos = VideoUtils.packagingNewAdvert(advertCodeVos, firstVideos);
        firstVideosNewVo.setFirstVideosVos(firstVideosVos);
    }

    /**
     * 填充合集视频子集合
     *
     * @param videos
     */
    private void fillGatherVideos(List<Videos161Vo> videos) {
        for (Videos161Vo vo : videos) {
            if (vo.getGatherId() != null && vo.getGatherId() > 0) {
                VideoGatherVo videoGatherVo = videoGatherService.getVideoGatherVo(vo);
                vo.setVideoGatherVo(videoGatherVo);
            }
        }
    }
}