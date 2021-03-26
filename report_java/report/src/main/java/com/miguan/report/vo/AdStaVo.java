package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**广告位数据对比 VO
 * @author zhongli
 * @date 2020-06-18 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdStaVo {
    /**
     * 设备名称: 1安卓 ， 2ios
     */
    private int deviceType;
    private String adSpace;
    private String appName;
    private double dataValue;
}
