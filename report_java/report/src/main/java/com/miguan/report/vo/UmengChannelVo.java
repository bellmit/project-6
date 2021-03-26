package com.miguan.report.vo;

import lombok.Data;

/**
 * @Description 友盟渠道数据vo
 * @Author zhangbinglin
 * @Date 2020/10/21 16:08
 **/
@Data
public class UmengChannelVo {

    //日期：yyyy-MM-dd
    private String date;

    //包名
    private String packageName;

    //父渠道
    private String fatherChannel;

    //渠道名称
    private String channel;

    //启动数
    private Integer launch;

    //使用时长
    private Integer duration;

    //当前渠道总用户数在总用户数中的比例
    private Double totalUserRate;

    //活跃用户
    private Integer activeUser;

    //新增用户
    private Integer newUser;

    //总用户数
    private Integer totalUser;

    //app类型：1--视频，2--来电
    private Integer appType;

    //app表的id
    private Integer appId;

    //应用名称
    private String appName;

    //客户端：1安卓 2ios
    private Integer clientId;
}
