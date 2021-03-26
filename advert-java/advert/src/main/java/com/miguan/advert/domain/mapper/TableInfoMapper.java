package com.miguan.advert.domain.mapper;

import com.miguan.advert.config.redis.util.CacheConstant;
import com.miguan.advert.domain.vo.TableInfo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface TableInfoMapper {

    /**
     * 查询表信息
     * @return
     */
    @Cacheable(value = CacheConstant.FIND_TABLE_INFO, unless = "#result == null || #result.size()==0")
    List<TableInfo> findTableInfo(String tableName);

    @Cacheable(value = CacheConstant.FIND_TABLE_COLUMN_COMMON, unless = "#result == null")
    String findTableColumnCommon(Map params);
}
