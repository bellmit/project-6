package com.miguan.report.dto;

import lombok.Data;

@Data
public class AdvertCodeJoinDto {
    /**
     * 包名
     */
    private String appPackage;
    /**
     * 手机类型：1-ios，2-安卓
     */
    private Integer mobileType;
    /**
     * 广告位名称
     */
    private String totalName;
    /**
     * 算法：1-概率补充；2-手动排序
     */
    private Integer computer;
    /**
     * 排序值
     */
    private Integer optionValue;
    /**
     * 代码位
     */
    private String adId;
    /**
     * 渠道类型：1-全部 2-仅限 3-排除
     */
    private Integer channelType;
    /**
     * 阶梯价格
     */
    private String ladderPrice;

    /**
     * AB实验组id
     */
    private Integer abTestId;


    /**
     * 排序值
     */
    private Integer sortValue;

}
