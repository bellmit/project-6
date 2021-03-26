package com.miguan.laidian.service;


import com.miguan.laidian.entity.AutoPushConfig;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 自动配置Service
 * @author laiyudan
 * @date 2020-04-24
 */
public interface AutoPushConfigService {

    /**
     * 查询自动推送配置
     *
     * @param status
     * @return
     */
    @Cacheable(value = CacheConstant.QUERY_ALL_BY_STATUS, unless = "#result == null")
    List<AutoPushConfig> queryAllByStatus(Integer status);
    /**
     * 通过id查询自动推送配置
     *
     * @param id
     * @return
     */
    AutoPushConfig queryById(Long id);

    void excecuteTask(String type, String desc);

    /**
     * 查询自动推送配置
     * @param eventType
     * @return
     */
    List<AutoPushConfig> queryByEventType(Integer eventType);
}
