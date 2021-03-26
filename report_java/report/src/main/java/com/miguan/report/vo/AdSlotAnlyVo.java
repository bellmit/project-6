package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**广告位分析统计 VO 按app_name,ad_space进行分组统计
 * @author zhongli
 * @date 2020-06-18 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdSlotAnlyVo {

    public AdSlotAnlyVo(int deviceType, String adSpace, String appName) {
        this.deviceType = deviceType;
        this.appName = appName;
        this.adSpace = adSpace;
        this.revenue = 0;
        this.activeValue = 0;
        this.cpm = 0;
        this.preShowNum = 0;
    }

    /**
     * 设备名称: 1安卓 ， 2ios
     */
    private int deviceType;
    private String adSpace;
    private String appName;
    private double revenue;
    private double activeValue;
    private double cpm;
    private double preShowNum;
}
