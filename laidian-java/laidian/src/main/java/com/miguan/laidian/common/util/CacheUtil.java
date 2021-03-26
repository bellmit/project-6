package com.miguan.laidian.common.util;

import com.miguan.laidian.vo.SysConfigVo;
import lombok.extern.slf4j.Slf4j;
import tool.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存帮助类
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Slf4j
public class CacheUtil {

    /**
     * 初始化系统参数配置
     */
    public static void initSysConfig(List<SysConfigVo> sysConfigs) {
        Map<String, Object> configMap = new HashMap<String, Object>();
        for (SysConfigVo sysConfig : sysConfigs) {
            if (null != sysConfig && StringUtil.isNotBlank(sysConfig.getCode())) {
                configMap.put(sysConfig.getCode()+"_"+sysConfig.getAppType(), sysConfig.getValue());
            }
        }
        Global.configMap = new HashMap<String, Object>();
        Global.configMap.putAll(configMap);
    }

}