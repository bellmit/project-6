package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("选项配置表")
@Data
public class XyOptionItemConfig extends BaseModel {
    @ApiModelProperty("选项的configCode")
    private String configCode;
    @ApiModelProperty("参数key")
    private String itemKey;
    @ApiModelProperty("参数value")
    private String itemVal;
    @ApiModelProperty("排序编号")
    private Integer orderNum;
    @ApiModelProperty("是否删除，1删除，0正常")
    private Integer isDel;
}
