package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;


@Data
@ApiModel("西柚埋点表(新用户)")
@Entity(name="xy_burying_point_user")
public class UserBuryingPointUser {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("初始化渠道")
    private String channelId;

}
