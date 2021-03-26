package com.miguan.bigdata.vo;

import lombok.Data;

@Data
public class PushLdConfigVo {
    /**
     * 包名,多个的时候逗号分隔
     */
    private String packageName;

    /**
     *  用户类型
     */
    private Integer userType;

    /**
     * 活跃天数 不活跃天数,逗号分隔
     */
    private String activityDays;

    /**
     * 触发类型  1：当天，2：次日，3：每小时检查。4：事件触发立即推送 单选
     */
    private Integer triggerType;

    /**
     * 触发时间 HH:mm:ss，逗号隔开
     */
    private String triggerTime;
}
