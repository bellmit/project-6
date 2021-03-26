package com.miguan.report.common.enums;

import lombok.Getter;

import static com.miguan.report.common.constant.CommonConstant.CALL_APP_TYPE;
import static com.miguan.report.common.constant.CommonConstant.VIDEO_APP_TYPE;

@Getter
public enum AppUseTimeEnum {

    VIDEO_SUMMARY("汇总", VIDEO_APP_TYPE, 1),
    XI_YOU_VIDEO_ANDROID("茜柚Android", VIDEO_APP_TYPE, 2),
    GUO_GUO_VIDEO_ANDROID("果果Android", VIDEO_APP_TYPE, 3),
    //XI_YOU_VIDEO_IOS("茜柚Ios", VIDEO_APP_TYPE, 5),
    GUO_GUO_VIDEO_IOS("果果Ios", VIDEO_APP_TYPE, 6),
    DOU_QU_VIDEO_ANDROID("豆趣Android", VIDEO_APP_TYPE, 7),


    LAI_DIAN_SUMMARY("汇总", CALL_APP_TYPE, 1),
    XUAN_LAI_DIAN_ANDROID("炫来电Android", CALL_APP_TYPE, 4);

    private String name;
    private Integer appType;
    private Integer dataType;

    AppUseTimeEnum(String name, Integer appType, Integer dataType) {
        this.name = name;
        this.appType = appType;
        this.dataType = dataType;
    }

    public static String getNameByAppTypeAndeDataType(int appType, int dataType) {
        AppUseTimeEnum[] appUseTimeEnums = AppUseTimeEnum.values();
        for (AppUseTimeEnum appUseTimeEnum : appUseTimeEnums) {
            if(appUseTimeEnum.getAppType().intValue() == appType && appUseTimeEnum.getDataType().intValue() == dataType){
                return appUseTimeEnum.getName();
            }
        }
        return null;
    }

}
