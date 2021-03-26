package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SaveLabelInfo {

    @NotBlank(message = "'标签ID'不能为空")
    @ApiModelProperty("标签ID,逗号隔开")
    private String labelId;

    @NotBlank(message = "'设备ID'不能为空")
    @ApiModelProperty("设备ID")
    private String deviceId;
}
