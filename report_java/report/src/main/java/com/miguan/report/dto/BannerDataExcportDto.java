package com.miguan.report.dto;

import lombok.Data;

/**
 * 广告商数据实体
 */
@Data
public class BannerDataExcportDto {

    /**
     * 日期
     */
    private String date;
    /**
     * app名 处理后的数据
     */
    private String cutAppName;
    /**
     * 广告位
     */
    private String totalName;
    /**
     * 代码位
     */
    private String adSpaceId;
    /**
     * 价格
     */
    private Double price;
    /**
     * 渠道类型：1-全部 2-仅限 3-排除
     */
    private String channelType;
    /**
     * 排序值
     */
    private Integer optionValue;
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
    private Double adFilling;
    /**
     * 展现量
     */
    private Integer showNumber;
    /**
     * 展现率
     */
    private Double showNumberRate;
    /**
     * 点击数
     */
    private Integer clickNumber;
    /**
     * 点击率
     */
    private Double clickRate;
    /**
     * 收益
     */
    private Double earnings;
    /**
     * CPM
     */
    private Double cpm;
    /**
     * 报错数
     */
    private Integer errorNumber;
    /**
     * 报错率
     */
    private Double errorRate;
    /**
     * 算法：1-概率补充；2-手动排序
     */
    // private Integer computer;
}
