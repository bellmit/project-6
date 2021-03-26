package com.miguan.report.common.enums;

import com.miguan.report.common.constant.CommonConstant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户端
 */
@Getter
public enum ClientEnum {

    ANDROID(1, 2, CommonConstant.ANDROID),
    IOS(2, 1, CommonConstant.IOS);

    /**
     * 报表库，客户端标识
     */
    private Integer id;
    /**
     * 广告库，客户端标识
     */
    private Integer advId;
    private String name;

    ClientEnum(Integer id, Integer advId, String name) {
        this.id = id;
        this.advId = advId;
        this.name = name;
    }

    public static ClientEnum getById(Integer id) {
        if (id == 1) {
            return ANDROID;
        } else {
            return IOS;
        }
    }

    public static ClientEnum getByAdvId(Integer id) {
        if (id == 1) {
            return IOS;
        } else {
            return ANDROID;
        }
    }

    public static List<Map<String, Object>> getClientList() {
        List<Map<String, Object>> list = new ArrayList<>();
        ClientEnum[] clientEnums = ClientEnum.values();
        for (ClientEnum client : clientEnums) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", client.id);
            map.put("name", client.name);
            list.add(map);
        }
        return list;
    }
}
