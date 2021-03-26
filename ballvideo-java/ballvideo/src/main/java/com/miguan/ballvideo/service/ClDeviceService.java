package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.ClDeviceVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 设备表Service
 * @author daoyu
 * @date 2020-06-09
 **/

public interface ClDeviceService {

	/**
	 *
	 * 通过条件查询设备列表
	 * @param deviceId
	 * @param appPackage
	 * @return
	 *
	 **/
	ClDeviceVo  getDeviceByDeviceIdAppPackage(String deviceId,String appPackage);

	/**
	 * 设备启动 app 上报
	 * @param request
	 * @param clDeviceVo 用户实体
	 * @return
	 */
	boolean startup(HttpServletRequest request, ClDeviceVo clDeviceVo);

	/**
	 * 获取全部推送用户的tokens，传入的是 HashMap
	 */
	List<ClDeviceVo> findAllTokens(Map<String, Object> params);


	/**
	 * 获取兴趣人群推送的设备数量
	 * @param params
	 * @return
	 */
	public Integer getAllTokensCountByDistinct(Map<String, Object> params);

	/**
	 * 获取需要推送的所有设备数量
	 * @param appPackage
	 * @return
	 */
	public Integer getAllTokensCount(String appPackage);

	List<ClDeviceVo> findAllTokensByDistinct(Map<String, Object> params);

}