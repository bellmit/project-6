package com.miguan.ballvideo.service.recommendlower;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.common.util.video.VideoUtils;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.mapper.VideosCatMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.service.recommend.FindRecommendCatidServiceImpl;
import com.miguan.ballvideo.service.recommend.FindRecommendEsServiceImpl;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import com.miguan.ballvideo.vo.AdvertVo;
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
 * 推荐视频
 */
@Service
@Slf4j
public class LowerRecommendVideosServiceImplAsyn {
    @Resource(name = "redisDB8Service")
    private RedisDB8Service redisDB8Service;
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
    private LowerFindRecommendVideosServiceImpl lowerFindRecommendVideosService;
    @Autowired
    private FindJLVideosServiceImpl findJLVideosService;
    @Autowired
    private FindRecommendCatidServiceImpl findRecommendCatidService;
    @Autowired
    private FindRecommendEsServiceImpl findRecommendEsService;
    @Resource
    private AdvertOldService advertOldService;
    @Autowired
    private ClUserVideosService clUserVideosService;
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
    ExecutorService executor = new ThreadPoolExecutor(16, 100, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    /**
     * 推荐
     * @param params
     * @return
     */
    public FirstVideos161Vo getRecommendVideos(VideoParamsDto params, AbTestAdvParamsVo queueVo) {

        long pt = System.currentTimeMillis();
        FirstVideos161Vo vo = getVideos(params, queueVo);
        //log.warn("推荐0.0总耗时：" + (System.currentTimeMillis() - pt));
        return vo;
    }

    private FirstVideos161Vo getVideos(VideoParamsDto params, AbTestAdvParamsVo queueVo) {
        String deviceId = params.getDeviceId();
        if (StringUtils.isBlank(deviceId)) {
            throw new NullPointerException("未获取到deviceid");
        }
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
        List<Integer> userCatids = findRecommendCatidService.getUserCatids(deviceId, params.getChannelId());
        //log.warn("推荐0.0当前uuid({})的兴趣标签: {}", deviceId, userCatids == null ? "" : JSON.toJSONString(userCatids));
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
        if(isEmpty(userCatids)){
            userCatids.add(firstCatid);
        }
        if(isEmpty(hotspotCatids)){
            hotspotCatids.add(firstCatid);
        }
        List<IncentiveVideoHotspot> jlvideos = new ArrayList<>();
        long pt = System.currentTimeMillis();
        //获取推荐视频
        int position = 0;
        int isShowIncentive = 0;
        String incentiveVideoRate = "0";
        Map<String, Object> advParam = new HashMap<>();
        advParam.put("queryType","position");
        advParam.put("positionType", "incentiveVideoPosition");
        advParam.put("mobileType", params.getMobileType());
        advParam.put("appPackage", params.getAppPackage());
        List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(queueVo, advParam, 3);
        if (CollectionUtils.isNotEmpty(list)) {
            isShowIncentive = list.get(0).getMaxShowNum() == null ? 1 : list.get(0).getMaxShowNum();
            position = list.get(0).getFirstLoadPosition() == null ? 1 : list.get(0).getFirstLoadPosition();
            incentiveVideoRate = list.get(0).getSecondLoadPosition() == null ? "0" : list.get(0).getSecondLoadPosition() + "";
        }
        //log.info("0.0当前uuid({})激励视频开关：{}", deviceId, isShowIncentive);
        List<String> searchVideo = getAsync(deviceId, hotspotCatids, userCatids, excludeCatid, excludeCollectid, jlvideos, isShowIncentive, position);
        //log.warn("推荐0.0耗时：" + (System.currentTimeMillis() - pt));
        //根据视频id查询es
        List<Videos161Vo> firstVideos = findRecommendEsService.list(searchVideo, incentiveVideoRate);
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
        if ((VersionUtil.isBetween(Constant.APPVERSION_253, Constant.APPVERSION_257, params.getAppVersion()) &&
                Constant.ANDROID.equals(params.getMobileType())) ||
                Constant.WeChat.equals(params.getMobileType())) {
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
        } else if("2".equals(params.getIsCombine())){
            //双列表需求，如果传值 IsCombine 为 2 ，不返回广告；默认为1
            List<FirstVideosVo> firstVideosVos = VideoUtils.getFirstVideosVos(firstVideos);
            firstVideosNewVo.setFirstVideosVos(firstVideosVos);
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

            boolean newFlag = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_249);
            if (newFlag) {
                //V2.5.0走新广告逻辑
                packagNewAdvertAndVideos(firstVideosNewVo, firstVideos, adverMap, queueVo);
            } else {
                List<AdvertVo> advertList = advertOldService.getAdvertsBySection(adverMap);
                VideoUtils.packagAdvertAndVideos(firstVideosNewVo, firstVideos, advertList, appVersion);
            }
        }
        //重新返回占比给前端
        firstVideosNewVo.setVideoDuty("4,3,1");
        //添加用户爆光视频至redis，用于记录用户爆光推荐记录
        explodeVideo(deviceId, firstVideos, jlvideos);
        return firstVideosNewVo;
    }

