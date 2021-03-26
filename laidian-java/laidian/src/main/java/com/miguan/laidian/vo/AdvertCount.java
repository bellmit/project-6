package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广告位次数bean
 *
 * @author xy.chen
 * @date 2019-08-02
 **/
@Data
@ApiModel("广告位次数")
public class AdvertCount {

    @ApiModelProperty("android来电列表广告位次数")
    private int callListCount;

    @ApiModelProperty("android来电详情广告位次数")
    private int callDetailsCount;

    @ApiModelProperty("android来电详情解锁广告位次数")
    private int callDetailsDeblockingCount;

    @ApiModelProperty("android来电详情退出广告位次数")
    private int callDetailsQuitCount;

    @ApiModelProperty("android来电详情退出广告位每天上限")
    private int callDetailsQuitLimit;

    @ApiModelProperty("iOS来电详情广告位次数")
    private int detailPageCount;

    @ApiModelProperty("android锁屏广告位次数")
    private int lockScreeneblockingCount;

    @ApiModelProperty("android小视频列表广告位次数")
    private int smallVideoListDeblockingCount;

    @ApiModelProperty("android小视频详情广告位次数")
    private int smallVideoDetailsDeblockingCount;

    @ApiModelProperty("android来电分类解锁广告的天数配置")
    private int laidianUnlockDays;

    @ApiModelProperty("android来电强制广告的天数配置")
    private int laidianForceDays;

    @ApiModelProperty("关闭弹窗广告位当日总次数")
    private int closePopupTodayTotalCount;
}
