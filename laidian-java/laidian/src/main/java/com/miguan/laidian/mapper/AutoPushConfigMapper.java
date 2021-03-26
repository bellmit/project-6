package com.miguan.laidian.mapper;



import com.miguan.laidian.entity.AutoPushConfig;

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

    /**
     * 查询自动推送配置
     * @param param
     * @return
     */
    List<AutoPushConfig> queryByEventType(Map<String, Object> param);
}