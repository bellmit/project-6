package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel
public class RealTimeCheckereDto {
    @ApiModelProperty("新增用户")
    private double newUser;
    @ApiModelProperty("活跃用户")
    private double user;
    @ApiModelProperty("播放用户")
    private double playUser;
    @ApiModelProperty("有效播放用户")
    private double validPlayUser;
    @ApiModelProperty("广告展现用户")
    private double adShowUser;
    @ApiModelProperty("广告点击用户")
    private double adClickUser;
    @ApiModelProperty("视频播放数")
    private double playCount;
    @ApiModelProperty("有效播放数")
    private double validPlayCount;
    @ApiModelProperty("广告展现")
    private double adShowCount;
    @ApiModelProperty("人均有效播放数")
    private double vperPlayCount;
    @ApiModelProperty("完播用户")
    private double endPlayUser;
    @ApiModelProperty("人均播放时长")
    private double perPlayTime;
    @ApiModelProperty("广告点击量")
    private double adClickCount;
    @ApiModelProperty("广告点击率")
    private double adClickRate;
    @ApiModelProperty("人均广告展示")
    private double perAdShowCount;
    @ApiModelProperty("人均广告点击")
    private double perAdclickCount;
    @ApiModelProperty("每曝光播放时长")
    private double preShowTime;
    @ApiModelProperty("每播放播放时长")
    private double prePlayTime;

    @ApiModelProperty("环比新增用户")
    private double momnewUser;
    @ApiModelProperty("环比活跃用户")
    private double momuser;
    @ApiModelProperty("环比播放用户")
    private double momplayUser;
    @ApiModelProperty("环比有效播放用户")
    private double momvalidPlayUser;
    @ApiModelProperty("环比广告展现用户")
    private double momadShowUser;
    @ApiModelProperty("环比广告点击用户")
    private double momadClickUser;
    @ApiModelProperty("环比视频播放数")
    private double momplayCount;
    @ApiModelProperty("环比有效播放数")
    private double momvalidPlayCount;
    @ApiModelProperty("环比广告展现")
    private double momadShowCount;
    @ApiModelProperty("环比完播用户")
    private double momendPlayUser;
    @ApiModelProperty("环比人均有效播放数")
    private double momvperPlayCount;
    @ApiModelProperty("环比人均播放时长")
    private double momperPlayTime;
    @ApiModelProperty("环比广告点击量")
    private double momadClickCount;
    @ApiModelProperty("环比广告点击率")
    private double momadClickRate;
    @ApiModelProperty("环比人均广告展示")
    private double momperAdShowCount;
    @ApiModelProperty("环比人均广告点击")
    private double momperAdclickCount;
    @ApiModelProperty("环比每曝光播放时长")
    private double monpreShowTime;
    @ApiModelProperty("环比每播放播放时长")
    private double monprePlayTime;
}
