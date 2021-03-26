package com.miguan.laidian.mapper;


import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.vo.Advert;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 广告位Mapper
 *
 * @author zhangbinglin
 * @date 2019-06-27
 **/

public interface AdvertMapper {

    @Cacheable(value = CacheConstant.QUERY_ADERT_LIST, unless = "#result == null || #result.size()==0")
    List<Advert> queryAdertList(Map<String, Object> param);
}