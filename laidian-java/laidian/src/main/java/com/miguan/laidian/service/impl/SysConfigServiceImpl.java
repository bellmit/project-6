package com.miguan.laidian.service.impl;


import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.CacheUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.adv.AdvFieldType;
import com.miguan.laidian.dynamicquery.Dynamic3Query;
import com.miguan.laidian.mapper.SysConfigMapper;
import com.miguan.laidian.mapper.VideoMapper;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.SysConfigService;
import com.miguan.laidian.service.ToolMofangService;
import com.miguan.laidian.vo.SysConfigVo;
import com.miguan.laidian.vo.SysVersionVo;
import com.miguan.laidian.vo.queue.SystemQueueVo;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service(value = "sysConfigService")
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Resource
    private FanoutExchange fanoutExchange;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private VideoMapper videosMapper;

    @Resource
    private Dynamic3Query dynamic3Query;

    @Resource
    private RedisService redisService;

    @Override
    public List<SysConfigVo> findAll() {
        return sysConfigMapper.findAll();
    }

    @Override
    public List<SysConfigVo> selectByCode(Map<String, Object> params) {
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
    public Map<String, Object> findSysConfigInfo(String appType) {
        Map<String, Object> restMap = new HashMap<>();
        if (StringUtils.isEmpty(appType)) {
            appType = Constant.appXld;
        }
        //是否展示快捷设置来电秀页面
        restMap.put("fastSettingLaidian", Global.getValue("fast_setting_laidian", appType));
        //启动时间配置
        restMap.put("advInterval", Global.getValue("adv_interval", appType));
        //查看详情页广告调起个数
        restMap.put("numberAdv", Global.getValue("number_adv", appType));
        //批量保存错误日志统计数量
        restMap.put("adv_error_batch_num", Global.getInt("adv_error_batch_num", appType));
        //批量保存错误日志统计开关
        restMap.put("adv_error_batch_state", Global.getInt("adv_error_batch_state", appType));
        //锁屏权限-针对Vivo市场上线渠道开关
        restMap.put("vivo_lock_screen_auth", Global.getInt("vivo_lock_screen_auth", appType));
        //app角标时间配置
        restMap.put("app_message_corner", Global.getValue("app_message_corner", appType));

        restMap.put("app_message_showtime", Global.getValue("app_message_showtime", appType));

        return restMap;
    }

    /**
     * 上报版本更新
     *
     * @param commonParams
     * @return
     */
    @Override
    public int reportSysVersionInfo(CommonParamsVo commonParams) {
        return toolMofangService.updateSysVersionInfo(commonParams);
    }

    /**
     * >=2.5.4版本则查询工具渠道后台的系统版本配置信息
     * @return
     */
    @Override
    public Map<String, Object> getSysVersionInfo(String appPackage, String appVersion,String channelId) {
        //跨库查询魔方后台数据，获取系统版本配置信息
        Map<String, Object> restMap = new HashMap<>();
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
            if (Constant.IOS.equals(sysVersionVo.getMobileType())) {
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
    public ResultMap projectCondition() {
        String errorMsg = "";
        try {
            Map<String, Object> params = new HashMap<>(2);
            params.put("tagIds", "1000");
            videosMapper.findVideosListById(params);
            log.info("【来电数据库laidian】：查询成功！");
        } catch (Exception e) {
            log.error("【来电数据库laidian】：查询失败！{}",e);
            errorMsg = errorMsg + "【来电数据库laidian】：查询失败！";
        }
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("projectCondition", "projectCondition");
            param.put("positionType", "detailPage");
            param.put("mobileType", Constant.Android);
            param.put("appType", Constant.appXld);
            dynamic3Query.getAdversPositionInfo(param,AdvFieldType.PostitionInfo);
            log.info("【广告数据库ballvideoadv】：查询成功！");
        } catch (Exception e) {
            log.error("【广告数据库ballvideoadv】：查询失败！{}",e);
            errorMsg = errorMsg + "【广告数据库ballvideoadv】：查询失败！";
        }
        try {
            toolMofangService.findUpdateVersionSet(Constant.appXld, Constant.APPVERSION_278);
            log.info("【魔方数据库channel_tool_mofang】：查询成功！");
        } catch (Exception e) {
            log.error("【魔方数据库channel_tool_mofang】：查询失败！{}",e);
            errorMsg = errorMsg + "【魔方数据库channel_tool_mofang】：查询失败！";
        }
        try {
            redisService.get(RedisKeyConstant.START_SYN_DISTINCT_FLAG);
            log.info("【来电Redis-db5】：查询成功！");
        } catch (Exception e) {
            log.error("【来电Redis-db5】：查询失败！{}",e);
            errorMsg = errorMsg + "【来电Redis-db5】：查询失败！";
        }

        if ("".equals(errorMsg)) {
            return ResultMap.success(200, "来电项目检测情况：正常！");
        } else {
            errorMsg = "来电项目检测情况：" + errorMsg;
            return ResultMap.error(400, errorMsg);
        }
    }
}