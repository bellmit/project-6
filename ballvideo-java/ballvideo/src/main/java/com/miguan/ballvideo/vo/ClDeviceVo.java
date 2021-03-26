package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户表bean
 * @author xy.chen
 * @date 2019-08-09
 **/
@Data
@ApiModel("设备表实体")
public class ClDeviceVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("状态（10 正常 20 禁用）")
	private String state;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("更新时间")
	private String updateTime;

    @ApiModelProperty("友盟对设备的唯一标识")
    private String  deviceToken;

    @ApiModelProperty("华为对设备的唯一标识")
    private String  huaweiToken;

	@ApiModelProperty("vivo对设备的唯一标识")
	private String vivoToken;

	@ApiModelProperty("oppo对设备的唯一标识")
	private String oppoToken;

	@ApiModelProperty("小米对设备的唯一标识")
	private String xiaomiToken;

    @ApiModelProperty("App包名")
    private String appPackage;

	@ApiModelProperty("是否已删除")
	private Integer isDelete = 0; //0:未删除，1：已删除。

	@ApiModelProperty("设备映射id")
	private String distinctId;

	@ApiModelProperty("App版本号")
	private String appVersion;

	@ApiModelProperty("阿里utdId")
	private String utdId;

}
