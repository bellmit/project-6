package com.miguan.recommend.service.recommend.impl;

import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoDetailRecommendDto;
import com.miguan.recommend.service.BloomFilterService;
import com.miguan.recommend.service.mongo.VideoScenairoSimilarService;
import com.miguan.recommend.service.recommend.CatRecommendService;
import com.miguan.recommend.service.recommend.VideoRecommendService;
import com.miguan.recommend.vo.VideoRecommendVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

@Slf4j
@Service("videoDetailRecommendService")
public class VideoDetailRecommendServiceImpl implements VideoRecommendService<VideoDetailRecommendDto> {

    @Value("${spring.profiles.active}")
    private String active;
    @Resource
    private CatRecommendService catRecommendService;
    @Resource
    private VideoScenairoSimilarService videoScenairoSimilarService;
    @Resource
    private BloomFilterService bloomFilterService;

    @Override
    public void recommend(BaseDto baseDto, VideoDetailRecommendDto recommendDto) {
        VideoRecommendVo recommendVo = catRecommendService.recommend(baseDto, recommendDto.getCatid(), recommendDto.getSensitiveState(), recommendDto.getNum());
        if(recommendVo == null || CollectionUtils.isEmpty(recommendVo.getVideo())){
            return;
        }
        recommendDto.setRecommendVideo(recommendVo.getVideo());
    }
}
