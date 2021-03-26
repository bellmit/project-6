package com.miguan.report.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.miguan.report.common.constant.CommonConstant.CALL_APP_TYPE;
import static com.miguan.report.common.constant.CommonConstant.VIDEO_APP_TYPE;

/**
 * @Description 活跃用户报表tab页选择
 * @Author zhangbinglin
 * @Date 2020/6/22 12:06
 **/
@Getter
public enum AppClientTabEnum {

    XI_YOU_ANDROID("2-1", "茜柚Android", VIDEO_APP_TYPE),
    GUO_GUO_ANDROID("1-1", "果果Android", VIDEO_APP_TYPE),
    GUO_GUO_IOS("1-2", "果果Ios", VIDEO_APP_TYPE),
    LAI_DIAN_ANDROID("4-1", "炫来电Android", CALL_APP_TYPE),
    DOU_QU_ANDROID("5-1", "豆趣Android", CALL_APP_TYPE),
    XI_YOU_SPEED_ANDROID("6-1", "茜柚极速版Android", CALL_APP_TYPE);

    /**
     * id格式= appid +"-" + 客户端id
     * appid：（1=果果视频，2=茜柚视频，3=蜜桃视频，4=炫来电）
     * 1安卓 ， 2ios
     */
    private String id;

    private String name;

    //app类型:1视频，2炫来电
    private Integer appType;

    AppClientTabEnum(String id, String name, Integer appType) {
        this.id = id;
        this.name = name;
        this.appType = appType;
    }

    public static List<Map<String, Object>> getAppClientTabList(Integer appType) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (appType == null) {
            return list;
        }
        AppClientTabEnum[] appClientEnums = AppClientTabEnum.values();
        for (AppClientTabEnum appClient : appClientEnums) {
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
