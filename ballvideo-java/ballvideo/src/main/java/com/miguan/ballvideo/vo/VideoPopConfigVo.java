package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 关于我们bean
 * @author xy.chen
 * @date 2020-07-17
 **/
@Data
@ApiModel("视频弹窗配置")
public class VideoPopConfigVo{
	@ApiModelProperty("1:首页暂停2:首页播放进度3:详情页暂停4:详情页播放进度5:全屏暂停6:全屏播放进度")
	private int popType;

	@ApiModelProperty("进度条范围1")
	private int progress1;

	@ApiModelProperty("进度条范围2")
	private int progress2;

	@ApiModelProperty("图片链接")
	private String imgUrl;

	@ApiModelProperty("1微信 2跳转链接")
	private int jumpType;

	@ApiModelProperty("跳转链接")
	private String jumpLink;
}
