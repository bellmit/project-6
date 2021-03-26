package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.CatRecommendDto;
import com.miguan.recommend.common.aop.Base;
import com.miguan.recommend.vo.VideoRecommendVo;

import java.util.List;

public interface CatRecommendService {

    public VideoRecommendVo recommend(BaseDto baseDto, Integer catId, Integer sensitive, Integer num);
}
