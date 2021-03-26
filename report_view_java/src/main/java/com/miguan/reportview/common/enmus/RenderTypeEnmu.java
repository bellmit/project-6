package com.miguan.reportview.common.enmus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渲染方式
 */
public enum RenderTypeEnmu {
    /**
     *
     */
    template("template_render", "模板渲染"),
    self("self_render", "自渲染"),
    none("none", "无");

    String code;
    String value;

    RenderTypeEnmu(String code, String value) {
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
        for (RenderTypeEnmu typeEnum : RenderTypeEnmu.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("code", typeEnum.getCode());
            map.put("value", typeEnum.getValue());
            list.add(map);
        }
        return list;
    }

    public static String getValue(String code) {
        for (RenderTypeEnmu typeEnum : RenderTypeEnmu.values()) {
            if(typeEnum.getCode().equals(code)) {
                return typeEnum.getValue();
            }
        }
        return "";
    }
}