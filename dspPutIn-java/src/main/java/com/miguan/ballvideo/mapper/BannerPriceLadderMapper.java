package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.AdvertVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 阶梯广告Mapper
 * @author cxy
 * @date 2020-03-31
 **/

public interface BannerPriceLadderMapper {

    /**
     * 查询阶梯广告配置
     *
     * @param param
     * @return
     */
    @Cacheable(value = CacheConstant.QUERY_LADDER_ADERT_LIST, unless = "#result == null || #result.size()==0")
    List<AdvertVo> queryLadderAdertList(Map<String, Object> param);
}