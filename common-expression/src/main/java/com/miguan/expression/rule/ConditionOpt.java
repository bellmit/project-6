package com.miguan.expression.rule;

/**
 * 条件的操作符常量
 * @author xujinbang
 * @date 2019-7-30
 */
public enum ConditionOpt {


    BIGGER(">"),
    BIGGER_EQUAL(">="),
    SMALL("<"),
    SMALL_EQUAL("<="),
    EQUAL("="),
    NOT_EQUAL("!="),
    INCLUDE("include"),
    NOT_INCLUDE("exclude");


    private String value;

    ConditionOpt(String opt) {
        this.value = opt;
    }


    public String getValue() {
        return this.value;
    }


    public static ConditionOpt getOpt(String opt) {
        if (opt != null) {
            ConditionOpt[] values = values();
            for (ConditionOpt each : values) {
                if (each.value.equals(opt)) {
                    return each;
                }
            }

        }
        return null;
    }

}
