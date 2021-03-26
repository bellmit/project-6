package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author laiyd
 */
@Data
@ApiModel("搜索历史记录实体")
public class SearchHistoryLogVo {

	@ApiModelProperty("设备Id")
	private String deviceId;

	@ApiModelProperty("用户ID")
	private Long userId;

	@ApiModelProperty("搜索词")
	private String searchContent;

}
