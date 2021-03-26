package com.miguan.recommend.service.recommend.impl.v8;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.CatWeightDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.*;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.RedisService;
import com.miguan.recommend.service.mongo.UserRawTagsService;
import com.miguan.recommend.service.recommend.*;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "oldUserRecommendService")
public class OldUserRecommendServiceImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "redisDB0Service")
    private RedisService redisDB0Service;
    @Resource(name = "redisDB11Service")
    private RedisService redisDB11Service;
    @Resource(name = "videoHotServiceV3")
    private VideoHotService videoHotServiceV3;
    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;
    @Resource
    private VideosCatService videosCatService;
    @Resource
    private UserRawTagsService userRawTagsService;
    @Autowired
    private PredictService predictService;
    @Resource
    private FeatureService featureService;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        // 初始推荐参数
        this.initRecommendParam(baseDto, recommendDto);
        // 是否需要曝光过滤
        boolean needFilter = false;
        // 判断是否是新用户
        int cacheExpireTime = 30;
        List<Object> videoInfoForRecList = null;
        String key = String.format(RedisRecommendConstants.key_user_rec_video_list_old, baseDto.getUuid());
        String cacheList = redisDB0Service.get(key);
        if (cacheList == null || cacheList.isEmpty()) {
            videoInfoForRecList = getAsync(baseDto, recommendDto);
            redisDB0Service.set(key, JSON.toJSONString(videoInfoForRecList), cacheExpireTime);
        } else {
            videoInfoForRecList = (List<Object>) JSON.parse(cacheList);
            needFilter = true;
        }

        Map<String, BigDecimal> videoPlayRateMap = (Map<String, BigDecimal>) videoInfoForRecList.get(0);
        Map<String, Integer> videoCatMap = (Map<String, Integer>) videoInfoForRecList.get(1);
        if (needFilter) {
            List<IncentiveVideoHotspot> jlVideo = new ArrayList<>();
            videoPlayRateMap = filterSortedMap(videoPlayRateMap, baseDto.getUuid(), bloomFilterService);
            CompletableFuture<Integer> incetiveHotVideo = getIncetiveHotVideo(baseDto, recommendDto, jlVideo, incentiveVideoHotServiceV3New, executor);
            incetiveHotVideo.join();
            recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
            recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        }

        if (videoInfoForRecList == null || videoInfoForRecList.isEmpty()) {
            return;
        }

        int hotVideoReturnCount = recommendDto.getVideoNum();
        if (isEmpty(recommendDto.getJlvideo())) {
            ++hotVideoReturnCount;
        }

        // 分类对应的视频列表Map
        Map<Integer, List<String>> catVideoListMap = predictService.videoTopK(videoPlayRateMap, videoCatMap);
        // 最终推荐的视频集合
        List<String> recVideoList = new ArrayList<>();
        recommendDto.getRecommendCatNumMap().forEach((key1, value) -> {
            List<String> catVideoList = catVideoListMap.get(key1);
            if (!isEmpty(catVideoList)) {
                recVideoList.addAll(catVideoList.subList(0, Math.min(value, catVideoList.size())));
            }
        });

        // 如果最终推荐的视频的个数，不满足需要返回的个数，补足
        if(recVideoList.size() < hotVideoReturnCount){
            // 按照排序取出剩余需要返回的视频
            List<String> videoSortList = predictService.videoTopK(videoPlayRateMap, videoCatMap, hotVideoReturnCount, 3, 2);
            videoSortList.removeAll(recVideoList);
            if(!isEmpty(videoSortList)){
                recVideoList.addAll(videoSortList.subList(0, Math.min(hotVideoReturnCount - recVideoList.size(), videoSortList.size())));
            }
        }


        recommendDto.setRecommendVideo(recVideoList);
        recommendDto.setRecommendVideoCat(videoCatMap);
        log.warn("老用户推荐优化 此次给uuid({})推荐了 {} 个视频，推荐视频ID集：{}, 激励视频 {} 个, 激励视频ID：{}",
                baseDto.getUuid(), recVideoList.size(), JSON.toJSONString(recVideoList), recommendDto.getJlvideo().size(), JSON.toJSONString(recommendDto.getJlvideo()));

        // 将查询的视频放入Bloom过滤器
        bloomVideo(baseDto.getUuid(), recVideoList, recommendDto.getJlvideo(), bloomFilterService, executor);
        featureService.saveFeatureToRedis(baseDto, recommendDto);
    }

    public List<Object> getAsync(BaseDto baseDto, VideoRecommendDto recommendDto) {
        Map<Integer, List<VideoHotspotVo>> userCatVideoMap = new LinkedHashMap<>();

        Map<Integer, Integer> catNumMap = new HashMap<Integer, Integer>();
        for(Integer catid : recommendDto.getRecommendCatNumMap().keySet()){
            catNumMap.put(catid, 50);
        }
        // 获取用户的兴趣分类视频
        CompletableFuture<Void> userCatVideoFuture = super.getHotVideo(baseDto, recommendDto, catNumMap, userCatVideoMap, videoHotServiceV3);
        // 获取激励视频
        List<IncentiveVideoHotspot> jlVideo = Lists.newArrayList();
        CompletableFuture<Integer> jlVideoFuture = null;
        if (recommendDto.getIncentiveVideoNum() > 0) {
            jlVideoFuture = super.getIncetiveHotVideo(baseDto, recommendDto, jlVideo, incentiveVideoHotServiceV3New, executor);
        }

        CompletableFuture<Void> allFuture = null;
        if (recommendDto.getIncentiveVideoNum() == 0) {
            allFuture = CompletableFuture.allOf(userCatVideoFuture);
        } else {
            allFuture = CompletableFuture.allOf(userCatVideoFuture, jlVideoFuture);
        }
        long pt = System.currentTimeMillis();
        allFuture.join();
        catNumMap.clear();

        // 所有视频
        List<VideoHotspotVo> allVideos = new ArrayList<VideoHotspotVo>();
        userCatVideoMap.forEach((key, value) -> {
            allVideos.removeAll(value);
            allVideos.addAll(value);
        });
        Map<String, Integer> videoCatMap = allVideos.stream().collect(Collectors.toMap(VideoHotspotVo::getVideo_id, VideoHotspotVo::getCatid, (v1, v2) -> v1));

        // 去重后得所有视频ID
        long pt2 = System.currentTimeMillis();
        log.warn("{} 老用户推荐优化 时长，召回：{}，召回数量：{}", baseDto.getUuid(), (pt2 - pt), allVideos.size());

        // 获取排序值并排序
        Map<String, BigDecimal> sortedVideoMap = predictService.getVideoListPlayRate(baseDto, allVideos);
        long pt3 = System.currentTimeMillis();

        recommendDto.getJlvideo().addAll(jlVideo.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList()));
        recommendDto.setJlvideoCat(jlVideo.stream().collect(Collectors.toMap(IncentiveVideoHotspot::getVideo_id, IncentiveVideoHotspot::getCatid)));
        log.info("{} 老用户推荐优化 时长，排序总时长：{}", baseDto.getUuid(), (pt3 - pt2));
        return Arrays.asList(sortedVideoMap, videoCatMap);
    }

    public void initRecommendParam(BaseDto baseDto, VideoRecommendDto recommendDto) {
        // 获取用户离线分类权重
        List<CatWeightDto> catWeightDtoList = this.getUserCatWeights(baseDto);
        // 扩展分类
        List<Integer> extensionCat = this.getExtensionCat(catWeightDtoList, recommendDto.getExcludeCatList());
        // 根据权重召回的分类集合
        List<Integer> randomCatList = new ArrayList<Integer>();
        int userCatNum = 0;
        // 如果用户有离线的分类权重数据， 根据权重，添加到召回的集合里
        if (!isEmpty(catWeightDtoList)) {
            log.debug("{} 老用户推荐优化，离线分类权重：{}", baseDto.getUuid(), JSONObject.toJSONString(catWeightDtoList));
            userCatNum = catWeightDtoList.size();
            catWeightDtoList.forEach(c -> {
                int weight = new BigDecimal(c.getWeight()).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                for (int i = 0; i < weight; i++) {
                    randomCatList.add(c.getCatId());
                }
            });
            catWeightDtoList.clear();
        }

        Random random = new Random();
        // 如果用户召回的分类大于2个，增加10%权重的扩展分类
        // 如果用户召回的分类小于3个，从扩展分类张随机补足3个
        if (userCatNum > 2) {
            int extensionNum = new BigDecimal(randomCatList.size())
                    .divide(new BigDecimal(0.9), 0, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(0.1))
                    .setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

            for (int i = 0; i < extensionNum; i++) {
                if (isEmpty(extensionCat)) {
                    break;
                }
                randomCatList.add(extensionCat.remove(random.nextInt(extensionCat.size())));
            }
            log.debug("{} 老用户推荐优化，扩展分类后权重：{}", baseDto.getUuid(), JSONObject.toJSONString(randomCatList));
        } else {
            while (userCatNum < 3 && !isEmpty(extensionCat)) {
                int extensionCatSize = extensionCat.size();
                randomCatList.add(extensionCat.remove(random.nextInt(extensionCatSize)));
                userCatNum++;
            }
            log.debug("{} 老用户推荐优化，随机分类后权重：{}", baseDto.getUuid(), JSONObject.toJSONString(randomCatList));
        }
        extensionCat = null;

        // 根据权重，概率性的随机出召回的分类，以及每个分类的视频个数
        int randomCatSize = randomCatList.size();
        Map<Integer, Integer> recommendCatNumMap = new HashMap<Integer, Integer>();
        for (int m = 0; m < recommendDto.getVideoNum(); m++) {
            Integer catid = randomCatList.get(random.nextInt(randomCatSize));
            if (recommendCatNumMap.containsKey(catid)) {
                int catVideoNum = recommendCatNumMap.get(catid);
                recommendCatNumMap.put(catid, catVideoNum + 1);
            } else {
                recommendCatNumMap.put(catid, 1);
            }
        }
        randomCatList.clear();
        // 记录用户召回的分类，以及分类的视频个数
        log.info("{} 老用户推荐优化，召回的分类及视频个数：{}", baseDto.getUuid(), JSONObject.toJSONString(recommendCatNumMap));
        recommendDto.setRecommendCat(recommendCatNumMap.keySet().stream().collect(Collectors.toList()));
        recommendDto.setRecommendCatNumMap(recommendCatNumMap);
    }

    /**
     * 获取随机扩展分类
     *
     * @return
     */
    private List<Integer> getExtensionCat(List<CatWeightDto> userCatWeights, List<Integer> excludeCatList) {
        List<Integer> allCat = videosCatService.getCatIdsByStateAndType(XyConstants.open, XyConstants.FIRST_VIDEO_CODE).stream().map(Integer::valueOf).collect(Collectors.toList());
        if (!isEmpty(userCatWeights)) {
            allCat.removeAll(userCatWeights.stream().map(CatWeightDto::getCatId).collect(Collectors.toList()));
        }
        if (!isEmpty(excludeCatList)) {
            allCat.removeAll(excludeCatList);
        }
        return allCat;
    }

    /**
     * 获取用户分类池权重
     *
     * @param baseDto
     * @return
     */
    private List<CatWeightDto> getUserCatWeights(BaseDto baseDto) {
        String date = null;
        LocalDateTime localDate = LocalDateTime.now();
        if(localDate.getHour() < 3){
            date = localDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            date = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        String redisKey = null;
        Integer tagId = null;
        switch (baseDto.getOldUserOptimizeGroup()) {
            case "2":
                redisKey = String.format(RedisUserConstant.user_play_weight_tfidf_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TFIDF_WEIGHT_4_PLAY;
                break;
            case "3":
                redisKey = String.format(RedisUserConstant.user_play_weight_tgi_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TGI_WEIGHT_4_PLAY;
                break;
            case "4":
                redisKey = String.format(RedisUserConstant.user_valid_play_weight_tfidf_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TFIDF_WEIGHT_4_VALIDPLAY;
                break;
            case "5":
                redisKey = String.format(RedisUserConstant.user_valid_play_weight_tgi_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TGI_WEIGHT_4_VALIDPLAY;
                break;
            case "6":
                redisKey = String.format(RedisUserConstant.user_over_play_weight_tfidf_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TFIDF_WEIGHT_4_OVERPLAY;
                break;
            case "7":
                redisKey = String.format(RedisUserConstant.user_over_play_weight_tgi_pool, date, baseDto.getUuid());
                tagId = MongoConstants.USER_CAT_TGI_WEIGHT_4_OVERPLAY;
                break;
            case "8":
                redisKey = String.format(RedisUserConstant.user_avg_play_weight_pool, date, baseDto.getUuid());
                tagId = MongoConstants.AVG_PLAY_WEIGHT;
                break;
            default:
                return null;
        }
        // 从redis获取缓存权重
        Map<String, String> redisValue = redisDB11Service.hgetAll(redisKey);
        if (CollectionUtils.isEmpty(redisValue)) {
            // 获取离线统计的权重
            List<CatWeightDto> catWeightDtoList = userRawTagsService.findUserCatWeightsAndLog10(baseDto.getUuid(), tagId);
            // 将获取到的权重结果，放入redis缓存
            Map<String, String> userCatWeights = new HashMap<String, String>();
            if (!isEmpty(catWeightDtoList)) {
                catWeightDtoList.forEach(w -> {
                    userCatWeights.put(w.getCatId().toString(), w.getWeight().toString());
                });
                redisDB11Service.hmset(redisKey, userCatWeights, ExistConstants.getTheRemainingSecondsOfTheThreeHourForTomorrow());
            }
            return catWeightDtoList;
        } else {
            // 解析缓存的权重
            List<CatWeightDto> catWeightDtoList = new ArrayList<CatWeightDto>();
            redisValue.forEach((key, value) -> {
                Integer catId = Integer.parseInt(key);
                Double weight = Double.parseDouble(value);
                catWeightDtoList.add(new CatWeightDto(catId, weight));
            });
            return catWeightDtoList;
        }
    }

}
