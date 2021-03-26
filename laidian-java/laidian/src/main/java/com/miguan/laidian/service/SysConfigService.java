package com.miguan.laidian.service;


import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.vo.SysConfigVo;

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
     *
     * @return
     * @throws Exception
     */
    List<SysConfigVo> findAll();

    List<SysConfigVo> selectByCode(Map<String, Object> map);

    void initSysConfig();

    /**
     * 更新分布式服务的每个服务器的缓存
     */
    void reloadAll();

    /**
     * 查询系统开关配置信息
     * @param appType
     * @return
     */
    Map<String,Object> findSysConfigInfo(String appType);

    /**
     * 上报版本更新
     * @param commonParams
     * @return
     */
    int reportSysVersionInfo(CommonParamsVo commonParams);

    /**
     * 获取新版本的信息
     * @param appPackage
     * @param appVersion
     * @param channelId
     * @return
     */
    Map<String, Object> getSysVersionInfo(String appPackage, String appVersion,String channelId);

    ResultMap projectCondition();
}
