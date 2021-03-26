package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * jpa entity class for banner_data_from_adv
 * 从广告库中读取的代码位数据
 */
@Data
@Entity
@Table(name = "banner_data_from_adv")
public class BannerDataFromAdv  implements Serializable {

	/**
	 * 主键
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 日期
	 */
	@Column(name = "date")
	private Date date;

	/**
	 * app名 处理后的数据
	 */
	@Column(name = "cut_app_name")
	private String cutAppName;
	/**
	 * app表的id
	 */
	@Column(name = "app_id")
	private Integer appId;
	/**
	 * 客户端：1安卓 2ios
	 */
	@Column(name = "client_id")
	private Integer clientId;

	/**
	 * 500*包名
	 */
	@Column(name = "app_package")
	private String appPackage;

	/**
	 * 广告位总称
	 */
	@Column(name = "total_name")
	private String totalName;

	/**
	 * 算法：1-概率补充；2-手动排序
	 */
	@Column(name = "computer")
	private Integer computer;

	/**
	 * 排序值或者概率值
	 */
	@Column(name = "option_value")
	private Integer optionValue;

	/**
	 * 排序值或者概率值
	 */
	@Column(name = "sort_value")
	private Integer sortValue;

	/**
	 * 代码位ID*代码位ID：第三方或98广告后台生成的广告ID
	 */
	@Column(name = "ad_id")
	private String adId;

	/**
	 * 渠道类型：1-全部 2-仅限 3-排除
	 */
	@Column(name = "channel_type")
	private Integer channelType;

	/**
	 * 阶梯价格
	 */
	@Column(name = "ladder_price")
	private String ladderPrice;

	/**
	 * AB实验组id
	 */
	private Integer abTestId;

}
