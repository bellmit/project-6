package com.miguan.flow.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.util.Map;

/**
 * @Description 魔方mapper
 **/
@DS("mofang")
public interface MofangMapper {

    /**
     * 根据版本判断是否屏蔽全部广告
     * @param params
     * @return
     */
    int countVersion(Map<String, Object> params);

    /**
     * 非全部的屏蔽根据渠道查询是否屏蔽广告
     * @param params
     * @return
     */
    int countChannel(Map<String, Object> params);
}

