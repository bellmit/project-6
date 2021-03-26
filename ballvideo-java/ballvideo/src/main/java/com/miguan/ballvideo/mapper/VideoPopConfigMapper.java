package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.VideoPopConfigVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 视频弹窗配置Mapper
 * @author xy.chen
 * @date 2020-07-17
 **/

public interface VideoPopConfigMapper {

	/**
	 * 
	 * 通过条件查询视频弹窗配置列表
	 * 
	 **/
	@Cacheable(value = CacheConstant.QUERY_POP_CONFIG_LIST, unless = "#result == null")
	List<VideoPopConfigVo>  queryPopConfigList(@Param("appPackage") String appPackage, @Param("appVersion") String appVersion);
}