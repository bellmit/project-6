package com.miguan.reportview.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-03 
 *
 */
@Setter
@Getter
public class AdSpaceVo {
    /**
     * 500*包名
     */
    private String appPackage;

    /**
     * 广告位名称
     */
    private String name;

    private String appName;

    /**
     * 广告位置
     */
    private String key;


}
