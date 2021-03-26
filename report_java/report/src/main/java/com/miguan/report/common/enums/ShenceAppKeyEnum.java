package com.miguan.report.common.enums;

import lombok.Getter;

import static com.miguan.report.common.constant.CommonConstant.CALL_APP_TYPE;
import static com.miguan.report.common.constant.CommonConstant.VIDEO_APP_TYPE;

/**
 * 神策App应用ID
 */
@Getter
public enum ShenceAppKeyEnum {

    XI_YOU_VIDEO_ANDROID("茜柚Android","com.mg.xyvideo", 2, VIDEO_APP_TYPE, 1),
    GUO_GUO_VIDEO_ANDROID("果果Android","com.mg.ggvideo", 1, VIDEO_APP_TYPE, 1),
    MI_TAO_VIDEO_ANDROID("蜜桃Android","com.mg.mtvideo", 3, VIDEO_APP_TYPE, 1),
    XUAN_LAI_DIAN_ANDROID("炫来电Android","com.mg.phonecall", 4, CALL_APP_TYPE, 1),
    XI_YOU_VIDEO_IOS("茜柚ios","com.xm98.grapefruit", 2, VIDEO_APP_TYPE, 2),
    GUO_GUO_VIDEO_IOS("果果ios","com.mg.westVideo", 1, VIDEO_APP_TYPE, 2);

    private String name;
    private String code;
    private Integer id;  //对应app表的id
    private Integer appType;  //app类型：1--视频，2--来电
    private Integer clientId;  //客户端类型, 1:安卓，2：ios

    ShenceAppKeyEnum(String name,  String code, Integer id, Integer appType, Integer clientId){
        this.name = name;
        this.code = code;
        this.id = id;
        this.appType = appType;
        this.clientId = clientId;
    }
}
