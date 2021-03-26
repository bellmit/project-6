package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
public class BuryingPointPushVO {

    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("app版本")
    private String appVersion;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("启动事件标识")
    private String actionId;

    @ApiModelProperty("推送id")
    private String pushId;

    @ApiModelProperty("厂商通道")
    private String pushChannel;

    @ApiModelProperty("推送方式")
    private String pushType;

    @ApiModelProperty("推送标题")
    private String pushTitle;

    @ApiModelProperty("推送内容")
    private String pushContent;

    @ApiModelProperty("推送视频id")
    private String pushVideoId;

    @ApiModelProperty("推送视频标题")
    private String pushVideoTitle;

}