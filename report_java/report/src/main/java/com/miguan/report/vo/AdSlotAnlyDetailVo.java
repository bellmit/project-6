package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**广告位分析统计点击详情 VO
 * @author zhongli
 * @date 2020-06-18 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdSlotAnlyDetailVo {

    /**
     * 应用名称
     */
    private String appName;
    /**
     * 广告位置
     */
    private String adSpace;
    /**
     * 应用设备类型1=安卓 2=苹果
     */
    private int appDevType;
    private String date;

    private double cpm;
    private double preShowNum;
    private double activeValue;
    /**
     * 用户行为
     */
    private double dataValue;
}
