package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**概览小方格详情 实体
 * @author zhongli
 * @date 2020-06-17 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CaStatNumVoDetail {
    private String appName;
    private int deviceType;
    private String date;
    private double dataValue;

    /**
     * 汇总使用，分子 注：查人均展示和日活均值使用
     */
    private double sumMol;
    /**
     * 汇总使用，分母，一般是日活量 注：查人均展示和日活均值使用
     */
    private double sumDem;
}
