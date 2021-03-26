package com.miguan.recommend.service.recommend.normativeimpl;

import com.miguan.recommend.bo.NormativeIncentiveVideoRecommendDto;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.service.recommend.IncentiveVideoHotService;
import com.miguan.recommend.service.recommend.NormativeVideoRecommendService;
import com.miguan.recommend.vo.NormativeVideoRecommendVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service(value = "normativeIncentiveVideoRecommendService")
public class NormativeIncentiveVideoRecommendServiceImpl extends NormativeRecommendService implements NormativeVideoRecommendService<NormativeIncentiveVideoRecommendDto> {

    private static ExecutorService executor = new ThreadPoolExecutor(200, 2000, 10L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000));

    @Resource(name = "incentiveVideoHotServiceV3New")
    private IncentiveVideoHotService incentiveVideoHotServiceV3New;

    @Override
    public NormativeVideoRecommendVo recommend(PredictDto predictDto, NormativeIncentiveVideoRecommendDto recommendDto) {
        // 获取激励视频
        CompletableFuture<Integer> incentiveFuture = this.getIncentiveVideo(predictDto, recommendDto);
        incentiveFuture.join();
        List<String> incentiveVideo = recommendDto.getIncetiveVideoList().stream().map(IncentiveVideoHotspot::getVideo_id).collect(Collectors.toList());
        super.cacheVideo(predictDto.getDevice_id(), null, incentiveVideo);
        return new NormativeVideoRecommendVo(incentiveVideo, null);
    }

    public CompletableFuture<Integer> getIncentiveVideo(PredictDto predictDto, NormativeIncentiveVideoRecommendDto recommendDto) {
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
}
