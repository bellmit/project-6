package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.AdvertVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 广告位Mapper
 * @author laiyudan
 * @date 2019-09-09
 **/

public interface AdvertMapper {

    /**
     * 查询广告配置
     * @param param
     * @return
     */
    @Cacheable(value = CacheConstant.QUERY_ADERT_LIST, unless = "#result == null || #result.size()==0")
    List<AdvertVo> queryAdertList(Map<String, Object> param);

    @Cacheable(value = CacheConstant.QUERY_ADERT_LIST_ALL, unless = "#result == null || #result.size()==0")
    List<String> queryPositionType(@Param("mobileType") String mobileType);

    @Cacheable(value = CacheConstant.POSITION_TYPE_GAME, unless = "#result == null || #result.size()==0")
    List<String> queryPositionTypeGame(@Param("mobileType") String mobileType);
}