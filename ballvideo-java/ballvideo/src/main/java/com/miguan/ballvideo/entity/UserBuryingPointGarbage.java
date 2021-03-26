package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 用户进入垃圾清理埋点
 */
@Entity(name="xy_burying_point_garbage")
@Data
@ApiModel("用户进入垃圾清理埋点")
public class UserBuryingPointGarbage extends BaseModel {

    @ApiModelProperty("设备表id")
    @Column(name = "device_id")
    private Long deviceId;

    @ApiModelProperty("进入次数")
    @Column(name = "entry_num")
    private Integer entryNum;
}

