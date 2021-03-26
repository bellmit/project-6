package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-06-22 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CostStaVo {
    private String date;
    private int dataOfWeek;
    private double cost;
    /**
     * 默认值：0.0
     */
    private double momcost;
    private double yoycost;

}
