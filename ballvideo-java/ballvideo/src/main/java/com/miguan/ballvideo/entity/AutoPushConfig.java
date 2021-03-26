package com.miguan.ballvideo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AutoPushConfig {
    private Long id;
    private String appPackage; //马甲包
    private String title; //标题
    private Integer type; //素材类型
    private String content; //内容
    private Integer activityType; //活跃类型  1：新用户,2：活跃用户,3：不活跃用户。
    private String activityTypeDays; //活跃配置天数字段：1_30
    private Integer triggerType;  //触发类型  1：当天，2：明天
    private String triggerTime; //触发时间 HH:mm:ss
    private Integer triggerEvent; //触发事件 1：20:00前新增，0:00-19:59未播放视频用户；视频包含所有类型的视频播放 2：20:00后新增，23:59：59未播放视频用户。 3：当日产生播放行为。4：上一自然日发生播放视频行为。5：上一自然日未发生播放视频行为。6：不活跃天数内未播放。7：当天未签到用户。8：上一自然日有签到，当天0:00-20:00未签到；上一个自然日。9：提现过新人提现；新人提现次数达到1次即满足条件。10：未提现过新人提现
    private Integer fallType ; //落地页类型，1：首页,2：链接,3：小视频，4：短视频
    private Integer status ; //启用状态。 1：启动
    private Date createdAt;
    private Date updatedAt;
}
