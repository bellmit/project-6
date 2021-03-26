package com.miguan.report.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 广告商数据实体
 */
@Data
public class BannerDataDto {

    /**
     * 日期
     */
    private String date;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 应用类型
     */
    private Integer appType;
    /**
     * 客户端：1安卓 2ios
     */
    private Integer clientId;
    /**
     * 广告位
     */
    private String totalName;
    /**
     * 代码位/广告位
     */
    private String adSpace;
    /**
     * 代码位
     */
    private String adSpaceId;
    /**
     * 广告请求量
     */
    private Integer adRequest;
    /**
     * 广告返回量
     */
    private Integer adReturn;
    /**
     * 广告填充率
     */
    private BigDecimal adFilling;
    /**
     * 展现量
     */
    private Integer showNumber;
    /**
     * 点击数
     */
    private Integer clickNumber;
    /**
     * 点击率
     */
    private BigDecimal clickRate;
    /**
     * 收益
     */
    private BigDecimal profit;
    /**
     * CPM
     */
    private BigDecimal cpm;
    /**
     * 请求数
     */
    private Integer reqNum;
    /**
     * 报错数
     */
    private Integer errNum;
    /**
     * 报错率
     */
    private BigDecimal errRate;
    /**
     * 渠道类型：1-全部 2-仅限 3-排除
     */
    private Integer channelType;
    /**
     * 算法：1-概率补充；2-手动排序
     */
    private Integer computer;
    /**
     * app名 处理后的数据
     */
    private String cutAppName;
    /**
     * 阶梯价格
     */
    private String ladderPrice;
    /**
     * 排序值
     */
    private Integer optionValue;
    /**
     * 包名
     */
    private String appPackage;
}
