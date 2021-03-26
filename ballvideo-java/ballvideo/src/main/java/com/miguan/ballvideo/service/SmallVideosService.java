package com.miguan.ballvideo.service;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.vo.SmallVideosVo;
import com.miguan.ballvideo.vo.video.SmallVideos16Vo;
import com.miguan.ballvideo.vo.video.SmallVideosNewVo;

import java.util.List;
import java.util.Map;

/**
 * 小视频列表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface SmallVideosService {

	/**
	 * 
	 * 通过条件查询小视频列列表(分页)
	 * 
	 **/
	Page<SmallVideosVo> findSmallVideosList(Map<String, Object> params, int currentPage, int pageSize);

	/**
	 *
	 * 通过条件查询小视频列列表
	 *
	 **/
	List<SmallVideosVo> findSmallVideosList(Map<String, Object> params);

	public SmallVideosNewVo findSmallVideosList13(Map<String, Object> params, int currentPage, int flag);

	public SmallVideos16Vo findSmallVideosList16(Map<String, Object> params, int currentPage, int flag);

	SmallVideos16Vo findSmallVideosList17(AbTestAdvParamsVo queueVo, Map<String, Object> params);
}