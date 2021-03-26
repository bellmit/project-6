package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.SmallVideoVo;

import java.util.List;
import java.util.Map;

/**
 * 视频源列表Mapper
 * @author xy.chen
 * @date 2019-08-02
 **/

public interface SmallVideoMapper {

	/**
	 * 通过条件查询视频源列列表
	 **/
	List<SmallVideoVo>  findVideosList(Map<String, Object> params);

	/**
	 * 更新举报次数
	 **/
	int updateReportCount(Map<String, Object> params);

    List<SmallVideoVo> findFirstVideoListByMyCollection(Map<String, Object> map);

	/**
	 *
	 * 通过条件查询小视频列列表
	 *
	 **/
	List<SmallVideoVo>  findSmallVideosList(Map<String, Object> params);

	SmallVideoVo  findVideosDetailByOne(Map<String, Object> params);

    int updateIOSVideosCount(Map<String, Object> map);
}