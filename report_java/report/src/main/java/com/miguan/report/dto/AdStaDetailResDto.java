package com.miguan.report.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**广告位数据对比 DTO
 * @author zhongli
 * @date 2020-06-17
 *
 */
@Setter
@Getter
@NoArgsConstructor
@ApiModel("广告位数据对比")
public class AdStaDetailResDto extends DisassemblyChartDto {
    public AdStaDetailResDto(String date, String type, double value) {
        super(date, type, value);
    }
}
