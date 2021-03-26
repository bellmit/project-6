package com.miguan.laidian.common.params;

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
    @ApiModelProperty("马甲包类型")
    private String appType;
    @ApiModelProperty("当前页面")
    private int currentPage;
    @ApiModelProperty("每页条数")
    private int pageSize;
    @ApiModelProperty("手机类型：1-ios，2-安卓")
    private String mobileType;
    @ApiModelProperty("设备id号")
    private String deviceId;
    @ApiModelProperty("app版本号")
    private String appVersion;
    @ApiModelProperty("渠道ID")
    private String channelId;
}
