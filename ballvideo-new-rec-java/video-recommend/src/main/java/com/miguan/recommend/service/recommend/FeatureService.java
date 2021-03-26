package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PublicInfo;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.bo.VideoRecommendDto;

public interface FeatureService {

    /**
     * 初始化用户特征
     *
     * @param userTag 用户标识
     * @param ip Ip地址
     */
    public UserFeature initUserFeature(String userTag, String ip);

    /**
     * 缓存用户特征
     *
     * @param recommendDto
     */
    public void saveFeatureToRedis(BaseDto baseDto, VideoRecommendDto recommendDto);
}
