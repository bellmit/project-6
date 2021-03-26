package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("用户表实体")
public class ClUserVo implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String USE = "10";//正常
	public static final String NO_USE = "20";//禁用

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("昵称")
	private String name;

	@ApiModelProperty("账号（手机号）")
	private String loginName;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("头像URL")
	private String imgUrl;

	@ApiModelProperty("签名")
	private String sign;

	@ApiModelProperty("状态（10 正常 20 禁用）")
	private String state;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("更新时间")
	private String updateTime;

	@ApiModelProperty("华为对设备的唯一标识")
	private String  huaweiToken;

	@ApiModelProperty("vivo对设备的唯一标识")
	private String vivoToken;

	@ApiModelProperty("oppo对设备的唯一标识")
	private String oppoToken;

	@ApiModelProperty("小米对设备的唯一标识")
	private String xiaomiToken;


}
