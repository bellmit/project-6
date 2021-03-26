package com.miguan.laidian.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AutoPushConfig {
    private Long id;
    private String appPackage;
    private String title; //标题
    private Integer titleType; //标题类型 1.文案库-签到推送文案 2.文案库-活跃用户推送文案 3.文案库-不活跃用户推送文案  n4.自定义标签
    private String content; //内容
    private Integer contentType; //内容类型 1素材标题(来电秀push库) 2自定义内容
    private Integer newActivityStartDays; //最近一次活跃，开始天数
    private Integer newActivityEndDays; //内容类型

    private Integer activityType; //活跃类型  1：新用户,2：活跃用户,3：不活跃用户。

    private Integer notActivityStartDays; //最近一次活跃，开始天数
    private Integer notActivityEndDays; //内容类型

    private Integer triggerType;  //触发类型  1：当天，2：明天
    private String triggerTime; //触发时间 HH:mm:ss

    /**
     * 事件类型。读取事件配置表：
     * 1001=未设置来电秀-未开通权限且未浏览
     * 1002=未设置来电秀-未开通权限但已浏览
     * 1003=未设置来电秀-已开通权限
     * 1004=未设置来电秀-已浏览来电秀
     * 1005=未设置来电秀-未浏览来电秀
     * 1006=未设置来电秀
     * 2001=已设置来电秀-未设置壁纸
     * 2002=已设置来电秀-未设置锁屏
     * 3001=未设置铃声-已浏览铃声
     * 3002=未设置铃声
     * 4001=连续6天签到用户
     * 4002=连续2天签到用户
     * 5001=上一自然日已签到，今日0：00-20:00未签到
     * 5002=上一自然日未签到
     * 5003=今日未签到
     * 6001=今日0：00-17:59新增
     * 6002=今日18：00-23:59新增
     * 6003=今日抽奖次数大于0
     * 6004=今日抽奖次数=0且任务未完成
     * 7001=上传的公用来电秀被点赞
     * 7002=上传的公用来电秀被使用
     * 8001=忽略事件类型
     */
    private Integer eventType;
    //落地页类型，1：启动APP ,2：链接,3：来电秀视频，4：小视频  5:来电秀视频（权限检查） 6:热门铃声分类列表页 7 活动详情
    private Integer jumpKey;
    private String jumpValue;
    private Integer status ; //启用状态。 1：启动
    private Date createdAt;
    private Date updatedAt;
}
