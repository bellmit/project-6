package com.miguan.recommend.service.recommend.impl;

import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.bo.VideoRecommendDto;
import com.miguan.recommend.service.recommend.FeatureService;
import com.miguan.recommend.service.recommend.impl.v8.OldUserRecommendServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OldUserRecommendServiceImplTest {

    @Resource
    private FeatureService featureService;
    @Resource
    private OldUserRecommendServiceImpl oldUserRecommendService;

    @Test
    public void test(){
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.setNewApp(true);
        publicInfo.setChangeChannel("dqsp_wm1_gdt07");
        publicInfo.setChannel("dqsp_wm1_gdt07");

        UserFeature userFeature = featureService.initUserFeature("c8deac800dbf4988812e7fec6a86cbb1", "127.0.0.1");

        BaseDto baseDto = new BaseDto();
//        baseDto.setUuid("001479c442ee4102bd84bd60b5d03a41");
        baseDto.setUuid("101479c442ee4102bd84bd60b5d03a41");
        baseDto.setFlushNum(1L);
        baseDto.setCatFlushNum(1L);
        baseDto.setPublicInfo(publicInfo);
        baseDto.setUserFeature(userFeature);
        baseDto.setCtrGroup("1");
        baseDto.setCvrGroup("1");
        baseDto.setSpecialGroup("1");
        baseDto.setOldUserOptimizeGroup("2");

        VideoRecommendDto recommendDto = new VideoRecommendDto();
        recommendDto.setDefaultCat("49,54");
        recommendDto.setVideoNum(7);
        recommendDto.setIncentiveVideoNum(1);
        recommendDto.setSensitiveState(0);
        oldUserRecommendService.recommend(baseDto, recommendDto);


    }
}