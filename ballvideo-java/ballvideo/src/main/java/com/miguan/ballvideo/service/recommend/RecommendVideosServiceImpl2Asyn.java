package com.miguan.ballvideo.service.recommend;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.PackageUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import com.miguan.ballvideo.vo.video.FirstVideosVo;
import com.miguan.ballvideo.vo.video.VideoGatherVo;
import com.miguan.ballvideo.vo.video.Videos161Vo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 推荐0.2将用户兴趣标签的2个视频替换成使用spark计算出来的视频
 * 推荐视频
 */
@Service
@Slf4j
public class RecommendVideosServiceImpl2Asyn {

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

    Function<List<Integer>, Integer> function1 = catids -> {
        int rn = RandomUtils.nextInt(1, 8);
        if (rn < 4) {
            return catids.get(0);
        } else if (rn < 6) {
            return catids.size() < 2 ? catids.get(catids.size() - 1) : catids.get(1);
        }
        return catids.size() < 3 ? catids.get(catids.size() - 1) : catids.get(2);
    };
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
        //log.warn("{} 推荐0.2总耗时：{}", uuid, (System.currentTimeMillis() - pt));
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
        long pt = System.currentTimeMillis();
        //激励视频配置参数
        Map<String, Object> incentiveMap = AdvUtils.getIncentiveInfo(params.getMobileType(), params.getAppPackage(), queueVo,1);
        Integer isShowIncentive = incentiveMap.get("isShowIncentive")==null? 0 : Integer.valueOf(incentiveMap.get("isShowIncentive").toString());
        Integer position = incentiveMap.get("position")==null? 0 : Integer.valueOf(incentiveMap.get("position").toString());
        String incentiveVideoRate = incentiveMap.get("incentiveVideoRate")==null? "0" : incentiveMap.get("incentiveVideoRate").toString();
        //获取推荐视频
        boolean isNewUUID = redisService.exits(RedisKeyConstant.UUID_KEY + uuid);
        List<String> searchVideo = getAsync(uuid, hotspotCatids, userCatids, excludeCatid, excludeCollectid, jlvideos, isShowIncentive, isNewUUID, params.isABTest());
        log.warn("{} 推荐0.2耗时：{}", uuid, (System.currentTimeMillis() - pt));
        //根据视频id查询es
        List<Videos161Vo> rvideos = findRecommendEsService.list(searchVideo, incentiveVideoRate);
        List<Videos161Vo> firstVideos = new ArrayList<>();
        if (isNotEmpty(rvideos)) {
            // 获取所有非激励视频
            List<Videos161Vo> notjl = rvideos.stream().filter(e -> e.getIncentiveVideo() == null || e.getIncentiveVideo().intValue() != 1).collect(Collectors.toList());
            firstVideos.addAll(notjl);

            List<Videos161Vo> jl = rvideos.stream().filter(e -> e.getIncentiveVideo() != null && e.getIncentiveVideo().intValue() == 1).collect(Collectors.toList());
            // 新用户，激励视频放到最后
            if (isNewUUID) {
                firstVideos.addAll(jl);
            } else {
                //获取所有激励视频,并将其放到后台配置得相应位置
                position = position > searchVideo.size() ? searchVideo.size() : position <= 0 ? 0 : (position - 1);
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
        } else if("2".equals(params.getIsCombine())) {
            //双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
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


    public List<String> getAsync(String uuid,
                                 List<Integer> hotspotCatids, List<Integer> userCatids,
                                 List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                 List<IncentiveVideoHotspot> jlvideos,
                                 int isShwoincentive,
                                 boolean isNewUUID, boolean isABTest) {
        //分配用户3个标签的随机数量； 取随机数1至7   1..4 取第1个分类，5..6 取第2个分类  7取第3个分类, 此操作随机5次
        //预发布视频(这期不做)；取随机数1至7   1..4 取第1个分类，5..6 取第2个分类  7取第3个分类
        List<Integer> jlHotspotCatids = Lists.newArrayList(hotspotCatids);
        List<String> searchVideo = Lists.newArrayList();
        List<String> searchPoolVideo = Lists.newArrayList();
        Map<Integer, List<String>> searchVideoMap = Maps.newHashMapWithExpectedSize(8);
        CompletableFuture<Void> f1 = rule1Video(uuid, userCatids, excludeCollectid, searchVideoMap, isShwoincentive, isABTest);
        CompletableFuture f2 = rule2Video(uuid, excludeCatid, searchPoolVideo, isABTest);
        CompletableFuture<Integer> f3 = rule3Video(uuid, hotspotCatids, excludeCollectid, searchVideoMap, isABTest);
        CompletableFuture<Integer> f4 = null;
        if(isShwoincentive > 0){
            f4 = rule4Video(uuid, jlHotspotCatids, excludeCatid, excludeCollectid, jlvideos, isABTest);
        }
        CompletableFuture<Integer> f5 = f1.thenCombineAsync(f3,
                (d1, d2) -> this.substituteVideo(uuid, userCatids, hotspotCatids, excludeCollectid, searchVideoMap, searchVideo, isShwoincentive, isABTest), executor);
        CompletableFuture<Void> f6 = null;
        if(isShwoincentive == 0){
            f6 =CompletableFuture.allOf(f2, f5);
        } else {
            f6 =CompletableFuture.allOf(f2, f4, f5);
        }
        long pt = System.currentTimeMillis();
        f6.join();
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.2规则all时长：{}", uuid, (System.currentTimeMillis() - pt));
        }
        log.warn("推荐0.2此次给uuid({})推荐了 {} 个视频，其中激励视频 {} 个, 推荐视频ID集：{}", uuid, searchVideo.size(), jlvideos.size(), JSON.toJSON(searchVideo));

        if (CollectionUtils.isNotEmpty(searchVideo) && CollectionUtils.isNotEmpty(searchPoolVideo)) {
            // 新用户不进行替换，老用户进行替换
            log.warn("推荐0.2此次给uuid({})【离线】推荐了 {} 个视频，推荐视频ID集：{}", uuid, searchPoolVideo.size(), JSON.toJSONString(searchPoolVideo));
            // 将离线视频放到前面，并移除与离线视频数量相同的，排在前面的原有视频
            searchVideo.removeAll(searchPoolVideo);
            searchVideo.addAll(0, searchPoolVideo);
            int videoSize = isShwoincentive == 0? 8 : 7;
            while (searchVideo.size() > videoSize) {
                searchVideo.remove(searchVideo.size() - 2);
            }
        }
        searchVideo.addAll(jlvideos.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        log.warn("推荐0.2此次给uuid({})推荐了 {} 个视频，其中激励视频 {} 个, 推荐视频ID集：{}", uuid, searchVideo.size(), jlvideos.size(), JSON.toJSONString(searchVideo));
        return searchVideo;
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
    private CompletableFuture<Void> rule1Video(String uuid, List<Integer> userCatids, List<Integer> excludeCollectid, Map<Integer, List<String>> searchVideoMap, int isShwoincentive, boolean isABTest) {
        //按分类计算每个分类要获取的热度视频数 key = catid, value = 数量
        Map<Integer, Long> catidDivide = null;
        if(isShwoincentive == 0){
            catidDivide = IntStream.range(1, 8).mapToObj(i -> function1.apply(userCatids))
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        } else {
            catidDivide = IntStream.range(1, 7).mapToObj(i -> function.apply(userCatids))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
        log.warn("推荐0.2此次目标分类个数：{}, 推荐标签情况：{}", catidDivide.size(), JSON.toJSON(catidDivide));
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            //lzhong 根据 catidDivide 获取第1规则的6个视频
            List<String> videoId1 = findRecommendVideosService.getVideoId(uuid, e[0], e[1].intValue(), excludeCollectid, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则1获取的视频：{}", uuid, isEmpty(videoId1) ? null : JSON.toJSONString(videoId1));
            }
            if (!isEmpty(videoId1)) {
                searchVideoMap.put(e[0], videoId1);
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则1时长：{}", uuid, (System.currentTimeMillis() - pt));
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
    private CompletableFuture<Integer> rule2Video(String uuid, List<Integer> catIds, List<String> searchPoolVideo, boolean isABTest) {
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第2规则的1个视频
            long pt = System.currentTimeMillis();
            List<String> videoId2 = findRecommendPoolVideosService.getVideoId(uuid, 5, catIds, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则2获取的视频：{}", uuid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                searchPoolVideo.addAll(videoId2);
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则2时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return searchPoolVideo.size();
        }, executor);
    }

    /**
     * 规则3获取推荐视频
     *
     * @param uuid
     * @param hotspotCatids
     * @param excludeCollectid
     * @param searchVideoMap
     * @return
     */
    private CompletableFuture<Integer> rule3Video(String uuid, List<Integer> hotspotCatids, List<Integer> excludeCollectid, Map<Integer, List<String>> searchVideoMap, boolean isABTest) {
        return CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第3规则的1个视频
            long pt = System.currentTimeMillis();
            Integer hotsportCatid = function.apply(hotspotCatids);
            //这里要去除这个标签，因为后面补足视频的时候需要用到hotspotCatids变量
            hotspotCatids.remove(hotsportCatid);
            List<String> videoId2 = findRecommendVideosService.getVideoId(uuid, hotsportCatid, 1, excludeCollectid, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则3获取的视频：{}", uuid, isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                searchVideoMap.put(hotsportCatid, videoId2);
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则3时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return videoId2 == null ? 0 : videoId2.size();
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
                log.debug("{} 推荐0.2规则4获取的激励视频：{}", uuid, isEmpty(jlvideo) ? null : JSON.toJSONString(jlvideo));
            }
            if (isEmpty(jlvideo)) {
                log.warn("推荐0.2规则4未找到 uuid={} 的推荐激励视频", uuid);
            } else {
                jlvideos.addAll(jlvideo);
                jlvideo = null;
            }
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2规则4时长：{}", uuid, (System.currentTimeMillis() - pt));
            }
            return jlvideos.size();
        }, executor);
    }

    /**
     * 替补视频
     *
     * @return
     */
    private int substituteVideo(String uuid, List<Integer> userCatids, List<Integer> hotspotCatids, List<Integer> excludeCollectid, Map<Integer, List<String>> searchVideoMap, List<String> searchVideo, int isShwoincentive, boolean isABTest) {
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.2推荐标签各获取视频情况：{}", uuid, JSON.toJSONString(searchVideoMap));
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
        if(isShwoincentive == 0){
            diffCount = 8 - searchVideo.size();
        }
        if (diffCount > 0) {
            log.warn("{} 推荐0.2不够返回视频数量，需要补足推荐视频，补充数量 {}", uuid, diffCount);
            List<String> suppVideos = findRecommendVideosService.getVideoId(uuid, diffCount, hotspotCatids, excludeCollectid, pt + 3000, isABTest);
            if (log.isDebugEnabled()) {
                log.debug("{} 推荐0.2补足视频mongo查询耗时：{}", uuid, System.currentTimeMillis() - pt);
            }
            if (isEmpty(suppVideos) || suppVideos.size() < diffCount) {
                log.warn("推荐0.2设备标识(uuid={})没有补全视频", uuid);
            } else {
                searchVideo.addAll(suppVideos);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("{} 推荐0.2补足视频耗时：{}", uuid, System.currentTimeMillis() - pt);
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