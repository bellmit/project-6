package com.miguan.ballvideo.mapper;

import com.miguan.ballvideo.entity.VideoGather;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.VideosCatVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 视频分类表Mapper
 *
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface VideosCatMapper {

    /**
     * 通过条件查询首页视频分类列表
     **/
    List<VideosCatVo> findFirstVideosCatList(Map<String, Object> params);

    @Cacheable(value = CacheConstant.FIND_CATIDS_NOTIN, unless = "#result == null")
    List<String> findCatIdsNotIn(Map<String, Object> paramMap);

    @Cacheable(value = CacheConstant.FIRSTVIDEOS_CATLIST, unless = "#result == null || #result.size()==0")
    List<VideosCatVo> firstVideosCatList(@Param("type") String type);

    @Cacheable(value = CacheConstant.FIND_CATIDS_BYSTATE, unless = "#result == null || #result.size()==0")
    List<String> findCatIdsByState(@Param("state") Integer state);

    List<String> getCatIdsByStateAndType(@Param("state") Integer state,@Param("type") String type);

    List<VideoGather> findGatherIdsNotIn(@Param("excludeIds") String[] excludeIds);

    @Cacheable(value = CacheConstant.QUERY_CATIDS_LIST, unless = "#result == null")
    List<String> queryCatIdsList(String appPackage);

    int existByCatIdAndApp(Map params);
}