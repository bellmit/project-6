package com.miguan.ballvideo.common.util;

import com.miguan.ballvideo.common.constants.Constant;
import org.apache.commons.lang3.StringUtils;

public class PackageUtil {

    public static String getAppPackage(String appPackage, String mobileType) {
        if (StringUtils.isBlank(appPackage)) {
            if (Constant.IOS_MOBILE.equals(mobileType)) {
                appPackage = Constant.IOSPACKAGE;
            } else {
                appPackage =  Constant.ANDROIDPACKAGE;
            }
        }
        return appPackage;
    }
}
