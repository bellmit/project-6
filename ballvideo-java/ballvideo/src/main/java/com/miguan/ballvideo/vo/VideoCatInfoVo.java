package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("跳转小程序视频分类信息")
public class VideoCatInfoVo {
	
	@ApiModelProperty("马甲包")
	private String appPackage;

	@ApiModelProperty("视频类别")
	private String videoCat;
}
