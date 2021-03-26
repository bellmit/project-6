package com.miguan.report.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miguan.report.common.constant.CommonConstant.CALL_APP_TYPE;
import static com.miguan.report.common.constant.CommonConstant.VIDEO_APP_TYPE;

/**
 * @Description 带客户端的app枚举
 * @Author zhangbinglin
 * @Date 2020/6/22 12:06
 **/
@Getter
public enum AppClientEnum {

    XI_YOU_ANDROID("茜柚视频1", "茜柚Android", VIDEO_APP_TYPE),
    GUO_GUO_ANDROID("果果视频1", "果果Android", VIDEO_APP_TYPE),
    XI_YOU_IOS("茜柚视频2", "茜柚Ios", VIDEO_APP_TYPE),
    GUO_GUO_IOS("果果视频2", "果果Ios", VIDEO_APP_TYPE),
    MI_TAO_ANDROID("蜜桃视频1", "蜜桃Android", VIDEO_APP_TYPE),
    DOU_QU_ANDROID("豆趣视频1", "豆趣Android", VIDEO_APP_TYPE),
    XI_YOU_SPEED_ANDROID("茜柚极速版1", "茜柚极速Android", VIDEO_APP_TYPE),
    LAI_DIAN_ANDROID("炫来电1", "炫来电Android", CALL_APP_TYPE);

    /**
     * id格式= appName +"-" + 客户端id
     * appid：（1=果果视频，2=茜柚视频，3=蜜桃视频，4=炫来电）
     * 1安卓 ， 2ios
     */
    private String id;

    private String name;

    //app类型:1视频，2炫来电
    private int appType;

    AppClientEnum(String id, String name, int appType) {
        this.id = id;
        this.name = name;
        this.appType = appType;
    }

    public static List<Map<String, Object>> getAppClientList(Integer appType) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (appType == null) {
            return list;
        }
        AppClientEnum[] appClientEnums = AppClientEnum.values();
        for (AppClientEnum appClient : appClientEnums) {
            if (appType == appClient.appType) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", appClient.id);
                map.put("name", appClient.name);
                list.add(map);
            }
        }
        return list;
    }
}
