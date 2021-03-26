package com.miguan.reportview.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@ApiModel("图和表格数据")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChartAndTableDto<C,T> {
    private C chartData;
    private T tableData;
}
