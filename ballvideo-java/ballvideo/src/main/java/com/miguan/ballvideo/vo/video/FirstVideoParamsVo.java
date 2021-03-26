package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 推荐接口请求大数据参数
 */
@Data
public class FirstVideoParamsVo {

	@ApiModelProperty("渠道默认分类, 多个逗号间隔")
	private String defaultCat;

	@ApiModelProperty("需要屏蔽的分类, 多个逗号间隔")
	private String excludeCat;

	@ApiModelProperty("激励视频个数")
	private String incentiveVideoNum;

	@ApiModelProperty("客户端的IP地址")
	private String ip;

	@ApiModelProperty("设备标识DeviceId")
	private String deviceId;

	@ApiModelProperty("Public-Info")
	private String publicInfo;

	@ApiModelProperty("分类ID")
	private String catid;

	@ApiModelProperty("视频数量")
	private String num;

	@ApiModelProperty("视频Id")
	private String videoId;

	@ApiModelProperty("ab实验信息")
	private String abExp;

	@ApiModelProperty("ab实验信息")
	private String abIsol;

	@ApiModelProperty("敏感词开关 1开0关")
	private String sensitiveState;
}
