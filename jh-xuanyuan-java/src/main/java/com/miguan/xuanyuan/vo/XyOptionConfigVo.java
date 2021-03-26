package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("选项配置表")
@Data
public class XyOptionConfigVo {
    private Long id;
    @ApiModelProperty("中文名称")
    private String configName;
    @ApiModelProperty("英文标识")
    private String configCode;
    @ApiModelProperty("排序编号")
    private Integer orderNum;
    @ApiModelProperty("状态,1启用，0禁用")
    private String status;
    @ApiModelProperty("备注说明")
    private String note;
}
