package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.AdvertVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 自动推送配置Mapper
 * @author cxy
 * @date 2020-03-31
 **/

public interface AutoPushConfigMapper {

    /**
     * 查询自动推送配置
     *
     * @param param
     * @return
     */
    List<AutoPushConfig> queryAllByStatus(Map<String, Object> param);
    /**
     * 通过id查询自动推送配置
     *
     * @param id
     * @return
     */
    AutoPushConfig queryById(Long id);
}