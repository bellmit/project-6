package com.miguan.bigdata.vo;

import lombok.Data;

/**
 * @Description clickhouse中rp_ad_action_day对应的vo
 * @Author zhangbinglin
 * @Date 2020/11/23 13:37
 **/
@Data
public class DspPlanVo {

    //日期，格式：yyyy-MM-dd
    private String date;
    //包名
    private String packageName;
    //计划id
    private Long planId;
    //创意id
    private Long designId;
    //广告位id
    private String adId;
    //广告主名称
    private String advertiser;
    //库存(流量)
    private Integer inventory;
    //请求量
    private Integer request;
    //返回量
    private Integer response;
    //渲染数
    private Integer render;
    //展示数
    private Integer show;
    //点击数
    private Integer click;
    //点击用户数(uv)
    private Integer clickUser;
    //有效展示数
    private Integer exposure;
    //有效点击数
    private Integer validClick;
    //实际消费
    private Double actualConsumption;
}
