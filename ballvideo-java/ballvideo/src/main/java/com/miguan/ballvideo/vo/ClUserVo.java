package com.miguan.ballvideo.vo;

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

    @ApiModelProperty("友盟对设备的唯一标识")
    private String  deviceToken;

    @ApiModelProperty("华为对设备的唯一标识")
    private String  huaweiToken;

	@ApiModelProperty("vivo对设备的唯一标识")
	private String vivoToken;

	@ApiModelProperty("oppo对设备的唯一标识")
	private String oppoToken;

	@ApiModelProperty("小米对设备的唯一标识")
	private String xiaomiToken;

	//---- 2019年9月11日15:44:23  HYL 埋点添加参数
	@ApiModelProperty("启动事件标识")
	private String actionId;

	@ApiModelProperty("app版本")
	private String appVersion;

	@ApiModelProperty("手机版本")
	private String osVersion;

	@ApiModelProperty("渠道ID")
	private String channelId;

	@ApiModelProperty("手机系统")
	private String systemVersion;

	//  HYL   2019年12月16日15:55:21 、埋点添加参数
	@ApiModelProperty("手机imei")
	private String imei;

    @ApiModelProperty("App包名")
    private String appPackage;

}
