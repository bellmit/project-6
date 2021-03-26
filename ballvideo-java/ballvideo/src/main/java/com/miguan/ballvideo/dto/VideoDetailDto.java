package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author shixh
 * @Date 2020/4/17
 **/
@Data
public class VideoDetailDto {
    @NotBlank(message = "'视频ID'不能为空")
    @ApiModelProperty("视频ID")
    private String id;
    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("分类ID")
    private String catId;
    @ApiModelProperty("广告位置类型")
    private String positionType;
    @ApiModelProperty("手机类型")
    private String mobileType;
    @ApiModelProperty("渠道ID")
    private String channelId;
    @ApiModelProperty("版本号")
    private String appVersion;
    @ApiModelProperty("是否开启储存、IMEI权限：0-未开启，1-开启(安卓参数)")
    private String permission;
    @ApiModelProperty("包名")
    private String appPackage;
    @ApiModelProperty("设备ID")
    private String deviceId;


    @ApiModelProperty("后端参数，前端不用传")
    private String marketChannelId;

}
