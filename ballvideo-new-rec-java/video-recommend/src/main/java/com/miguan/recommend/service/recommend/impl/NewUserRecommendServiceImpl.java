package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoQueryDto;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.common.constants.XyConstants;
import com.miguan.recommend.common.util.Global;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.recommend.AbstractRecommendService;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.VideoHotService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import com.miguan.recommend.service.xy.VideosCatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service("newUserRecommendService")
public class NewUserRecommendServiceImpl extends AbstractRecommendService implements VideoRecommendService<VideoRecommendDto> {

    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;
    @Resource(name = "videoHotServiceV3")
    private VideoHotService videoHotServiceV3;
    @Resource
    private VideosCatService videosCatService;
    @Resource
    private BloomFilterService bloomFilterService;

    ExecutorService executor = new ThreadPoolExecutor(200, 1000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Override
    public void recommend(BaseDto baseDto, VideoRecommendDto recommendDto) {
        int jlVideoNum = recommendDto.getIncentiveVideoNum();
        String value = Global.getValue("XYSP_NEW_USER_APPOINT_VIDEO");
        if (StringUtils.isNoneEmpty(value)) {
            JSONObject configVideos = JSONObject.parseObject(value);
            List<String> movieVideos = configVideos.getJSONArray("影视").toJavaList(String.class);
            //List<String> anecdoteVideos = configVideos.getJSONArray("奇闻").toJavaList(String.class);
            //List<String> funnyVideos = configVideos.getJSONArray("搞笑").toJavaList(String.class);
            List<String> girlVideos = configVideos.getJSONArray("美女").toJavaList(String.class);
            //List<String> excludeCatIds = configVideos.getJSONArray("排除分类").toJavaList(String.class);

            log.info("{} 新用户指定内容 开始第{}刷", baseDto.getUuid(), baseDto.getFlushNum());
            List<String> recommendVideos = null;
            if (StringUtils.equals("2", baseDto.getAppointVideoGroup())) {
                //recommendVideos = this.getVideoList(baseDto.getFlushNum().intValue(), movieVideos, funnyVideos, anecdoteVideos);
                recommendVideos = this.getVideoList(baseDto.getFlushNum().intValue(), movieVideos);
                log.info("{} 新用户指定内容 2组结果>>{}", baseDto.getUuid(), JSONObject.toJSONString(recommendVideos));
            } else if (StringUtils.equals("3", baseDto.getAppointVideoGroup())) {
                //recommendVideos = this.getVideoList(baseDto.getFlushNum().intValue(), girlVideos, funnyVideos, anecdoteVideos);
                recommendVideos = this.getVideoList(baseDto.getFlushNum().intValue(), girlVideos);
                log.info("{} 新用户指定内容 3组结果>>{}", baseDto.getUuid(), JSONObject.toJSONString(recommendVideos));
            }

            List<IncentiveVideoHotspot> jlVideoList = new ArrayList<IncentiveVideoHotspot>();
            // 获取激励视频
            recommendDto.setIncentiveVideoNum(9);
            CompletableFuture<Integer> incetiveHotVideo = super.getIncetiveHotVideo(baseDto, recommendDto, jlVideoList, incentiveVideoHotServiceV3New, executor);
            incetiveHotVideo.join();

            List<String> jlVideoIds = jlVideoList.stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList());
            jlVideoIds.removeAll(recommendVideos);
            if(!isEmpty(jlVideoIds)){
                recommendDto.setJlvideo(jlVideoIds.subList(0, Math.min(jlVideoNum, jlVideoIds.size())));
            }
            recommendDto.setSelectedVideo(recommendVideos);

            bloomFilterService.putAll(baseDto.getUuid(), recommendVideos);
        } else {
            log.info("{} 新用户指定内容 视频为空", baseDto.getUuid());
        }
    }

    private List<String> getVideoList(int flushNum, List<String> videos){
        int startIndex = (flushNum - 1) * 7;
        int endIndex = flushNum * 7;
        return videos.subList(startIndex, endIndex);
    }

