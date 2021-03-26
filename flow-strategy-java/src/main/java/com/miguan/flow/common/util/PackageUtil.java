package com.miguan.flow.common.util;

import com.miguan.flow.common.constant.FlowConstant;
import org.apache.commons.lang3.StringUtils;

public class PackageUtil {

    public static String getAppPackage(String appPackage, String mobileType) {
        if (StringUtils.isBlank(appPackage)) {
            if (FlowConstant.IOS.equals(mobileType)) {
                appPackage = FlowConstant.IOSPACKAGE;
            } else {
                appPackage =  FlowConstant.ANDROIDPACKAGE;
            }
        }
        return appPackage;
    }
}
