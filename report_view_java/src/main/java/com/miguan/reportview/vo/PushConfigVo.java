package com.miguan.reportview.vo;

import lombok.Data;

@Data
public class PushConfigVo {
    /**
     * 包名
     */
    private String packageName;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 活跃度配置天数字段，例：1_30
     */
    private String activityDays;

    /**
     * 触发类型  1：当天，2：明天,3:每小时30分检查，立即推送
     */
    private Integer triggerType;

    /**
     * 触发时间
     */
    private String triggerTime;
}
