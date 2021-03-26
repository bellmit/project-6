package com.miguan.report.common.enums;

import lombok.Getter;

/**
 * 对应APP表的id、name
 */
@Getter
public enum AdvChannelEnum {


    ALL(1, "全部渠道"),
    ONLY(2, "仅限渠道"),
    EXCULDED(3, "排除渠道");

    private int id;
    private String name;

    AdvChannelEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static AdvChannelEnum getById(int id) {
        AdvChannelEnum[] enums = AdvChannelEnum.values();
        for (AdvChannelEnum e : enums) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }
}
