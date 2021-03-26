package com.miguan.ballvideo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户意见反馈表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("用户意见反馈表实体")
public class ClUserOpinionVo implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PROCESSED = "1";	//	·	1 已处理
	public static final String UNTREATED = "0";	//	·	0 未处理

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

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("1.app启动 2.链接 3.短视频 4.小视频 5.系统消息")
	private int type;

	@ApiModelProperty("链接")
	private String link;

	@ApiModelProperty("type_value保存type对应的值(URL，视频ID等)")
	private String typeValue;

	@ApiModelProperty("创建时间")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date createTime;

	@ApiModelProperty("更新时间")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date updateTime;

	@ApiModelProperty("状态 0未读 1已读")
	private String replyState;

	@ApiModelProperty("马甲包")
	private String appPackage;
}
