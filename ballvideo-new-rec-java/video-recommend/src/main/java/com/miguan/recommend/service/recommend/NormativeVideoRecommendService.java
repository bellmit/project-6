package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.vo.NormativeVideoRecommendVo;

/**
 * 通用视频推荐服务接口
 * @param <T>
 */
public interface NormativeVideoRecommendService<T> {

    /**
     * 通用视频推荐方法
     *
     * @param recommendDto
     */
    public NormativeVideoRecommendVo recommend(PredictDto paramDto, T recommendDto);
}
