package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("参数配置")
@Data
public class XyMapConfig extends BaseModel {
    @ApiModelProperty("参数名称")
    private String configName;
    @ApiModelProperty("参数key")
    private String configKey;
    @ApiModelProperty("参数value")
    private String configVal;
    @ApiModelProperty("排序编号")
    private Integer orderNum;
    @ApiModelProperty("状态,1启用，0禁用")
    private Integer status;
    @ApiModelProperty("是否删除，1删除，0正常")
    private Integer isDel;
    @ApiModelProperty("备注说明")
    private String note;
}
