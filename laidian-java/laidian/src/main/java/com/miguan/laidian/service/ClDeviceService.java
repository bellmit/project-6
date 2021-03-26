package com.miguan.laidian.service;

import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.ClUserVo;

import java.util.List;
import java.util.Map;

/**
 * 设备表Service
 * @author daoyu
 * @date 2020-06-09
 **/

public interface ClDeviceService {

	/**
	 * 设备启动 app 上报设备信息
	 * @param clDeviceVo 用户实体
	 * @return
	 */
	boolean startup(ClDeviceVo clDeviceVo);

	/**
	 * 获取全部推送用户的tokens，传入的是 HashMap
	 */
	List<ClUserVo> findAllTokens(Map<String, Object> params);

	/**
	 * 获取所有用户token条数
	 *
	 * @param params
	 * @return
	 */
	int countClDevice(Map<String, Object> params);

	/**
	 * 根据distinctId获取token信息
	 * @param params
	 * @return
	 */
	List<ClDeviceVo> findAllTokensByDistinct(Map<String, Object> params);
}