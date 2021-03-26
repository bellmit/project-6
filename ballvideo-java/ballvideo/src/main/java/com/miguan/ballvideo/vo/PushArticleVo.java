package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 推送消息表
 * @author daoyu
 * @date 2020-06-04
 **/
@Data
@ApiModel("用户表实体")
public class PushArticleVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("标题")
	private String title;

	@ApiModelProperty("内容")
	private String noteContent;

	@ApiModelProperty("1-开启")
	private int state;

	@ApiModelProperty("1-app启动； 2-链接； 3-短视频； 4-小视频；5-系统消息")
	private int type;

	@ApiModelProperty("1-定时 2-推送")
	private String pushType;

	@ApiModelProperty("视频标题")
	private String videoTitle;

	@ApiModelProperty("type_value保存type对应的值（URL，视频ID等)")
	private String typeValue;

	@ApiModelProperty("用户类型：10全部用户 20指定用户")
	private String userType;

    @ApiModelProperty("友盟对设备的唯一标识")
    private String deviceTokens;

    @ApiModelProperty("华为对设备的唯一标识")
    private String  huaweiTokens;

	@ApiModelProperty("vivo对设备的唯一标识")
	private String vivoTokens;

	@ApiModelProperty("oppo对设备的唯一标识")
	private String oppoTokens;

	@ApiModelProperty("小米对设备的唯一标识")
	private String xiaomiTokens;

	@ApiModelProperty("推送有效期")
	private String expireTime;

    @ApiModelProperty("App包名")
    private String appPackage;

	@ApiModelProperty("用户Id，多用户")
	private String userIds;

}
