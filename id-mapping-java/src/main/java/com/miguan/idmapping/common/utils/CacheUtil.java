package com.miguan.idmapping.common.utils;

import com.miguan.idmapping.vo.SysConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
        Map<String, Object> configs = new HashMap<String, Object>();
        for (SysConfigVo sysConfig : sysConfigs) {
            if (null != sysConfig && StringUtils.isNotBlank(sysConfig.getCode())) {
                configs.put(sysConfig.getCode(), sysConfig.getValue());
            }
        }
        Global.configMap = new HashMap<>();
        Global.putConfigMapAll(configs);
    }
}