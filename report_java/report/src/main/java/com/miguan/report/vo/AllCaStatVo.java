package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-06-23 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AllCaStatVo {
    private double revenue;
    private double active;
    private double cpm;
    private double pre_show_num;
    private double active_value;

    private double momrevenue;
    private double momactive;
    private double momcpm;
    private double mompre_show_num;
    private double momactive_value;

    private double yoyrevenue;
    private double yoyactive;
    private double yoycpm;
    private double yoypre_show_num;
    private double yoyactive_value;
}
