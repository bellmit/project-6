package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 音频下载记录表bean
 * @author xy.chen
 * @date 2020-05-22
 **/
@Data
@ApiModel("音频下载记录表实体")
public class AudioDownloadVo {

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("音频ID")
	private Long audioId;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("更新时间")
	private String updateTime;
}
