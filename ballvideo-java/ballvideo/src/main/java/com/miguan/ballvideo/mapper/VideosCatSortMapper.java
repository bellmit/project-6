package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.VideosCatSortVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 视频分类排序Mapper
 *
 * @author xy.chen
 * @date 2019-08-13
 **/

public interface VideosCatSortMapper {

    /**
     * 根据渠道和版本号查询排序
     **/
    @Cacheable(value = CacheConstant.QUERY_VIDEOS_CAT_SORT_LIST, unless = "#result == null")
    List<VideosCatSortVo> queryVideosCatSortList(Map<String, Object> params);
}