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
@NoArgsConstructor
@AllArgsConstructor
public class DisaChartVo {
    private String name;
    /**
     * 设备名称: 1安卓 ， 2ios
     */
    private int deviceType = 1;
    private String date;
    private double dataValue;
}
