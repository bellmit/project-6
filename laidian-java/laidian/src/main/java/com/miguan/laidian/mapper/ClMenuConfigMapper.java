package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ClMenuConfig;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 菜单栏配置表Mapper
 *
 * @author xy.chen
 * @date 2019-08-23
 **/

public interface ClMenuConfigMapper {

    /**
     * 通过条件查询菜单栏配置列表
     **/
    @Cacheable(value = CacheConstant.FIND_CLMENUCONFIGLIST, unless = "#result == null")
    List<ClMenuConfig> findClMenuConfigList(Map<String, Object> params);

}