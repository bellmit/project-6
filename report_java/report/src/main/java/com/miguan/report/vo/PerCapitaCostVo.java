package com.miguan.report.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**人均成本Vo
 * @author zhongli
 * @date 2020-06-22 
 *
 */
@Setter
@Getter
@NoArgsConstructor
public class PerCapitaCostVo extends DisaChartVo {
    /**
     * 总成本
     */
    private double cost;
    /**
     * 新增用户数
     */
    private double newUsers;

    /**
     * 日活数
     */
    private double active;
}
