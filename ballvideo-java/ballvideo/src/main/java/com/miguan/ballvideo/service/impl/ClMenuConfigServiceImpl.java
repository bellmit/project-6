package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.controller.SysController;
import com.miguan.ballvideo.entity.ClMenuConfig;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.repositories.ClMenuConfigDao;
import com.miguan.ballvideo.service.ClMenuConfigService;
import com.miguan.ballvideo.service.MarketAuditService;
import com.miguan.ballvideo.vo.queue.UserLabelQueueVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * ClMenuConfigServiceImpl  查询APP菜单功能
 *
 * @author HYL
 * @date 2019年9月11日10:22:45
 **/
@Service
public class ClMenuConfigServiceImpl implements ClMenuConfigService {

    private Logger log = LoggerFactory.getLogger(SysController.class);

    @Autowired
    private ClMenuConfigDao clMenuConfigDao;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private MarketAuditService marketAuditService;

    //默认APP包名，向上兼容
    private final String DEFAULT_APP_PACKAGE = "com.mg.xyvideo";

    /**
     * 首页菜单栏
     *
     * @param channelId  渠道ID
     * @param deviceId   设备ID
     * @param appVersion 版本
     * @param appPackage 马甲包
     * @return
     */
    @Override
    @Transactional
    public ResultMap findClMenuByAppPackageOrFilterChannel(
            String channelId,
            String deviceId,
            String appVersion,
            String appPackage,
            String abExp) {
        if (appPackage == null || ("").equals(appPackage)) {
            appPackage = DEFAULT_APP_PACKAGE;
        }
        if (StringUtils.isEmpty(channelId)) {
            channelId = " ";
        }
        //旧版本默认1.6.0
        if (StringUtils.isEmpty(appVersion)) {
            appVersion = "1.6.0";
        }
        if (VersionUtil.compareIsHigh(Constant.APPVERSION_300, appVersion) && !StringUtils.isEmpty(deviceId)) {
            // 如果用户标签为空，创建用户标签
            String jsonStr = JSON.toJSONString(new UserLabelQueueVo(deviceId, channelId, appVersion, appPackage));
            rabbitTemplate.convertAndSend(
                    RabbitMQConstant.UserLabel_EXCHANGE,
                    RabbitMQConstant.UserLabel_KEY, jsonStr);
        }
        List<ClMenuConfig> data = clMenuConfigDao.findClMenuConfigListByAppPackage(appPackage, channelId, appVersion);
        if (CollectionUtils.isNotEmpty(data)) {
            MenuConfigAbTest(abExp, data);
            return ResultMap.success(data);
        }
        return ResultMap.success("");
    }

    /**
     * AB测试方案:对照组展示「专辑」tab; 实验组1展示「小说」tab；实验组2:展示[直播]tab
     * 对照组展示「小视频」tab; 实验组1展示「特价购」tab；实验组2:展示[1.9元包邮]tab
     * @param abExp
     * @param data
     */
    private void MenuConfigAbTest(String abExp, List<ClMenuConfig> data) {
        boolean exist1 = false;
        boolean exist2 = false;
        int showFlag1 = getShowFlag1(abExp);
        int showFlag2 = getShowFlag2(abExp);
        Iterator<ClMenuConfig> it = data.iterator();
        while(it.hasNext()){
            ClMenuConfig config = it.next();
            if ("专辑".equals(config.getTitle())) {
                exist1 = true;
            }
            if ("小视频".equals(config.getTitle())) {
                exist2 = true;
            }
            if (showFlag1 == 1) {
                if ("小说".equals(config.getTitle()) || "直播".equals(config.getTitle())) {
                    it.remove();
                }
            } else if (showFlag1 == 2) {
                if ("专辑".equals(config.getTitle()) || "直播".equals(config.getTitle())) {
                    it.remove();
                }
            } else if (showFlag1 == 3) {
                if ("专辑".equals(config.getTitle()) || "小说".equals(config.getTitle())) {
                    it.remove();
                }
            }
            if (showFlag2 == 1) {
                if ("特价购".equals(config.getTitle()) || "1.9元包邮".equals(config.getTitle())) {
                    it.remove();
                }
            } else if (showFlag2 == 2) {
                if ("小视频".equals(config.getTitle()) || "1.9元包邮".equals(config.getTitle())) {
                    it.remove();
                }
            } else if (showFlag2 == 3) {
                if ("小视频".equals(config.getTitle()) || "特价购".equals(config.getTitle())) {
                    it.remove();
                }
            }
        }
        if (!exist1) {
            Iterator<ClMenuConfig> itMenu = data.iterator();
            while(itMenu.hasNext()){
                ClMenuConfig config = itMenu.next();
                if ("小说".equals(config.getTitle()) || "直播".equals(config.getTitle())) {
                    itMenu.remove();
                }
            }
        }
        if (!exist2) {
            Iterator<ClMenuConfig> itMenu = data.iterator();
            while(itMenu.hasNext()){
                ClMenuConfig config = itMenu.next();
                if ("特价购".equals(config.getTitle()) || "1.9元包邮".equals(config.getTitle())) {
                    itMenu.remove();
                }
            }
        }
    }

    private int getShowFlag1(String abExp) {
        if (StringUtils.isEmpty(abExp)) {
            abExp = "243-514,569-1333";
        }
        //对照组:展示专辑tab
        String expStr1 = "243-514";
        //实验组1:展示小说tab
        String expStr2 = "243-515";
        //实验组2:展示直播tab
        String expStr3 = "243-516";
        if ("dev".equals(Global.getValue("app_environment"))) {
            expStr1 = "569-1333";
            expStr2 = "569-1334";
            expStr3 = "569-1335";
        }
        int showFlag = 1;
        if (abExp.contains(expStr1)) {
            showFlag = 1;
        } else if (abExp.contains(expStr2)) {
            showFlag = 2;
        } else if (abExp.contains(expStr3)) {
            showFlag = 3;
        }
        return showFlag;
    }

    private int getShowFlag2(String abExp) {
        if (StringUtils.isEmpty(abExp)) {
            abExp = "244-517,568-1330";
        }
        //对照组:展示小视频tab
        String expStr1 = "244-517";
        //实验组1:展示特价购tab
        String expStr2 = "244-518";
        //实验组2:展示1.9元包邮tab
        String expStr3 = "244-519";
        if ("dev".equals(Global.getValue("app_environment"))) {
            expStr1 = "568-1330";
            expStr2 = "568-1331";
            expStr3 = "568-1332";
        }
        int showFlag = 1;
        if (abExp.contains(expStr1)) {
            showFlag = 1;
        } else if (abExp.contains(expStr2)) {
            showFlag = 2;
        } else if (abExp.contains(expStr3)) {
            showFlag = 3;
        }
        return showFlag;
    }
}
