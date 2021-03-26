package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视频合集查询对象
 * @Author shixh
 **/
@Data
public class VideoGatherParamsDto{

    @ApiModelProperty("广告位置ID")
    String positionType;//广告位置类型,空则查询全部
    @ApiModelProperty("安卓是够开启储存、IMEI权限")
    String permission;//是否开启储存、IMEI权限：0-未开启，1-开启，安卓调用参数，IOS不传
    @ApiModelProperty("渠道ID,前端传什么就是什么")
    private String channelId;
    @ApiModelProperty("包名")
    private String appPackage;
    @ApiModelProperty("appVersion，空的话默认1.7.0")
    private String appVersion;
    @ApiModelProperty("手机类型：1-ios，2：安卓")
    private String mobileType;
    @ApiModelProperty("设备Id")
    private String deviceId;

    @ApiModelProperty("分页参数（返回列表才要传）")
    private int currentPage;
    @ApiModelProperty("分页参数（返回列表才要传）")
    private int pageSize;

}
