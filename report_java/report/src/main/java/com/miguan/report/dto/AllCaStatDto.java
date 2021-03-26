package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**概览统计概览首行数据（包括总数，环比，同比）DTO
 * @author zhongli
 * @date 2020-06-17
 *
 */
@Setter
@Getter
@AllArgsConstructor
@ApiModel("概览小格统计")
public class AllCaStatDto implements Serializable {
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("汇总")
    private String total;
    @ApiModelProperty("环比")
    private String mom;
    @ApiModelProperty("同比")
    private String yoy;
}
