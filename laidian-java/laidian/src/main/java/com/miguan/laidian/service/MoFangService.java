package com.miguan.laidian.service;

import com.miguan.laidian.entity.Channel;
import com.miguan.laidian.entity.ShieldMenuConfig;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 魔方后台Service
 * @author xy.chen
 * @date 20120-03-23
 **/

public interface MoFangService {

	/**
	 * 跨库查询魔方后台数据，根据版本判断是否屏蔽菜单栏
	 */
	@Cacheable(value = CacheConstant.COUNT_VERSION_LIST, unless = "#result == null")
	List<ShieldMenuConfig> countVersionList(String codes, String appVersion, String appType, int tagType);

	/**
	 * 跨库查询魔方后台数据，根据渠道判断是否屏蔽菜单栏
	 */
	@Cacheable(value = CacheConstant.COUNT_CHANNEL_LIST, unless = "#result == null")
	List<ShieldMenuConfig> countChannelList(String codes, String channelId, String appType, String appVersion,int tagType);

	/**
	 * 跨库查询魔方后台数据，根据版本判断是否屏蔽全部广告
	 */
	@Cacheable(value = CacheConstant.COUNT_FORBIDDEN_VERSION, unless = "#result == null")
	int countVersion(String postitionType, String appVersion, String appType, int tagType);

	/**
	 * 跨库查询魔方后台数据，根据渠道判断是否屏蔽广告
	 */
	@Cacheable(value = CacheConstant.COUNT_FORBIDDEN_CHANNEL, unless = "#result == null")
	int countChannel(String postitionType, String channelId, String appType, String appVersion, int tagType);

	/**
	 * 跨库查询魔方后台数据，获取渠道id和供应商
	 *
	 * @return
	 */
	List<Channel> getChannels();

	/**
	 * 查询魔方后台是否禁用该渠道的广告:true禁用，false非禁用
	 * @param param
	 * @return
	 */
	boolean stoppedByMofang(Map<String, Object> param);
}