package com.miguan.laidian.entity;

import com.miguan.laidian.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@Entity(name = "video_setting_phone")
@Data
@ApiModel("专属来电设置用户记录")
public class VideoSettingPhone extends BaseModel {

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("视频Id")
    private Long videoId;

    @ApiModelProperty("手机号")
    private String phone;
}
