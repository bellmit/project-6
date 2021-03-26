package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.vo.SysConfigVo;
import com.miguan.ballvideo.vo.VideoPopConfigVo;
import com.miguan.ballvideo.vo.VideoCatInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 系统参数Service
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
*/
public interface SysConfigService {

   	 /**
   	  * 查询所有配置
   	  * @return
   	  * @throws Exception
   	  */
	 List<SysConfigVo> findAll();

	SysConfigVo selectByCode(Map<String, Object> params);

	public void initSysConfig();

	/**
	 * 更新分布式服务的每个服务器的缓存
	 */
    void reloadAll();

	/**
	 * 获取系统版本配置信息
	 * @return
	 */
	Map<String, Object> findSysVersionInfo(String appPackage, String appVersion,String channelId);

	Map<String, Object> findSysConfigInfo(CommonParamsVo commonParamsVo, AbTestAdvParamsVo queueVo);

	int reportSysVersionInfo(CommonParamsVo commonParams);

    void reloadByKey(String adConfig_cache);

	/**
	 * 通过条件查询视频弹窗配置列表
	 *
	 * @param appPackage
	 * @param appVersion
	 * @return
	 */
	List<VideoPopConfigVo>  queryPopConfigList(String appPackage, String appVersion);

	List<VideoCatInfoVo> selectVideoCatInfo();
}
