package com.miguan.ballvideo.common.enums;

import java.util.*;

/**
 * 切片广告类型枚举
 */
public enum SectionAdType {
    //2、10、21广点通3-9、13-19穿山甲
    GDT_AD("0", "guangdiantong","gdtRate","2,10,21"),//广点通类型
    CSJ_AD("1", "chuanshanjia", "csjRate","3,4,5,6,7,8,9,13,14,15,16,17,18,19"),//穿山甲类型
    JSB_AD("2", "98adv","jsbRate","-1");//98度广告（暂时未使用，广告商先定义-1，到时再重新赋值）

    SectionAdType(String code, String groupKey, String rateKey, String type) {
        this.code = code;
        this.groupKey = groupKey;
        this.rateKey = rateKey;
        this.type = type;
    }

    private String code;//广告类型
    private String groupKey;//阶梯广告组key
    private String rateKey;//redis概率key
    private String type;//广告商

    public static String getGroupKeyByCode(String code) {
        for (SectionAdType sectionAdType : SectionAdType.values()) {
            if (sectionAdType.code.equals(code)) {
                return sectionAdType.groupKey;
            }
        }
        return "";
    }

    public static Map<String,String> getCodeAndRateKey() {
        Map<String,String> result = new HashMap<>();
        for (SectionAdType sectionAdType : SectionAdType.values()) {
            result.put(sectionAdType.code, sectionAdType.rateKey);
        }
        return result;
    }

    public static List<String> getTypeListByCode(String code) {
        List<String> result = new ArrayList<>();
        for (SectionAdType sectionAdType : SectionAdType.values()) {
            if (!sectionAdType.code.equals(code)) {
                String[] split = sectionAdType.type.split(",");
                List<String> list = Arrays.asList(split);
                result.addAll(list);
            }
        }
        return result;
    }

}
