package com.miguan.report.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * jpa entity class for banner_rule
 * 广告位规则
*/
@Data
@Entity
@Table(name = "banner_rule")
public class BannerRule  implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	/**
	 * 规则ID
	 */
	private Integer id;

	/**
	 * 广告位名字
	 */
	@Column(name = "ad_space")
	private String adSpace;

	/**
	 * 广告位类型
	 */
	@Column(name = "ad_type")
	private String adType;

	/**
	 * 广告位样式
	 */
	@Column(name = "ad_style")
	private String adStyle;

	/**
	 * 匹配的key
	 */
	@Column(name = "search_key")
	private String searchKey;

	/**
	 * 匹配的key(java端使用)
	 */
	private String key;

	/**
	 * 1开启，2关闭
	 */
	private Integer status;

	/**
	 * 1非阶梯，2阶梯
	 */
	private Integer jt;

	/**
	 * app类型:1视频，2炫来电
	 */
	@Column(name = "app_type")
	private Integer appType;

	/**
	 * 总名称
	 */
	@Column(name = "total_name")
	private String totalName;

}
