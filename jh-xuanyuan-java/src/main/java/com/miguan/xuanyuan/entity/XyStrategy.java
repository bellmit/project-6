package com.miguan.xuanyuan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@ApiModel("广告位策略")
@Data
public class XyStrategy extends BaseModel {

    @ApiModelProperty("策略分组id")
    private Long strategyGroupId;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("ab实验分组id")
    private Long abItemId;

    @ApiModelProperty("实验占比")
    private Integer abRate;

    @ApiModelProperty("排序策略")
    private Integer sortType;

    @ApiModelProperty("自定义字段，json格式")
    private String customField;

    @ApiModelProperty("状态，1启用，0禁用")
    private Integer status = 1;

}
