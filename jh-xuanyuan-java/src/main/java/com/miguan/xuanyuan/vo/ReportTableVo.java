package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description 报表表格vo
 * @Date 2020/11/24 13:54
 **/
@Data
public class ReportTableVo {

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("曝光量")
    private Integer showNum;

    @ApiModelProperty("曝光用户数")
    private Integer showUser;

    @ApiModelProperty("点击量")
    private Integer clickNum;

    @ApiModelProperty("点击用户数")
    private Integer clickUser;

    @ApiModelProperty("点击率(百分比，已乘了100)")
    private Double clickRate;

    @ApiModelProperty("分时数据")
    private List<ReportTimeDivisionVo> timeDivisions;
}