    /**
     * 爆光历史记录
     * @param firstVideos
     * @param jlvideos
     */
    public void explodeVideo(String deviceId, List<Videos161Vo> firstVideos, List<IncentiveVideoHotspot> jlvideos) {
        if (!isEmpty(firstVideos)) {
            List<String> exV = firstVideos.stream().map(e -> e.getId().toString()).collect(Collectors.toList());
            lowerFindRecommendVideosService.recordHistory(deviceId, exV);
        }
        if (!isEmpty(jlvideos)) {
            String jlBg = jlvideos.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.joining(","));
            String history = usershowHistoryVideos(deviceId, RedisKeyConstant.INCENTIVEVIDEO);
            if (StringUtils.isNotBlank(history)) {
                history = history.concat(",").concat(jlBg);
                history = Stream.of(history.split(",")).distinct().collect(Collectors.joining(","));
            } else {
                history = jlBg;
            }
            String key = RedisKeyConstant.SHOWEDIDS_KEY.concat(deviceId).concat(":").concat(RedisKeyConstant.INCENTIVEVIDEO);
            redisDB8Service.set(key, history, RedisKeyConstant.SHOWEDIDS_SECONDS);
        }
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


    public List<String> getAsync(String deviceId,
                                 List<Integer> hotspotCatids, List<Integer> userCatids,
                                 List<Integer> excludeCatid, List<Integer> excludeCollectid,
                                 List<IncentiveVideoHotspot> jlvideos,
                                 int isShowIncentive,
                                 int position) {
        //分配用户3个标签的随机数量； 取随机数1至7   1..4 取第1个分类，5..6 取第2个分类  7取第3个分类, 此操作随机5次
        //预发布视频(这期不做)；取随机数1至7   1..4 取第1个分类，5..6 取第2个分类  7取第3个分类
        List<Integer> jlHotspotCatids = Lists.newArrayList(hotspotCatids);
        List<String> searchVideo = Lists.newArrayList();
        //按分类计算每个分类要获取的热度视频数 key = catid, value = 数量
        Map<Integer, Long> catidDivide = null;
        if (isShowIncentive == 0) {
            catidDivide = IntStream.range(1, 8).mapToObj(i -> function1.apply(userCatids))
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        } else {
            catidDivide = IntStream.range(1, 7).mapToObj(i -> function.apply(userCatids))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
        //log.warn("推荐0.0此次目标分类个数：{}, 推荐标签情况：{}", catidDivide.size(), JSON.toJSON(catidDivide));
        Map<Integer, List<String>> searchVideoMap = Maps.newHashMapWithExpectedSize(8);
        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            //lzhong 根据 catidDivide 获取第1规则的6个视频
            List<String> videoId1 = lowerFindRecommendVideosService.getVideoId(deviceId, e[0], e[1].intValue(), excludeCollectid);
            if (log.isDebugEnabled()) {
                log.debug("第1规则获取的视频：{}", isEmpty(videoId1) ? null : JSON.toJSONString(videoId1));
            }
            if (!isEmpty(videoId1)) {
                searchVideoMap.put(e[0], videoId1);
            }
            if (log.isDebugEnabled()) {
                log.debug("规则1时长：{}", (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catidDivide.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);

        CompletableFuture<Void> f1 = CompletableFuture.allOf(listFeture);
        CompletableFuture<Number> f2 = CompletableFuture.supplyAsync(() -> {
            //lzhong 根据热度标签获取第3规则的1个视频
            long pt = System.currentTimeMillis();
            Integer hotsportCatid = function.apply(hotspotCatids);
            //这里要去除这个标签，因为后面补足视频的时候需要用到hotspotCatids变量
            hotspotCatids.remove(hotsportCatid);
            List<String> videoId2 = lowerFindRecommendVideosService.getVideoId(deviceId, hotsportCatid, 1, excludeCollectid);
            if (log.isDebugEnabled()) {
                log.debug("第2规则获取的视频：{}", isEmpty(videoId2) ? null : JSON.toJSONString(videoId2));
            }
            if (!isEmpty(videoId2)) {
                searchVideoMap.put(hotsportCatid, videoId2);
            }
            if (log.isDebugEnabled()) {
                log.debug("规则2时长：{}", (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        }, executor);
        CompletableFuture<Integer> f3 = null;
        if(isShowIncentive > 0){
            f3 = CompletableFuture.supplyAsync(() -> {
                //lzhong 根据热度标签获取第4规则的1个视频(激励视频)
                long pt = System.currentTimeMillis();
                int jlCatid = function.apply(jlHotspotCatids);
                List<IncentiveVideoHotspot> jlvideo = findJLVideosService.getVideoId(deviceId, usershowHistoryVideos(deviceId, RedisKeyConstant.INCENTIVEVIDEO), jlCatid, 1, excludeCatid, excludeCollectid);
                if (log.isDebugEnabled()) {
                    log.debug("推荐0.0第4规则获取的激励视频：{}", isEmpty(jlvideo) ? null : JSON.toJSONString(jlvideo));
                }
                if (isEmpty(jlvideo)) {
                    log.warn("推荐0.0未找到 distinctId={} 的推荐激励视频", deviceId);
                } else {
                    jlvideos.addAll(jlvideo);
                    jlvideo = null;
                }
                /*if (log.isDebugEnabled()) {
                    log.debug("推荐0.0规则4时长：{}", (System.currentTimeMillis() - pt));
                }*/
                return jlvideos.size();
            }, executor);
        }

        CompletableFuture<Integer> f4 = f1.thenCombineAsync(f2, (d1, d2) -> {
            if (log.isDebugEnabled()) {
                log.debug("推荐0.0推荐标签各获取视频情况：{}", JSON.toJSONString(searchVideoMap));
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
            if(isShowIncentive == 0){
                diffCount = 8 - searchVideo.size();
            }
            if (diffCount > 0) {
                log.warn("推荐0.0不够返回视频数量，需要补足推荐视频，补充数量 {}", diffCount);
                List<String> suppVideos = lowerFindRecommendVideosService.getVideoId(deviceId, diffCount, hotspotCatids, excludeCollectid, pt + 3000);
                if (log.isDebugEnabled()) {
                    log.debug("推荐0.0补足视频mongo查询耗时：{}", System.currentTimeMillis() - pt);
                }
                if (isEmpty(suppVideos) || suppVideos.size() < diffCount) {
                    log.warn("推荐0.0设备标识(uuid={})没有补全视频", deviceId);
                } else {
                    searchVideo.addAll(suppVideos);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("推荐0.0补足视频耗时：{}", System.currentTimeMillis() - pt);
            }
            return 0;
        }, executor);
        CompletableFuture<Void> f5 = null;
        if(isShowIncentive == 0){
            f5 = CompletableFuture.allOf(f4);
        } else {
            f5 = CompletableFuture.allOf(f3, f4);
        }

        long pt = System.currentTimeMillis();
        f5.join();
        /*if (log.isDebugEnabled()) {
            log.debug("推荐0.0规则all时长：{}", (System.currentTimeMillis() - pt));
        }*/

        position = position > searchVideo.size() ? searchVideo.size() : position <= 0 ? 0 : (position - 1);
        searchVideo.addAll(position, jlvideos.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        log.warn("推荐0.0此次给uuid({})推荐了 {} 个视频，其中激励视频 {} 个, 推荐视频ID集：{}", deviceId, searchVideo.size(), jlvideos.size(), JSON.toJSON(searchVideo));
        return searchVideo;
    }

    private String usershowHistoryVideos(String deviceId, String catId) {
        String key = RedisKeyConstant.SHOWEDIDS_KEY.concat(deviceId).concat(":").concat(catId);
        String showedIds = redisDB8Service.get(key);
        String[] history = null;
        if (showedIds != null) {
            history = showedIds.split(",");
        }
        //之前有针对普通视频，按之前的规定是只能最多存储100个，现在这个方法只针对激励视频
        //按最新的要求是不要限制存储爆光个数，所以这里不想修改代码，所以给其1000个值(目前线上激励视频不会有这么多)
        if (history != null && history.length > 1000) {
            redisDB8Service.del(key);
            history = null;
        }
        return history == null ? null : showedIds;
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