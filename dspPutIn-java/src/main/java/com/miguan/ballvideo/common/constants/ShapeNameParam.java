package com.miguan.ballvideo.common.constants;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShapeNameParam{
    @ApiModelProperty("值")
    private Integer key;
    @ApiModelProperty("名称")
    private String value;

    public ShapeNameParam(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
}
