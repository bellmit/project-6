package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.CacheUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.entity.MarketAudit;
import com.miguan.ballvideo.mapper.SysConfigMapper;
import com.miguan.ballvideo.mapper.VideoPopConfigMapper;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.service.MarketAuditService;
import com.miguan.ballvideo.service.SysConfigService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.*;
import com.miguan.ballvideo.vo.queue.SystemQueueVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统参数ServiceImpl
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Service(value = "sysConfigService")
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private FanoutExchange fanoutExchange;

	@Resource
	private MarketAuditService marketAuditService;

	@Resource
	private ToolMofangService toolMofangService;

	@Resource
	private VideoPopConfigMapper videoPopConfigMapper;

	@Resource
	private AdvertService advertService;

	private static final String GG_VIDEO  = "com.mg.ggvideo";   //果果视频

	private static final int NOT_UPDATE = 10;   //10--否，20--是

	private static final String GG_APP_VERSION = "1.0.0";   //10--否，20--是

	@Override
	public List<SysConfigVo> findAll() {
		return sysConfigMapper.findAll();
	}

	@Override
	public SysConfigVo selectByCode(Map<String, Object> params) {
		return sysConfigMapper.selectByCode(params);
	}

	@Override
	public void initSysConfig() {
		List<SysConfigVo> sysConfigs = findAll();
		CacheUtil.initSysConfig(sysConfigs);
	}

	@Override
	public void reloadAll() {
		String msg = JSON.toJSONString(new SystemQueueVo(SystemQueueVo.FLASH_CACHE));
		rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", msg);
	}

	@Override
	public Map<String, Object> findSysVersionInfo(String appPackage, String appVersion,String channelId) {
		Map<String, Object> restMap = new HashMap<>();
		if (VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_180)) {
			//获取返回是否更新版本
			restMap = getSysVersionInfo(restMap, appPackage, appVersion,channelId);
		} else {
			//临时加功能  2019年12月9日18:54:41   果果视频 不强制更新
			if(StringUtils.isNoneBlank(appPackage) && GG_VIDEO.equals(appPackage.trim())){
				restMap.put("forceUpdate", NOT_UPDATE);
				restMap.put("androidVersion", GG_APP_VERSION);
				restMap.put("iosVersion", GG_APP_VERSION);
			}else {
				restMap.put("forceUpdate", Global.getValue("force_update"));
				restMap.put("androidVersion", Global.getValue("android_version"));
				restMap.put("iosVersion", Global.getValue("ios_version"));
			}
			restMap.put("updateContent", Global.getValue("update_content"));
			restMap.put("androidAddress", Global.getValue("android_address"));
			restMap.put("iosAddress", Global.getValue("ios_address"));
		}
		return restMap;
	}

	/**
	 * 大于1.8版本则查询工具渠道后台的系统版本配置信息
	 * @param restMap
	 * @return
	 */
	private Map<String, Object> getSysVersionInfo(Map<String, Object> restMap, String appPackage, String appVersion,String channelId) {
		//跨库查询魔方后台数据，获取系统版本配置信息
		List<SysVersionVo> sysVersionVoList = toolMofangService.findUpdateVersionSet(appPackage,appVersion);
		if (CollectionUtils.isEmpty(sysVersionVoList)) {
			return restMap;
		}
		SysVersionVo sysVersionVo = sysVersionVoList.get(0);
        if(StringUtils.isNotEmpty(sysVersionVo.getChannel())){
            //判断是否包含当前渠道
			sysVersionVo.setChannel(sysVersionVo.getChannel()+",");
			channelId = channelId + ",";
            if(sysVersionVo.getChannel().contains(channelId)||"all,".equals(sysVersionVo.getChannel())) {
                if (Integer.valueOf(sysVersionVo.getUpdateUserTotalCount()) == -1 || (Integer.valueOf(sysVersionVo.getRealUserUpdateCount()) < Integer.valueOf(sysVersionVo.getUpdateUserTotalCount()))){
                    restMap.put("forceUpdate", sysVersionVo.getForceUpdate());
                }else if((Integer.valueOf(sysVersionVo.getRealUserUpdateCount()) >= Integer.valueOf(sysVersionVo.getUpdateUserTotalCount()))){
					return restMap;
                }
            }else {
                return restMap;
            }
            if (Constant.IOS_MOBILE.equals(sysVersionVo.getMobileType())) {
                restMap.put("iosVersion", sysVersionVo.getAppVersion());
                restMap.put("iosAddress", sysVersionVo.getAppAddress());
                restMap.put("androidVersion", "");
                restMap.put("androidAddress", "");
            } else {
                restMap.put("iosVersion", "");
                restMap.put("iosAddress", "");
                restMap.put("androidVersion", sysVersionVo.getAppVersion());
                restMap.put("androidAddress", sysVersionVo.getAppAddress());
            }
            restMap.put("updateContent", sysVersionVo.getUpdateContent());
        }
		return restMap;
	}

	@Override
	public Map<String, Object> findSysConfigInfo(CommonParamsVo commonParamsVo, AbTestAdvParamsVo queueVo) {
		Map<String, Object> configs = new HashMap<String, Object>();
		String channelId = commonParamsVo.getChannelId();
		String appVersion = VersionUtil.getVersion(commonParamsVo.getAppVersion());
		String catIds = marketAuditService.getCatIdsByChannelIdAndAppVersionFromTeenager(channelId,appVersion);
		String mobileType = commonParamsVo.getMobileType();
		String appPackage = commonParamsVo.getAppPackage();
		Integer videoFirstPosition = null;
		Integer videoSecondPosition = null;
		Integer appScreenPageShowtime = null;
		if (StringUtils.isNotEmpty(mobileType) && StringUtils.isNotEmpty(appPackage)) {
			Map<String, Object> param = new HashMap<>();
            param.put("queryType","position");
			param.put("positionType","personalPage");
			param.put("mobileType",mobileType);
			param.put("appPackage",appPackage);
			List<AdvertCodeVo> list = advertService.getAdvertInfoByParams(queueVo, param, 3);
			if (CollectionUtils.isNotEmpty(list)) {
				videoFirstPosition = list.get(0).getFirstLoadPosition();
				videoSecondPosition = list.get(0).getSecondLoadPosition();
			}
			param.put("positionType","openScreenPageIos");
			list = advertService.getAdvertInfoByParams(queueVo, param, 3);
			if (CollectionUtils.isNotEmpty(list)) {
				appScreenPageShowtime = list.get(0).getFirstLoadPosition();
			}
		}
		//首个广告位置（视频列表）
		if (videoFirstPosition != null) {
			configs.put("video_first_position",videoFirstPosition);
		} else {
			configs.put("video_first_position",Global.getInt("video_first_position"));
		}
		//第二个广告位置（视频列表）
		if (videoSecondPosition != null) {
			configs.put("video_second_position",videoSecondPosition);
		} else {
			configs.put("video_second_position",Global.getInt("video_second_position"));
		}
		//启动页展示时间开关
		if (appScreenPageShowtime != null) {
			configs.put("app_ScreenPage_showtime",appScreenPageShowtime);
		} else {
			configs.put("app_ScreenPage_showtime",Global.getInt("app_ScreenPage_showtime"));
		}
		configs.put("teenager_model_state",StringUtils.isNotEmpty(catIds)?1:0);//青少年模式是否开启
		configs.put("table_smallScale_showTime",Global.getInt("table_smallScale_showTime"));//桌面小图标显示间隔
		configs.put("table_smallScale_state",Global.getInt("table_smallScale_state"));//桌面小图标开关
		configs.put("video_auto_play_switch",Global.getInt("video_auto_play_switch"));//视频自动播放开关
		configs.put("safety_tips_up",Global.getInt("safety_tips_up"));//每隔几天出现1次安全提示框
		configs.put("cruel_refusal",Global.getInt("cruel_refusal"));//安全提示框内“残忍拒绝”展示开关
		configs.put("adv_error_batch_num",Global.getInt("adv_error_batch_num"));//批量保存错误日志统计
		configs.put("adv_error_batch_state",Global.getInt("adv_error_batch_state"));//批量保存错误日志统计开关
		configs.put("album_video_screen_day",Global.getInt("album_video_screen_day"));//专辑首页插屏广告隔多少天触发
		configs.put("m3u8_video_switch",Global.getInt("m3u8_video_switch"));//m3u8视频开关(10开 20关)
		configs.put("host_list",Global.getInt("host_list"));//热门榜单排序

		MarketAudit marketAudit = marketAuditService.getCatIdsByChannelIdAndAppVersion(channelId, appVersion);
		configs.put("game_state",marketAudit == null ? 1 : marketAudit.getGameState()); //小游戏开关1开启0关闭 没配置市场审核或者未启用市场审核 默认为开启
		int hotState = marketAudit == null ? 1 : marketAudit.getHotState();//热榜开关 1开启 0关闭 没配置市场审核或者未启用市场审核 默认为开启
		int hotListSwitch = 0;//首页热榜开关 1开启 0关闭
		int hotSearchSwitch = 0;//搜索页热榜开关 1开启 0关闭
		if (hotState == 1) {
			String switchValue = Global.getValue("host_list_switch");
			String[] switchStr = switchValue.split(",");
			hotListSwitch = Integer.valueOf(switchStr[0]);
			hotSearchSwitch = Integer.valueOf(switchStr[1]);
		}
		configs.put("hot_list_switch",hotListSwitch);
		configs.put("hot_search_switch",hotSearchSwitch);
		//2.2版本增加市场审核开关是否开启
		if(VersionUtil.compareIsHigh(appVersion,Constant.APPVERSION_210)){
			if(VersionUtil.compareIsHigh(appVersion,Constant.APPVERSION_249)) {
				configs.put("market_audit", 0);//2.5.0及以后版本统一由魔方后台控制广告
			} else {
				if (marketAudit != null) {
					configs.put("market_audit", 1);//开启
				} else {
					configs.put("market_audit", 0);//关闭
				}
			}
		}
		return configs;
	}

	@Override
	public int reportSysVersionInfo(CommonParamsVo commonParams) {
		return toolMofangService.updateSysVersionInfo(commonParams);
	}

	@Override
	public void reloadByKey(String adConfig_cache) {
		String msg = JSON.toJSONString(new SystemQueueVo(adConfig_cache));
		rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", msg);
	}

	@Override
	public List<VideoPopConfigVo> queryPopConfigList(String appPackage, String appVersion){
		return videoPopConfigMapper.queryPopConfigList(appPackage, appVersion);
	}

	@Override
	public List<VideoCatInfoVo> selectVideoCatInfo() {
		return sysConfigMapper.selectVideoCatInfo();
	}
}