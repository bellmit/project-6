package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
@Setter
@Getter
public class AdTotalVo {
    /**
     * 营收
     */
    private double revenue;

    /**
     * 日期
     */
    private String dd;
    private String appPackage;
    /**
     * 广告位置关键字
     */
    private String positionType;

    private Integer platForm;

}
