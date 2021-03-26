package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("三方广告平台应用关联表")
@Data
public class XySourceApp extends BaseModel {

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("平台id")
    private Long platId;

    @ApiModelProperty("广告平台应用id")
    private String sourceAppId;
}
