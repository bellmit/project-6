package com.miguan.recommend.service.recommend.normativeimpl;

import com.alibaba.fastjson.JSON;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.bo.NormativeVideoRecommendDto;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.common.constants.RedisRecommendConstants;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.NormativeVideoRecommendService;
import com.miguan.recommend.service.recommend.PredictService;
import com.miguan.recommend.service.recommend.VideoHotService;
import com.miguan.recommend.vo.NormativeVideoRecommendVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "normativeVideoRecommendServiceV1")
public class NormativeVideoRecommendServiceV1Impl extends NormativeRecommendService implements NormativeVideoRecommendService<NormativeVideoRecommendDto> {

    private static ExecutorService executor = new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));
    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "videoHotServiceV3")
    private VideoHotService videoHotServiceV3;
    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;
    @Autowired
    private PredictService predictService;

    @Override
    public NormativeVideoRecommendVo recommend(PredictDto predictDto, NormativeVideoRecommendDto recommendDto) {
        // 初始化推荐分类
        this.initRecommendCat(predictDto, recommendDto);

        //视频信息和预估播放率
        List<Object> videoInfoForRecList = null;
        // 获取视频预估播放率缓存
        int predictExpireTime = Boolean.parseBoolean(recommendDto.getIs_first()) ? 30 : 60;
        String predictKey = RedisRecommendConstants.key_user_rec_video_list + predictDto.getDevice_id();
        String predictValue = redisDB0Service.get(predictKey);
        boolean isRedis = false;
        if (StringUtils.isEmpty(predictValue)) {
            // 异步获取视频
            videoInfoForRecList  = this.getVideoAsync(predictDto, recommendDto);
            if(!CollectionUtils.isEmpty(videoInfoForRecList)){
                predictValue = JSON.toJSONString(videoInfoForRecList);
                redisDB0Service.set(predictKey, predictValue, predictExpireTime);
            }
        } else {
            isRedis = true;
            // 解析缓存
            videoInfoForRecList = (List<Object>) JSON.parse(predictValue);
            // 获取激励视频
            CompletableFuture<Integer> incentiveFuture = this.getIncentiveVideo(predictDto, recommendDto);
            incentiveFuture.join();
        }

        Map<String, BigDecimal> videoPlayRateMap = (Map<String, BigDecimal>) videoInfoForRecList.get(0);
        Map<String, Integer> videoCatMap = (Map<String, Integer>) videoInfoForRecList.get(1);
        if(isRedis){
            videoPlayRateMap = super.bloomSortedMap(videoPlayRateMap, predictDto.getDevice_id());
        }

        List<String> hotspotVideoList = new ArrayList<>();
        List<String> incentiveVideo = recommendDto.getIncetiveVideoList().stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(videoPlayRateMap)) {
            hotspotVideoList = predictService.videoTopK(videoPlayRateMap, videoCatMap, recommendDto.getHotspotNum(), 2, 20);
        }
        // 将推荐的视频ID缓存：1.避免重复推荐 2.曝光埋点需要客户端上报，才能将视频添加到bloom过滤器中
        super.cacheVideo(predictDto.getDevice_id(), hotspotVideoList, incentiveVideo);
        return new NormativeVideoRecommendVo(incentiveVideo, hotspotVideoList);
    }

    private List<Object> getVideoAsync(PredictDto predictDto, NormativeVideoRecommendDto recommendDto) {

        CompletableFuture<Void> hotspotVideoFuture = this.getHotspotVideo(predictDto, recommendDto);
        CompletableFuture<Integer> jlVideoFuture = null;
        if (recommendDto.getIncentiveNum() > 0) {
            jlVideoFuture = this.getIncentiveVideo(predictDto, recommendDto);
        }

        CompletableFuture<Void> allFuture = null;
        if (recommendDto.getIncentiveNum() == null || recommendDto.getIncentiveNum() == 0) {
            allFuture = CompletableFuture.allOf(hotspotVideoFuture);
        } else {
            allFuture = CompletableFuture.allOf(hotspotVideoFuture, jlVideoFuture);
        }
        long pt = System.currentTimeMillis();
        allFuture.join();
        long pt2 = System.currentTimeMillis();

        List<VideoHotspotVo> allVideos = new ArrayList<VideoHotspotVo>();
        Map<Integer, List<VideoHotspotVo>> hotspotVideoMap = recommendDto.getHotspotVideoMap();
        int catNum = 0;
        for (Integer catid : hotspotVideoMap.keySet()) {
            List<VideoHotspotVo> catVideos = hotspotVideoMap.get(catid);
            if(isEmpty(catVideos)){
                continue;
            }
            catNum++;
            allVideos.removeAll(catVideos);
            allVideos.addAll(catVideos);
        }
        log.info("{} 通用推荐召回时长：{}，召回数量：{}，其中热门召回：{}，协同过滤召回：{}",
                predictDto.getDevice_id(), (pt2 - pt), allVideos.size(), catNum, 0);

        // 预估视频的播放率
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(predictDto, allVideos);
        // 视频对应分类Map
        Map<String, Integer> videoCatMap = allVideos.stream().collect(Collectors.toMap(VideoHotspotVo::getVideo_id, VideoHotspotVo::getCatid, (v1, v2) -> v1));
        return Arrays.asList(sortedVideoMap, videoCatMap);
    }

    public CompletableFuture<Integer> getIncentiveVideo(PredictDto predictDto, NormativeVideoRecommendDto recommendDto) {
        return CompletableFuture.supplyAsync(() -> {
            long pt = System.currentTimeMillis();
            List<IncentiveVideoHotspot> findList = incentiveVideoHotServiceV3New.findAndFilter(predictDto.getDevice_id(), 0, recommendDto.getIncentiveNum());
            if (isEmpty(findList)) {
                log.info("通用推荐 未找到 uuid={} 的激励视频", predictDto.getDevice_id());
                return 0;
            } else {
                log.debug("{} 通用推荐 获取的激励视频：{} 个", predictDto.getDevice_id(), isEmpty(findList) ? 0 : findList.size());
                recommendDto.getIncetiveVideoList().addAll(findList);
            }
            log.info("{} 通用推荐 获取的激励视频时长：{}", predictDto.getDevice_id(), (System.currentTimeMillis() - pt));
            return findList.size();
        }, executor);
    }

    private CompletableFuture<Void> getHotspotVideo(PredictDto predictDto, NormativeVideoRecommendDto recommendDto) {
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        long initNum = 110;
        for (Integer catid : recommendDto.getRecommendCat()) {
            catNum.put(catid, initNum < 20 ? 20 : initNum);
            initNum -= 20;
        }

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(predictDto.getDevice_id(), e[0], e[1]);
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
            if (isEmpty(videoId1)) {
                log.debug("{} 通用推荐 获取用户兴趣分类[{}]视频：0 个", predictDto.getDevice_id(), e[0]);
            } else {
                log.debug("{} 通用推荐 获取用户兴趣分类[{}]视频：{} 个", predictDto.getDevice_id(), e[0], isEmpty(videoId1) ? 0 : videoId1.size());
                recommendDto.getHotspotVideoMap().put(e[0], videoId1);
            }
            log.info("{} 通用推荐 获取用户兴趣分类[{}]视频时长：{}", predictDto.getDevice_id(), e[0], (System.currentTimeMillis() - pt));
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        return CompletableFuture.allOf(listFeture);
    }

    private void initRecommendCat(PredictDto predictDto, NormativeVideoRecommendDto recommendDto) {
        recommendDto.setDefaultCat(super.getDefaultCat());
        recommendDto.setUserCat(super.getUserCat(predictDto.getDevice_id()));

        List<Integer> recommendCat = new ArrayList<Integer>();
        if(CollectionUtils.isEmpty(recommendDto.getUserCat())){
            recommendCat.addAll(recommendDto.getDefaultCat().subList(0, Math.min(3, recommendDto.getDefaultCat().size())));
        } else {
            recommendCat.addAll(recommendDto.getUserCat().subList(0, Math.min(3, recommendDto.getUserCat().size())));
        }


        recommendDto.setSimilarCat(super.getSimilarCat(recommendCat.get(0)));
        if(!CollectionUtils.isEmpty(recommendDto.getSimilarCat())){
            int needCatNum = 6 - recommendCat.size();
            recommendCat.addAll(recommendDto.getSimilarCat().subList(0, Math.min(needCatNum, recommendDto.getSimilarCat().size())));
        }

        // 确定最终推荐召回的分类
        recommendDto.setRecommendCat(recommendCat);
    }
}
