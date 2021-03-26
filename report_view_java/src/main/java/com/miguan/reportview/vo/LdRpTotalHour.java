package com.miguan.reportview.vo;

import lombok.Data;

/**
 * @Description 来电ld_rp_total_hour
 * @Author zhangbinglin
 * @Date 2020/8/27 19:02
 **/
@Data
public class LdRpTotalHour {

    private String dd;    //日期，格式：YYYY-MM-DD
    private Integer dh;    //小时，格式：YYYYMMDDHH
    private Integer user;    //活跃用户数
    private Integer newUser;    //新增用户数
    private Integer detailPlayUser;    //详情页观看次数
    private Integer detailPlayCount;    //进入详情页播放用户数
    private Integer setCount;    //设置数
    private Integer setUser;    //设置用户数
    private Integer setConfirmCount;    //成功设置数
    private Integer setConfirmUser;    //成功设置用户数
    private Integer appStart;    //app启动次数
    private Integer adShow;    //广告展现量
    private Integer adClick;    //广告点击量
    private Integer adShowUser;    //广告展现用户
    private Integer adClickUser;    //广告点击用户
}
