package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("首页视频信息")
public class VideosInfoVo {

	@ApiModelProperty("视频字段：主键")
	private Long id;

	@ApiModelProperty("视频字段：视频标题")
	private String title;

	@ApiModelProperty("视频字段：分类id")
	private Long catId;

	@ApiModelProperty("视频字段：白山云视频地址")
	private String bsyUrl;

	@ApiModelProperty("视频字段：白山云图片地址")
	private String bsyImgUrl;

	@ApiModelProperty("视频字段：视频作者头像地址")
	private String bsyHeadUrl;

	@ApiModelProperty("视频字段：视频作者")
	private String videoAuthor;

	@ApiModelProperty("视频字段：观看数")
	private String watchCount;
}
