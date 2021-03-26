package com.miguan.reportview.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Setter
@Getter
public class LdRealTimeStaVo {

    private String dd;  //日期，格式：YYYY-MM-DD
    private String dh;  //小时，格式：YYYYMMDDHH
    private String showMinute;   //分钟
    private Double showValue;  //单个统计使用的属性

    private Integer user;  //活跃用户数
    private Integer newUser;  //新增用户数
    private Integer detailPlayCount;  //详情页观看次数
    private Integer detailPlayUser;  //进入详情页播放用户数
    private Integer setCount;  //设置次数
    private Integer setUser;  //设置用户数
    private Integer setConfirmCount;  //成功设置次数
    private Integer setConfirmUser;  //成功设置用户数
    private Integer appStart;  //app启动次数
    private Integer adShow;  //广告展现量
    private Integer adClick;  //广告点击量
    private Integer adShowUser;  //广告展现用户
    private Integer adClickUser;  //广告点击用户

    private double prePlayCount;  //人均观看次数
    private double preSetCount;  //人均设置次数
    private double preSetConfirmCount;  //人均成功设置次数
    private double preAdShow;   //人均广告展现
    private double preAdClick;  //人均广告点击
    private double adClickRate;  //广告点击率
    private double adClickShowRate;  //广告点击用户占比
    private double preAppStart;  //人均APP启动次数
    private double userRetention;  //活跃用户留存率
    private double newUserRetention;  //新增用户留存率

}
