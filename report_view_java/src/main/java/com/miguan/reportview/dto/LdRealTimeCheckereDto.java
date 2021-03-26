package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class LdRealTimeCheckereDto {
    
    @ApiModelProperty("活跃用户数")
    private Integer user;

    @ApiModelProperty("新增用户数")
    private Integer newUser;

    @ApiModelProperty("详情页观看次数")
    private Integer detailPlayCount;

    @ApiModelProperty("进入详情页播放用户数")
    private Integer detailPlayUser;

    @ApiModelProperty("设置用户数")
    private Integer setUser;

    @ApiModelProperty("成功设置用户数")
    private Integer setConfirmUser;

    @ApiModelProperty("人均观看次数")
    private double prePlayCount;

    @ApiModelProperty("人均设置次数")
    private double preSetCount;

    @ApiModelProperty("人均成功设置次数")
    private double presetConfirmCount;

    @ApiModelProperty("app启动次数")
    private Integer appStart;

    @ApiModelProperty("广告展现量")
    private Integer adShow;

    @ApiModelProperty("广告点击量")
    private Integer adClick;

    @ApiModelProperty("广告展现用户")
    private Integer adShowUser;

    @ApiModelProperty("广告点击用户")
    private Integer adClickUser;

    @ApiModelProperty("人均广告展现")
    private double preAdShow;

    @ApiModelProperty("人均广告点击")
    private double preAdClick;

    @ApiModelProperty("广告点击率")
    private double adClickRate;

    @ApiModelProperty("广告点击用户占比")
    private double adClickShowRate;

    @ApiModelProperty("人均APP启动次数")
    private double preAppStart;




    @ApiModelProperty("环比活跃用户数")
    private double monUser;

    @ApiModelProperty("环比新增用户数")
    private double monNewUser;

    @ApiModelProperty("环比详情页观看次数")
    private double monDetailPlayUser;

    @ApiModelProperty("环比进入详情页播放用户数")
    private double monDetailPlayCount;

    @ApiModelProperty("环比设置用户数")
    private double monSetUser;

    @ApiModelProperty("环比成功设置用户数")
    private double monSetConfirmUser;

    @ApiModelProperty("环比人均观看次数")
    private double monPrePlayCount;

    @ApiModelProperty("环比人均设置次数")
    private double monPreSetCount;

    @ApiModelProperty("环比人均成功设置次数")
    private double monPresetConfirmCount;

    @ApiModelProperty("环比app启动次数")
    private double monAppStart;

    @ApiModelProperty("环比广告展现量")
    private double monAdShow;

    @ApiModelProperty("环比广告点击量")
    private double monAdClick;

    @ApiModelProperty("环比广告展现用户")
    private double monAdShowUser;

    @ApiModelProperty("环比广告点击用户")
    private double monAdClickUser;

    @ApiModelProperty("环比活跃用户留存率")
    private double monUserRetention;

    @ApiModelProperty("环比新增用户留存率")
    private double monNewUserRetention;

    @ApiModelProperty("环比人均广告展现")
    private double monPreAdShow;

    @ApiModelProperty("环比人均广告点击")
    private double monPreAdClick;

    @ApiModelProperty("环比广告点击率")
    private double monAdClickRate;

    @ApiModelProperty("环比广告点击用户占比")
    private double monAdClickShowRate;

    @ApiModelProperty("环比人均APP启动次数")
    private double monPreAppStart;

}
