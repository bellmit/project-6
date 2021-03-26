package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**广告位分析统计 扩展VO 加入环比属性
 * @author zhongli
 * @date 2020-06-18 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdSlotAnlyExtVo {

    private String adSpace;
    private String appName;

    private double revenue;
    private double activeValue;
    private double cpm;
    private double preShowNum;

    private double momRevenue;
    private double momActiveValue;
    private double momCpm;
    private double momPreShowNum;
}
