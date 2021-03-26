package com.miguan.reportview.common.enmus;

/**
 * @author zhongli
 * @date 2020-08-17 
 *
 */
public enum PlatEnmu {
    /**
     *
     */
    m98_adv(4, "98广告", "98_adv"),
    kuai_shou(3, "快手", "kuai_shou"),
    guang_dian_tong(2, "广点通","guang_dian_tong"),
    chuan_shan_jia(1, "穿山甲", "chuan_shan_jia");
    int code;
    String value;
    String biCode;

    PlatEnmu(int code, String value, String biCode) {
        this.code = code;
        this.value = value;
        this.biCode = biCode;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public String getBiCode() {
        return biCode;
    }

    public static String getValueByCode(int code) {
        for (PlatEnmu typeEnum : PlatEnmu.values()) {
            if (typeEnum.code == code) {
                return typeEnum.getValue();
            }
        }
        return null;
    }

    public static String getValueByBiCode(String biCode) {
        for (PlatEnmu typeEnum : PlatEnmu.values()) {
            if (typeEnum.biCode .equals(biCode) ) {
                return typeEnum.getValue();
            }
        }
        return biCode;
    }

    public static PlatEnmu getEnum(int code) {
        for (PlatEnmu typeEnum : PlatEnmu.values()) {
            if (typeEnum.code == code) {
                return typeEnum;
            }
        }
        return null;
    }
}