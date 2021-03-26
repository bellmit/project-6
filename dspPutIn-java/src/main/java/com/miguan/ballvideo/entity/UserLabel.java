package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel("用户标签表")
@Entity(name="User_Label")
@Data
public class UserLabel extends BaseModel {

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("第一标签（分类ID1）")
    private Long catId1;

    @ApiModelProperty("第二标签（分类ID2）")
    private Long catId2;

    @ApiModelProperty("分类权重集合,1.8新增")
    private String catIdsSort;
}
