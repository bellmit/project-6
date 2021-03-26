package com.miguan.ballvideo.vo;

import com.miguan.ballvideo.entity.Apps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("查询参数VO")
@Data
public class AppsVo {

    private String _id;

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("设备映射id")
    private String distinctId;

    @ApiModelProperty("app包名")
    private String appPackage;

    @ApiModelProperty("app列表")
    private List<Apps> appsList;
}
