package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("用户埋点新表每次点击广告和分类")
public class LdBuryingPointEveryVo{

    @ApiModelProperty("事件id")
    private String actionId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("app版本")
    private String appVersion;

    @ApiModelProperty("APP类型 xld炫来电  wld微来电")
    private String appType;

    @ApiModelProperty("是否是新用户")
    private Boolean userState;

    @ApiModelProperty("渠道id")
    private String channelId;

    @ApiModelProperty("参数id")
    private String parameterId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建日期")
    private Date createDate;

    @ApiModelProperty("标记是广告（Advertisement）还是分类（classification）")
    private String type;

    @ApiModelProperty("广告id")
    private String xId;

    @ApiModelProperty("广告展示是否成功  0：成功  1：失败")
    private String state;
}