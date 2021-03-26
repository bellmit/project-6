package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视频专辑表bean
 * @author xy.chen
 * @date 2020-06-02
 **/
@Data
@ApiModel("视频专辑表实体")
public class VideoAlbumVo {

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("简介")
	private String intro;

	@ApiModelProperty("封面图片地址")
	private String coverImageUrl;

	@ApiModelProperty("排序")
	private Long sort;

	@ApiModelProperty("是否需要解锁:1=是,-1=否")
	private Integer needUnlock;

	@ApiModelProperty("专辑第一条视频，点封面图片跳转用")
	private Long videoId;

	@ApiModelProperty("专辑视频数")
	private Long videosCount;
}
