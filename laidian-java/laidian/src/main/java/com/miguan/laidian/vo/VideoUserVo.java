package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 安卓视频用户关联表
 * @author shixh
 * @date 2019-09-29
 **/
@Data
@ApiModel("用户首页视频关联表实体")
public class VideoUserVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("用户id")
	private Long userId;

	@ApiModelProperty("视频id")
	private Long videoId;

	@ApiModelProperty("收藏")
	private String userLikeCount;

	@ApiModelProperty("分享")
	private String shareCount;

	@ApiModelProperty("点击")
	private String clickCount;

	@ApiModelProperty("来电设置成功数")
	private String successNum;

	@ApiModelProperty("马甲包类型：xld--炫来电，wld-微来电")
	private String appType;

}
