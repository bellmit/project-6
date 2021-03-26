package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;

/**
 * 菜单栏配置表Service
 * @author hyl
 * @date 2019年9月11日10:30:28
 **/

public interface ClMenuConfigService {
	/**
	 * @Description: 查询app菜单 根据app
	 * @Param:
	 * @return: findClMenuConfigListByAppPackage
	 * @Author: hyl
	 * @Date: 2019年12月3日14:19:51
	 */
	ResultMap findClMenuByAppPackageOrFilterChannel(String channelId, String deviceId, String appVersion, String appPackagee,String abExp);

}