package com.miguan.flow.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 版本号工具类
 **/
@Slf4j
public class VersionUtil {

    /**
     * 版本号为空处理
     * 1 生产环境存在appVersion="未知版本"问题，添加条件过滤；
     * @param appVersion
     * @return
     */
    public static String getVersion(String appVersion) {
        if (StringUtils.isBlank(appVersion) || "未知版本".equals(appVersion)) {
            return "3.2.23";
        } else {
            return appVersion;
        }
    }
}
