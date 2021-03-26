package com.miguan.xuanyuan.vo.sdk;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdPositionVo {

    @ApiModelProperty("广告位KEY")
    private String positionKey;

    @ApiModelProperty("自定义规则1")
    private String customRule1;

    @ApiModelProperty("自定义规则2")
    private String customRule2;

    @ApiModelProperty("自定义规则3")
    private String customRule3;

    @ApiModelProperty("自定义规则4")
    private String customRule4;

    @ApiModelProperty("自定义规则5")
    private String customRule5;

    @ApiModelProperty("自定义规则6")
    private String customRule6;

    public AdPositionVo() {
    }

    public AdPositionVo(String positionKey) {
        this.positionKey = positionKey;
    }
}
