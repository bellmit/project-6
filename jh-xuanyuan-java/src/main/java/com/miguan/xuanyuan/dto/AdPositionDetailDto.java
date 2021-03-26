package com.miguan.xuanyuan.dto;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("轩辕广告位表")
@Data
public class AdPositionDetailDto extends BaseModel {

    private Long positionId;

    private String packageName;

    @ApiModelProperty("媒体账号id")
    private Long userId;

    @ApiModelProperty("应用id")
    private Long appId;

    @ApiModelProperty("appKey")
    private String appkey;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("广告样式")
    private String adType;

    @ApiModelProperty("广告位名称")
    private String positionName;

}
