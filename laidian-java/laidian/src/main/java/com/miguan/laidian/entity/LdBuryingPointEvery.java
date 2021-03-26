package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "ld_burying_point_every")
@Data
@ApiModel("用户埋点新表每次点击广告和分类")
public class LdBuryingPointEvery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("事件id")
    @Column(name = "action_id")
    private String actionId;

    @ApiModelProperty("用户id")
    @Column(name = "user_id")
    private String userId;

    @ApiModelProperty("设备id")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty("app版本")
    @Column(name = "app_version")
    private String appVersion;

    @ApiModelProperty("APP类型 xld炫来电  wld微来电")
    @Column(name = "app_type")
    private String appType;

    @ApiModelProperty("是否是新用户")
    @Column(name = "user_state")
    private Boolean userState;

    @ApiModelProperty("渠道id")
    @Column(name = "channel_id")
    private String channelId;

    @ApiModelProperty("参数id")
    @Column(name = "parameter_id")
    private String parameterId;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @ApiModelProperty("创建日期")
    @Column(name = "create_date")
    private Date createDate;

    @ApiModelProperty("标记是广告（Advertisement）还是分类（classification）")
    @Column(name = "type")
    private String type;

    @ApiModelProperty("广告id")
    @Column(name = "x_id")
    private String xId;

    @ApiModelProperty("广告展示是否成功  0：成功  1：失败")
    @Column(name = "state")
    private String state;
}