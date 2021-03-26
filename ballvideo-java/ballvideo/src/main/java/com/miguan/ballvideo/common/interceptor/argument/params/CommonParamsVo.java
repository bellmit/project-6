package com.miguan.ballvideo.common.interceptor.argument.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * CommonParams
 * @Author shixh
 * @Date 2019/9/29
 **/
@Data
public class CommonParamsVo {

    @ApiModelProperty("用户ID")
    private String userId;
    @ApiModelProperty("token")
    private String token;
    @ApiModelProperty("渠道ID,前端传什么就是什么")
    private String channelId;
    @ApiModelProperty("父渠道ID,通过ChannelUtil.filter返回的渠道")
    private String parentChannelId;
    @ApiModelProperty("包名")
    private String appPackage;
    @ApiModelProperty("appVersion，空的话默认1.7.0")
    private String appVersion;
    private String appType;
    @ApiModelProperty("手机类型")
    private String mobileType;
    private String deviceId;
    @ApiModelProperty("实验分组Id")
    private String abTestId;

    /* 是否合并广告，默认为 1*/
    String isCombine = "1";

    private int currentPage;
    private int pageSize;
}
