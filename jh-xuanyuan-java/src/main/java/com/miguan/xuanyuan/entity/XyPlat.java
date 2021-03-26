package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("平台")
@Data
public class XyPlat extends BaseModel {
    @ApiModelProperty("平台标识")
    private String platKey;
    @ApiModelProperty("平台名称")
    private String platName;
    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel;

}
