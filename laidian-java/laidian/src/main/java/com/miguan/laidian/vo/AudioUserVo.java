package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 音频用户关系表bean
 * @author xy.chen
 * @date 2020-05-22
 **/
@Data
@ApiModel("音频用户关系表实体")
public class AudioUserVo{

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("音频ID")
	private Long audioId;

	@ApiModelProperty("试听 0未试听 1试听")
	private Integer audition;

	@ApiModelProperty("下载 0未下载 1下载")
	private Integer download;

	@ApiModelProperty("收藏 0 未收藏 1收藏")
	private Integer collection;

	@ApiModelProperty("分享数")
	private Integer shareCount;

	@ApiModelProperty("创建时间")
	private String createdAt;

	@ApiModelProperty("更新时间")
	private String updatedAt;
}
