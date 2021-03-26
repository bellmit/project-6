package com.miguan.ballvideo.vo.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("手机表")
public class AdvertPhoneVo {
    @ApiModelProperty("手机表id")
    private Long id;
    @ApiModelProperty("类型,1:不限品牌，2：指定品牌")
    private Integer type;
    @ApiModelProperty("计划ID")
    private Long plan_id;
    @ApiModelProperty("手机品牌，多个‘,’隔开")
    private String brand;
    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;
}
