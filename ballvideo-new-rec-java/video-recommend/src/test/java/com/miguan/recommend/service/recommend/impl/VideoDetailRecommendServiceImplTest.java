package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.VideoDetailRecommendDto;
import com.miguan.recommend.vo.VideoRecommendVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VideoDetailRecommendServiceImplTest {

    @Resource
    private VideoDetailRecommendServiceImpl videoDetailRecommendService;

    @Test
    public void test(){
        BaseDto baseDto = new BaseDto();
        baseDto.setUuid("11111111111");

        VideoDetailRecommendDto recommendDto = new VideoDetailRecommendDto();
        recommendDto.setNum(8);
        recommendDto.setCatid(10010);
        recommendDto.setSensitiveState(1);

        videoDetailRecommendService.recommend(baseDto, recommendDto);

        VideoRecommendVo recommendVo = new VideoRecommendVo(recommendDto.getRecommendVideo());
        System.out.println(JSONObject.toJSONString(recommendVo));
    }
}