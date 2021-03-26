package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReportTimeDivisionVo {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("曝光量")
    private String showNum;

    @ApiModelProperty("曝光用户数")
    private String showUser;

    @ApiModelProperty("点击量")
    private String clickNum;

    @ApiModelProperty("点击用户数")
    private String clickUser;

    @ApiModelProperty("点击率(百分比，已乘了100)")
    private String clickRate;
}
