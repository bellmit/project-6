package com.miguan.report.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("代码位分析数据实体")
@Data
public class CodeSpaceVo {

    @ApiModelProperty("数据日期")
    private String date;
    @ApiModelProperty("应用名称")
    private String cut_app_name;
    @ApiModelProperty("广告位名称")
    private String total_name;

    private List<CodeSpaceDataVo> code;
}
