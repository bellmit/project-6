package com.miguan.xuanyuan.common.enums;

import lombok.Data;

/**
 * 第三方平台枚举
 */
public enum ThirdPlatEnum {

    CHUANG_SHAN_JIA("chuan_shan_jia", "穿山甲"),
    GUANG_DIAN_TONG("guang_dian_tong", "广点通"),
    KUAI_SHOU("kuai_shou", "快手");
    String code;
    String name;

    ThirdPlatEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
