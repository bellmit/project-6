package com.miguan.bigdata.common.util;

import com.miguan.bigdata.entity.SysConfig;
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
    public static void initSysConfig(List<SysConfig> sysConfigs) {
        Map<String, Object> configs = new HashMap<String, Object>();
        for (SysConfig sysConfig : sysConfigs) {
            if (null != sysConfig && StringUtil.isNotBlank(sysConfig.getCode())) {
                configs.put(sysConfig.getCode(), sysConfig.getValue());
            }
        }
        Global.configMap = new HashMap<String, Object>();
        Global.putConfigMapAll(configs);
    }
}