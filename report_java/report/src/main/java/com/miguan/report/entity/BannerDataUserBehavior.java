package com.miguan.report.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * jpa entity class for banner_data_user_behavior
 * 数据汇总-用户行为
*/
@Data
@Entity
@Table(name = "banner_data_user_behavior")
public class BannerDataUserBehavior  implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	/**
	 * 汇总数据
	 */
	private Integer id;

	/**
	 * 日期
	 */
	private java.util.Date date;

	/**
	 * 注：没有除以日活;启动页-人均启动次数;视频底部banner广告-2.3版本以上用户人均首页列表观看视频数;视频结束广告-人均观看视频完播数;视频开始广告-人均观看视频数;视频中间广告-人均视频观看35%以上视频数;首页列表-人均观看视频数（来源是首页列表）;首页视频详情-人均观看视频数（来源是详情）;搜索结果广告-人均观看视频数（来源是搜索结果）;搜索页广告-人均观看视频数（来源是搜索页）;锁屏原生广告-人均观看视频数（来源是锁屏列表）;小视频列表-人均小视频菜单栏点击用户数;小视频详情-人均观看视频数（类型是小视频）
	 */
	@Column(name = "show_value")
	private Double showValue;

	/**
	 * 日活
	 */
	private Integer active;

	/**
	 * app 名称
	 */
	@Column(name = "app_name")
	private String appName;

	/**
	 * app表自增 id
	 */
	@Column(name = "app_id")
	private String appId;

	/**
	 * 1安卓 ， 2ios
	 */
	@Column(name = "client_id")
	private Integer clientId;

	/**
	 * 广告位置规则 总名称(total_name)
	 */
	@Column(name = "ad_space")
	private String adSpace;

	/**
	 * app类型:1视频，2炫来电
	 */
	@Column(name = "app_type")
	private Integer appType;

	/**
	 * 创建时间
	 */
	@Column(name = "created_at")
	private java.util.Date createdAt;

}
