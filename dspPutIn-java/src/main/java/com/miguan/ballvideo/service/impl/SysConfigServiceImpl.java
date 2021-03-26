package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.CacheUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.mapper.SysConfigMapper;

import com.miguan.ballvideo.service.SysConfigService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.SysConfigVo;
import com.miguan.ballvideo.vo.SysVersionVo;
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
	private ToolMofangService toolMofangService;


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
	public Map<String, Object> findSysConfigInfo(CommonParamsVo commonParamsVo) {
		Map<String, Object> configs = new HashMap<String, Object>();
		String channelId = commonParamsVo.getChannelId();
		String appVersion = VersionUtil.getVersion(commonParamsVo.getAppVersion());
		configs.put("table_smallScale_showTime",Global.getInt("table_smallScale_showTime"));//桌面小图标显示间隔
		configs.put("table_smallScale_state",Global.getInt("table_smallScale_state"));//桌面小图标开关
		configs.put("video_auto_play_switch",Global.getInt("video_auto_play_switch"));//视频自动播放开关
		configs.put("app_ScreenPage_showtime",Global.getInt("app_ScreenPage_showtime"));//启动页展示时间开关
		configs.put("safety_tips_up",Global.getInt("safety_tips_up"));//每隔几天出现1次安全提示框
		configs.put("cruel_refusal",Global.getInt("cruel_refusal"));//安全提示框内“残忍拒绝”展示开关
		configs.put("video_first_position",Global.getInt("video_first_position"));//首个广告位置（视频列表）
		configs.put("video_second_position",Global.getInt("video_second_position"));//第二个广告位置（视频列表）
		configs.put("adv_error_batch_num",Global.getInt("adv_error_batch_num"));//批量保存错误日志统计
		configs.put("adv_error_batch_state",Global.getInt("adv_error_batch_state"));//批量保存错误日志统计开关
		configs.put("album_video_screen_day",Global.getInt("album_video_screen_day"));//专辑首页插屏广告隔多少天触发
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
}