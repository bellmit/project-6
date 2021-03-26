package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**广告位数据对比 DTO
 * @author zhongli
 * @date 2020-06-17
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("广告位数据对比")
public class AdStaResDto implements Serializable {


    @ApiModelProperty("广告位名称集")
    private List<String> lableNames;
    @ApiModelProperty("值")
    private List<Map<String, Object>> data;
}
