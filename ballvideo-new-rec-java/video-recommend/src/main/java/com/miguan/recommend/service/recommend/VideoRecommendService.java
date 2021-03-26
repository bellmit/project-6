package com.miguan.recommend.service.recommend;

import com.google.common.collect.Lists;
import com.miguan.recommend.bo.BaseDto;

import java.util.List;

public interface VideoRecommendService<T> {

    /**
     * 推荐服务入口
     *
     * @param recommendDto
     */
    public void recommend(BaseDto baseDto, T recommendDto);

}
