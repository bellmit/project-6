package com.miguan.reportview.common.enmus;

/**
 * @author zhongli
 * @date 2020-08-17 
 *
 */
public enum VideosSourceEnmu {
    /**
     *
     */
    M98("98du", "98°视频"),
    YIYOULIAO("yiyouliao", "易有料");
    String code;
    String value;

    VideosSourceEnmu(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }


    public static String getValueByCode(String code) {
        for (VideosSourceEnmu typeEnum : VideosSourceEnmu.values()) {
            if (typeEnum.code.equals(code)) {
                return typeEnum.getValue();
            }
        }
        return code;
    }
}