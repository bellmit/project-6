package com.miguan.report.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import static com.miguan.report.common.util.AppNameUtil.getAppIdForName;

/**广告位数据对比 扩展VO 加入了环比属性
 * @author zhongli
 * @date 2020-06-18 
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdStaExtVo implements Serializable {
    public AdStaExtVo(String adSpace, String appName, int clientId, String mom, double dataValue) {
        this.adSpace = adSpace;
        this.appName = appName;
        this.appId = getAppIdForName(appName);
        this.clientId = clientId;
        this.mom = mom;
        this.dataValue = dataValue;
    }

    /**
     * 广告位
     */
    private String adSpace;
    /**
     * 应用名称
     */
    private String appName;

    private int appId;
    private int clientId;

    /**
     * 环比
     */
    private String mom;
    /**
     * 值
     */
    private double dataValue;
}
