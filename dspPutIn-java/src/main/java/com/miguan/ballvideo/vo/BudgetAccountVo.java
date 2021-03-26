package com.miguan.ballvideo.vo;


import lombok.Data;

@Data
public class BudgetAccountVo {

    //计划id
    private Integer planId;

    //投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段
    private Integer timeSetting;

    //时间配置。指定时间段的给是为：10:45-11:80;多个时段的json格式：[{week_day:1,start_hour:0,end_hour:47}]
    private String timesConfig;

    //单价(元)
    private Double price;

    //计划日预算(元)
    private Double dayPrice;

    //计划剩余日预算(元)
    private Double remainDayPrice;

    //计划剩余总预算(元)
    private Double remainTotalPrice;

    //app类型，1-视频，2-来电
    private Integer type;
}
