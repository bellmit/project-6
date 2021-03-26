package com.miguan.reportview.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据汇总(banner_data_total_name)实体类
 *
 * @author zhongli
 * @since 2020-08-07 10:04:29
 * @description 
 */
@Data
@NoArgsConstructor
@TableName("banner_data_total_name")
public class BannerDataTotalName implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 汇总数据
     */
    @TableId
	private Integer id;
    /**
     * 日期
     */
    private Date date;
    /**
     * 展现量 
     */
    private Integer showNumber;
    /**
     * 展现配比
     */
    private BigDecimal showRate;
    /**
     *  展现占比
     */
    private BigDecimal showRateOccupy;
    /**
     * 点击量
     */
    private Integer clickNumber;
    /**
     * 点击率=点击量/展现量
     */
    private BigDecimal clickRate;
    /**
     * 点击单价=营收/点击量
     */
    private BigDecimal clickPrice;
    /**
     * cpm=营收/展现量*1000
     */
    private BigDecimal cpm;
    /**
     * 营收
     */
    private BigDecimal revenue;
    /**
     * 日活
     */
    private Integer active;
    /**
     *  人均展现数=展现量/日活
     */
    private BigDecimal preShowNum;
    /**
     * 日活价值=营收/日活
     */
    private String activeValue;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;
    /**
     * app 名称
     */
    private String appName;
    /**
     * app表自增 id
     */
    private String appId;
    /**
     * 1安卓 ， 2ios
     */
    private Integer clientId;
    /**
     * 1穿山甲 2广点通 3快手
     */
    private Integer platForm;
    /**
     * 广告样式
     */
    private String adStyle;
    /**
     * 广告类型
     */
    private String adType;
    /**
     * 广告位置规则 总名称(total_name)
     */
    private String adSpace;
    /**
     * app类型:1视频，2炫来电 3快手
     */
    private Integer appType;
    /**
     * md5(date + total_name+ app_name + client_id +  plat_form)
     */
    private String uniqueKey;

}