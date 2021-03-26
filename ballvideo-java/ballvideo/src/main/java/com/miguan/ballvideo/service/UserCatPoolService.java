package com.miguan.ballvideo.service;

import java.util.Map;

/**
 * 用户视频分类兴趣池
 * @author laiyd
 * @date 2020-08-26
 **/

public interface UserCatPoolService {

	/**
	 * 更新用户视频分类到redis
	 *
	 * @param params
	 */
	boolean addRedisVideosCatInfo(Map<String, Object> params);

}