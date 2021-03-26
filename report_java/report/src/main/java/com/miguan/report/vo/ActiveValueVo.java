package com.miguan.report.vo;

import com.miguan.report.common.util.AppNameUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**日活均价值统计 实体
 * @author zhongli
 * @date 2020-06-20 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveValueVo {

    /**
     * 应用名称/平台名称
     */
    private String name;
    /**
     * 设备名称: 1安卓 ， 2ios
     */
    private int deviceType;
    /**
     * 平台类型：1穿山甲 2广点通 3快手
     */
    private int platType;
    private double dataValue;
    /**
     * 汇总使用，分子
     */
    private double sumMol;
    /**
     * 汇总使用，分母，一般是日活量
     */
    private double sumDem;
    private String dates;
    private String minDate;
    private String maxDate;

    public ActiveValueVo convertDeviceType2name() {
        this.setName(AppNameUtil.convertDeviceType2name(this.getName(), this.getDeviceType()));
        return this;
    }

    public ActiveValueVo convertPlatType2name() {
        this.setName(AppNameUtil.convertPlatType2name(this.getName(), this.getDeviceType(), this.getPlatType()));
        return this;
    }
}
