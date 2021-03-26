package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
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

	@NotEmpty(message = "设备ID不能为空")
	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("状态（10 正常 20 禁用）")
	private String state;

	@ApiModelProperty("创建时间")
	private String createTime;

	@ApiModelProperty("更新时间")
	private String updateTime;

    @ApiModelProperty("华为对设备的唯一标识")
    private String  huaweiToken;

	@ApiModelProperty("vivo对设备的唯一标识")
	private String vivoToken;

	@ApiModelProperty("oppo对设备的唯一标识")
	private String oppoToken;

	@ApiModelProperty("小米对设备的唯一标识")
	private String xiaomiToken;

	@NotEmpty(message = "包名不能为空")
    @ApiModelProperty("App包名")
    private String appType;

	@NotEmpty(message = "渠道Id不能为空")
	@ApiModelProperty("渠道Id")
	private String channelId;

	@ApiModelProperty("App版本号")
	private String appVersion;

	@ApiModelProperty("设备映射id")
	private String distinctId;
}