    private List<String> getVideoList(int flushNum, List<String> videos1, List<String> videos2, List<String> videos3) {
        List<String> recommendVideos = new ArrayList<String>();
        int index1 = 0;
        int index2 = 0;
        int index3 = 0;
        if (flushNum > 1) {
            index1 = (flushNum * 4) - 4;
            index2 = (flushNum * 2) - 2;
            index3 = flushNum - 1;
        }
        index1 = this.copyIndexObject(index1, videos1, recommendVideos);
        index1 = this.copyIndexObject(index1, videos1, recommendVideos);
        index2 = this.copyIndexObject(index2, videos2, recommendVideos);
        index3 = this.copyIndexObject(index3, videos3, recommendVideos);
        index1 = this.copyIndexObject(index1, videos1, recommendVideos);
        index2 = this.copyIndexObject(index2, videos2, recommendVideos);
        index1 = this.copyIndexObject(index1, videos1, recommendVideos);
        return recommendVideos;
    }

    private List<String> getTheLastOnlineVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<String> videos1, List<String> videos2, List<String> excludeCats) {
        long t1 = System.currentTimeMillis();
        List<String> recommendVideos = new ArrayList<String>();
        this.getRandomCatVideo(baseDto, recommendDto, excludeCats, recommendVideos);

        int index = baseDto.getFlushNum().intValue() - 1;
        recommendVideos.add(0, videos1.get(index));
        if (!isEmpty(videos2)) {
            recommendVideos.add(3, videos2.get(index));
        }

        while (recommendVideos.size() > 7){
            recommendVideos.remove(7);
        }
        log.info("{} 新用户指定内容推荐 48刷 总耗时：{}", baseDto.getUuid(), System.currentTimeMillis() - t1);
        return recommendVideos;
    }

    private void getRandomCatVideo(BaseDto baseDto, VideoRecommendDto recommendDto, List<String> excludeCats, List<String> recommendVideos) {
        long t1 = System.currentTimeMillis();
        List<Integer> randomCats = this.get3RandomCat(excludeCats);
        log.info("{} 新用户指定内容推荐 获取到的随机分类:{}", baseDto.getUuid(), JSONObject.toJSONString(randomCats));
        // 计算用户的兴趣分类，每个分类的个数
        Map<Integer, Long> catNum = new HashMap<Integer, Long>();
        for (Integer catid : randomCats) {
            catNum.put(catid, 2L);
        }

        Function<Integer[], Number> f = e -> {
            long pt = System.currentTimeMillis();
            VideoQueryDto<VideoHotspotVo> queryDto = new VideoQueryDto<VideoHotspotVo>(baseDto, e[0], recommendDto.getSensitiveState(), e[1]);
            List<VideoHotspotVo> videoId1 = videoHotServiceV3.findAndFilter(queryDto, null);
            if (log.isDebugEnabled()) {
                log.debug("{} 新用户指定内容推荐 获取用户兴趣分类[{}]视频：{} 个", baseDto.getUuid(), e[0], isEmpty(videoId1) ? 0 : videoId1.size());
            }
            if (!isEmpty(videoId1)) {
                recommendVideos.addAll(videoId1.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList()));
            }
            if (log.isInfoEnabled()) {
                log.info("{} 新用户指定内容推荐 获取用户兴趣分类视频时长：{}", baseDto.getUuid(), (System.currentTimeMillis() - pt));
            }
            return System.currentTimeMillis() - pt;
        };
        CompletableFuture[] listFeture = catNum.entrySet().stream().map(e -> {
            Integer[] params = new Integer[]{e.getKey(), e.getValue().intValue()};
            return CompletableFuture.completedFuture(params).thenApplyAsync(f, executor);
        }).toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(listFeture).join();
        log.info("{} 新用户指定内容推荐 获取随机分类视频耗时：{}", baseDto.getUuid(), System.currentTimeMillis() - t1);
    }

    private List<Integer> get3RandomCat(List<String> excludeCats) {
        List<String> allCatIds = videosCatService.getCatIdsByStateAndType(XyConstants.open, XyConstants.FIRST_VIDEO_CODE);
        if (!isEmpty(excludeCats)) {
            allCatIds.removeAll(excludeCats);
        }
        Random random = new Random();
        List<Integer> randomCats = new ArrayList<Integer>(3);
        while (randomCats.size() < 3) {
            Integer size = allCatIds.size();
            randomCats.add(Integer.parseInt(allCatIds.remove(random.nextInt(size))));
        }
        return randomCats;
    }

    private int copyIndexObject(int index, List<String> sourceList, List<String> targetList) {
        targetList.add(sourceList.get(index));
        return index + 1;
    }
}
