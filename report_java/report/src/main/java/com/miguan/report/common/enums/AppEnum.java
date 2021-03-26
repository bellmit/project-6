package com.miguan.report.common.enums;

import lombok.Getter;

import static com.miguan.report.common.constant.CommonConstant.*;

/**
 * 对应APP表的id、name
 */
@Getter
public enum AppEnum {


    GUO_GUO(1, "果果视频", APP_GG, VIDEO_APP_TYPE, VIDEO_APP_TYPE_NAME),
    XI_YOU_SPEED(6, "茜柚极速版", APP_XYS, VIDEO_APP_TYPE, VIDEO_APP_TYPE_NAME),
    XI_YOU(2, "茜柚视频", APP_XY, VIDEO_APP_TYPE, VIDEO_APP_TYPE_NAME),
    MI_TAO(3, "蜜桃视频", APP_MT, VIDEO_APP_TYPE, VIDEO_APP_TYPE_NAME),
    LAI_DIAN(4, "炫来电", APP_XLD, CALL_APP_TYPE, CALL_APP_TYPE_NAME),
    DOU_QU(5, "豆趣视频", APP_DQ, VIDEO_APP_TYPE, VIDEO_APP_TYPE_NAME),
    QI_LI(7,"飞快清理大师",APP_FKQL,FKQL_APP_TYPE,FKQL_APP_TYPE_NAME),
    WAN_NLI(8,"锦鲤万年历",APP_JLWNL,JLWNL_APP_TYPE,JLWNL_APP_TYPE_NAME),
    HAO_LU(9,"好鹿视频",APP_HL,VIDEO_APP_TYPE,VIDEO_APP_TYPE_NAME);

    private int id;
    private String appName;
    private String aliasName;
    private int appType;
    private String appTypeName;

    AppEnum(int id, String appName, String aliasName, int appType, String appTypeName) {
        this.id = id;
        this.appName = appName;
        this.aliasName = aliasName;
        this.appType = appType;
        this.appTypeName = appTypeName;
    }

    public static AppEnum getById(int id) {
        AppEnum[] appEnums = AppEnum.values();
        for (AppEnum a : appEnums) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }
}
