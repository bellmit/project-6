package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.service.recommend.FeatureService;
import com.miguan.recommend.vo.VideoRecommendVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CatRecommendServiceImplTest {

    @Resource
    private CatRecommendServiceImpl catRecommendService;
    @Resource
    private FeatureService featureService;

    @Test
    public void test(){
        PublicInfo publicInfo = new PublicInfo();
        publicInfo.setNewApp(true);
        publicInfo.setChangeChannel("dqsp_wm1_gdt07");
        publicInfo.setChannel("dqsp_wm1_gdt07");

        UserFeature userFeature = featureService.initUserFeature("c8deac800dbf4988812e7fec6a86cbb1", "127.0.0.1");

        BaseDto baseDto = new BaseDto();
        baseDto.setUuid("c8deac800dbf4988812e7fec6a86cbb1");
        baseDto.setFlushNum(1L);
        baseDto.setCatFlushNum(1L);
        baseDto.setPublicInfo(publicInfo);
        baseDto.setUserFeature(userFeature);
        baseDto.setCtrGroup("1");
        baseDto.setCvrGroup("1");
        baseDto.setSpecialGroup("1");
        VideoRecommendVo recommendVo = catRecommendService.recommend(baseDto, 49, 0, 8);
        System.out.println(JSONObject.toJSONString(recommendVo));
    }

}