package com.miguan.ballvideo.vo.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("手机品牌")
public class PhoneBrandRes {
    @ApiModelProperty("手机品牌id")
    private Long id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("中文名称")
    private String zh_name;
}
