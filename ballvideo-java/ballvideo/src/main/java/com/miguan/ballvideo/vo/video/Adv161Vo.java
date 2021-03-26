package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("首页视频源广告VO-V1.6.1")
public class Adv161Vo {

	@ApiModelProperty("广告字段：主键")
	private Long id;

	@ApiModelProperty("广告字段：广告标题")
	private String title;

	@ApiModelProperty("广告字段：广告url")
	private String url;

	@ApiModelProperty("广告字段：广告所在位置类型")
	private String positionType;

	@ApiModelProperty("广告字段：广告所在位置名称")
	private String positionName;

	@ApiModelProperty("广告字段：图片路径")
	private String imgPath;

	@ApiModelProperty("广告字段：广告备注")
	private String remark;

	@ApiModelProperty("广告字段：广告类型（1表示自定义 2表示sdk）")
	private String adType;

	@ApiModelProperty("广告字段：广告商（1表示百度 2表示广点通 3表示穿山甲）")
	private String adCode;

	@ApiModelProperty("广告字段：广告ID")
	private String adId;

	@ApiModelProperty("广告字段：应用ID")
	private String appId;

	@ApiModelProperty("广告字段：手机类型")
	private String mobileType;

	@ApiModelProperty("广告字段：锁屏广告开关")
	private Integer lockScreenToken;

	@ApiModelProperty("广告位置ID")
	private Long positionId;

	@ApiModelProperty("展示概率")
	private int probability;

	@ApiModelProperty("0原广告 1填充互补广告 2阶梯广告")
	private int type;

	@ApiModelProperty("广告组Id")
	private Long groupId;

	@ApiModelProperty("连接类型")
	private String linkType;
}
