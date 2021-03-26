package com.miguan.ballvideo.dto;
/*
 * @Author: daixq
 * @Date: 2021/2/22 15:45
 * @Description:
 **/

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IssueInterestLabelDto {

    @NotBlank( message = "'标签ID'不可为空")
    @ApiModelProperty("标签ID")
    private String labelId;

    @NotBlank( message = "'标签名称'不可为空")
    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签排序")
    private Integer sort;
}
