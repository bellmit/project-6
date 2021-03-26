package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.FirstVideosDeleteDto;
import com.miguan.ballvideo.dto.FirstVideosDto;
import com.miguan.ballvideo.dto.UserCenterDto;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.vo.FirstVideos;
import com.miguan.ballvideo.vo.video.FirstVideoDetailVo;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import com.miguan.ballvideo.vo.video.VideosInfoVo;
import com.miguan.ballvideo.vo.video.VideosParamVo;

import java.util.List;
import java.util.Map;

/*
* 1.6.1 后新接口往这里迁移
* */
public interface FirstVideosService {

	/**
	 * 首页非推荐接口V1.6.1
	 *
	 * @param params
	 * @return
	 */
	FirstVideos161Vo firstVideosList161(AbTestAdvParamsVo queueVo, Map<String, Object> params);

	/**
	 * 首页推荐接口V1.6.1
	 *
	 * @param params
	 * @return
	 */
	FirstVideos161Vo firstRecommendVideosList161(Map<String, Object> params);

	/**
	 * 首页推荐接口V1.8
	 *
	 * @param params
	 * @return
	 */
	FirstVideos161Vo firstRecommendVideosList18(VideoParamsDto params, AbTestAdvParamsVo queueVo);

	/**
	 * 首页推荐接口（青少年模式）V2.0.0
	 *
	 * @param params
	 * @return
	 */
	FirstVideos161Vo findRecommendByTeenager(Map<String,Object> params);
	/**
	 * 首页非推荐接口（青少年模式）V2.0.0
	 *
	 * @param params
	 * @return
	 */
	FirstVideos161Vo findNoRecommendByTeenager(Map<String,Object> params);

	/**
	 * 视频详情页接口V2.5以上版本
	 * @param params
	 * @return
	 */
	FirstVideoDetailVo firstVideosDetailList25(AbTestAdvParamsVo queueVo, Map<String, Object> params);

	/**
	 * 用户发布视频
	 * @param firstVideosDto
	 * @return
	 */
	ResultMap videoPublication(FirstVideosDto firstVideosDto);

	/**
	 * 用户视频删除
	 * @return
	 */
	ResultMap videoDelete(FirstVideosDeleteDto deleteDto);

	ResultMap center(UserCenterDto userCenterDto);

	FirstVideos getFirstVideosById(Long id);

	/**
	 * 沉浸流引导页视频
	 * @param deviceId
	 * @param channelId
	 * @param publicInfo
	 * @return
	 */
	FirstVideos161Vo immerseVideosList(String deviceId, String channelId, String publicInfo);

	/**
	 * 根据视频ID查询视频信息
	 * @param paramVo
	 * @return
	 */
	List<VideosInfoVo> getFirstVideosByIds(VideosParamVo paramVo);

	void saveVideoCache(String videoIds);

	void flashCacheToRedis(String options,String cacheStr);
}