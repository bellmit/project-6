package com.miguan.report.common.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 平台
 * @Author zhangbinglin
 * @Date 2020/6/17 18:09
 **/
@Getter
public enum PlatFormEnum {

    chuang_shan_jia(1, "穿山甲"),
    guang_dian_tong(2, "广点通"),
    kuai_shou(3, "快手");

    private Integer id;
    private String name;

    PlatFormEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Map<String, Object>> getPlatFormList() {
        List<Map<String, Object>> list = new ArrayList<>();
        PlatFormEnum[] platFormEnums = PlatFormEnum.values();
        for(PlatFormEnum platForm : platFormEnums) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", platForm.id);
            map.put("name", platForm.name);
            list.add(map);
        }
        return list;
    }
}
