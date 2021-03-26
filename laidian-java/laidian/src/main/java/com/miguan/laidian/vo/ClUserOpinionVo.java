package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户意见反馈表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("用户意见反馈表实体")
public class ClUserOpinionVo implements Serializable {

	private static final long serialVersionUID = 1L;

	//	意见反馈结果	1 已处理
	public static final String PROCESSED = "1";
	//	意见反馈结果	0 未处理
	public static final String UNTREATED = "0";

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("用户ID")
	private Long userId;

	@ApiModelProperty("反馈内容")
	private String content;

	@ApiModelProperty("反馈图片链接")
	private String imageUrl;

	@ApiModelProperty("状态 0 未处理 1 已处理")
	private String state;

	@ApiModelProperty("回复内容")
	private String reply;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("更新时间")
	private String updateTime;

	@ApiModelProperty("app类型  微来电-wld,炫来电-xld")
	private String appType;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("联系方式")
	private String contact;

	@ApiModelProperty("手机品牌")
	private String telBrand;

	@ApiModelProperty("手机机型")
	private String telType;

	@ApiModelProperty("机型版本")
	private String telTypeVision;

	@ApiModelProperty("安卓版本")
	private String androidVision;

	@ApiModelProperty("未开启的权限")
	private String noopenPermission;

	@ApiModelProperty("状态 0未读 1已读")
	private String replyState;
}
