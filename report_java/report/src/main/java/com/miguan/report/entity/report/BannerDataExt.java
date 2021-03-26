package com.miguan.report.entity.report;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * jpa entity class for banner_data_ext
 * 源数据扩展表
*/
@Data
@Entity
@Table(name = "banner_data_ext")
public class BannerDataExt  implements Serializable {

	public BannerDataExt(){}

	public BannerDataExt(String adId, Integer errNum, Integer reqNum, Date date, Integer appType) {
		this.adId = adId;
		this.errNum = errNum;
		this.reqNum = reqNum;
		this.date = date;
		this.appType = appType;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 代码位id
	 */
	@Column(name = "ad_id")
	private String adId;

	/**
	 *  错误数
	 */
	@Column(name = "err_num")
	private Integer errNum;

	/**
	 * 错误率
	 */
	@Column(name = "err_rate")
	private Double errRate;

	/**
	 * 请求数
	 */
	@Column(name = "req_num")
	private Integer reqNum;

	/**
	 * 日期
	 */
	private Date date;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Integer createTime;

	/**
	 * app类型:1视频，2炫来电
	 */
	@Column(name = "app_type")
	private Integer appType;

}
