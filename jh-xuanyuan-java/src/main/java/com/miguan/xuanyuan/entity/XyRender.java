package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("渲染方式")
@Data
public class XyRender extends BaseModel {
    @ApiModelProperty("应用名称")
    private String platName;
    @ApiModelProperty("appkey")
    private String platKey;
    @ApiModelProperty("app密钥")
    private String rName;
    @ApiModelProperty("应用分类key")
    private String rKey;
    @ApiModelProperty("状态，1启用，0禁用")
    private Integer status;
}
