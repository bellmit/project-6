package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("轩辕广告位")
@Data
public class XyAdPositionSimpleVo {
    @ApiModelProperty("广告位id")
    private Long id;
    @ApiModelProperty("广告位名称")
    private String positionName;
    @ApiModelProperty("广告样式")
    private String adType;

}
