package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("专辑组装信息实体")
public class PackagingAlbumVo {

	@ApiModelProperty("专辑标题")
	private String title;

	@ApiModelProperty("是否需要解锁:1=是,-1=否")
	private Integer needUnlock;
}
