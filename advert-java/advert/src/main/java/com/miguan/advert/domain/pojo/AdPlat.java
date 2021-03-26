package com.miguan.advert.domain.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("广告平台信息")
@Data
public class AdPlat {

    @ApiModelProperty("广告平台标识")
    private Long id;

    @ApiModelProperty("广告名称")
    private String adv_name;

    @ApiModelProperty("广告平台标识")
    private String plat_key;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建日期")
    private Date created_at;
    @ApiModelProperty("修改日期")
    private Date updated_at;
}