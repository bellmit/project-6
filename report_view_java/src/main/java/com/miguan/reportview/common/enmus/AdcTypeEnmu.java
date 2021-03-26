package com.miguan.reportview.common.enmus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 素材
 */
public enum AdcTypeEnmu {
    /**
     *
     */
    image("图片", "图片"),
    video("视频", "视频");
    String code;
    String value;

    AdcTypeEnmu(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static List<Map<String, String>> getList() {
        List<Map<String, String>> list = new ArrayList<>();
        for (AdcTypeEnmu typeEnum : AdcTypeEnmu.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("code", typeEnum.getCode());
            map.put("value", typeEnum.getValue());
            list.add(map);
        }
        return list;
    }

    public static String getValue(String code) {
        for (AdcTypeEnmu typeEnum : AdcTypeEnmu.values()) {
            if(typeEnum.getCode().equals(code)) {
                return typeEnum.getValue();
            }
        }
        return "";
    }

}