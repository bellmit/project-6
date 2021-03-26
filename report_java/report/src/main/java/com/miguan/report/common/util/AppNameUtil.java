package com.miguan.report.common.util;

import com.miguan.report.common.enums.AppEnum;

import static com.miguan.report.common.constant.CommonConstant.ANDROID;
import static com.miguan.report.common.constant.CommonConstant.IOS;
import static com.miguan.report.common.constant.CommonConstant.LINE_SEQ;

/**应用名称工具类
 * @author zhongli
 * @date 2020-06-22 
 *
 */
public final class AppNameUtil {


    private static String convertDeviceType2AppName(String name, int deviceType) {
        switch (deviceType) {
            case 1: {
                return name.concat(ANDROID);
            }
            case 2: {
                return name.concat(IOS);
            }
            default:
        }
        return name;
    }


    /**
     * 对app名称缩短处理并和设备类型组装名称
     * @param name
     * @param deviceType
     * @return
     */
    public static String convertDeviceType2name(String name, int deviceType) {
        name = name.replaceFirst("视频", "");
        return convertDeviceType2AppName(name, deviceType);
    }

    /**
     * 通过appid和设备类型组装名称
     * @param appID
     * @param deviceType
     * @return
     */
    public static String convertDeviceType2name(int appID, int deviceType) {
        String name = getAppNameForId(appID);
        return convertDeviceType2AppName(name, deviceType);
    }


    /**
     * 通过appid 获取app别名
     * @param appID
     * @return
     */
    public static String getAppNameForId(int appID) {
        AppEnum appEnum = AppEnum.getById(appID);
        return appEnum == null ? "" : appEnum.getAliasName();
    }

    /**
     * 通过appid 获取app别名
     * @param appID
     * @return
     */
    public static String getAppFullNameForId(int appID) {
        AppEnum appEnum = AppEnum.getById(appID);
        return appEnum == null ? "" : appEnum.getAppName();
    }

    /**
     * 通过app名称 获取appid
     * @param name
     * @return
     */
    public static int getAppIdForName(String name) {
        for (AppEnum appEnum : AppEnum.values()) {
            if (name.startsWith(appEnum.getAliasName())) {
                return appEnum.getId();
            }
        }
        return -1;
    }


    public static String convertPlatType2name(String name, int deviceType, int platType) {
        switch (platType) {
            case 1: {
                return convertDeviceType2name(name, deviceType).concat(LINE_SEQ).concat("穿山甲");
            }
            case 2: {
                return convertDeviceType2name(name, deviceType).concat(LINE_SEQ).concat("广点通");
            }
            case 3: {
                return convertDeviceType2name(name, deviceType).concat(LINE_SEQ).concat("快手");
            }
            default:
        }
        return convertDeviceType2name(name, deviceType);
    }
}
