package com.miguan.xuanyuan.dto.request;

import com.miguan.xuanyuan.common.exception.ValidateException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("三方平台应用关联")
@Data
public class SourceAppRequest {

    @ApiModelProperty("广告源id")
    private Long id;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("平台id")
    private Long platId;

    @ApiModelProperty("三方广告平台应用id")
    private String sourceAppId;
}
