package com.miguan.bigdata.common.enums;

/**
 * 推送用户类型
 */
public enum PushUserEnmu {
    /**
     *
     */
    newUserNoPlayOne(1, "新用户（激活当天）,20点前新增的用户，且（0-19：59）未产生播放行为"),
    newUserNoPlayTwo(2, "新用户（激活当天）,20点后新增的用户，且（20-23：59）未产生播放行为"),
    newUserPlay(3, "新用户（激活当天) 当日产生播放行为"),
    oldUserPlay(4, "老用户（激活次日以后） 当日产生播放行为"),
    oldUserNoPlay(5, "老用户（激活次日以后） 当日未产生播放行为"),
    oldUserNoActive(6, "不活跃用户（未启动天数>=30天）");

    int code;
    String value;

    PushUserEnmu(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

}