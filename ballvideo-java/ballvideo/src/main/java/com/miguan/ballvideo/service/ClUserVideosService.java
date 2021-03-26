package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.ClUserVideoInfoVo;
import com.miguan.ballvideo.vo.ClUserVideosVo;

import java.util.List;
import java.util.Map;

/**
 * 用户视频关联表Service
 * @author xy.chen
 * @date 2019-08-09
 **/

public interface ClUserVideosService {

	/**
	 * 
	 * 通过条件查询用户视频关联列表
	 * 
	 **/
	List<ClUserVideosVo>  findClUserVideosList(Map<String, Object> params);

	/**
	 * 
	 * 新增用户视频关联信息
	 * 
	 **/
	int saveClUserVideos(ClUserVideosVo clUserVideosVo);

	/**
	 * 
	 * 修改用户视频关联信息
	 * 
	 **/
	int updateClUserVideos(ClUserVideosVo clUserVideosVo);


	/**
	 * 获取用户视频收藏等信息
	 * @param userId
	 * @param list
	 * @return
	 */
	List<ClUserVideoInfoVo> findUserVideo(String userId, List<Long> list);
}