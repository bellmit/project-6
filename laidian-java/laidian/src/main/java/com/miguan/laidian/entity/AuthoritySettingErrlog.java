package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 权限设置异常日志bean
 * @author laiyd
 * @date 2019-10-16
 **/
@Entity(name = "authority_setting_errlog")
@Getter
@Setter
@ApiModel("权限设置异常日志实体")
public class AuthoritySettingErrlog implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ApiModelProperty("手机品牌")
	@Column(name = "device_trademark")
	private String deviceTrademark;

	@ApiModelProperty("手机型号")
	@Column(name = "device_info")
	private String deviceInfo;

	@ApiModelProperty("系统版本")
	@Column(name = "os_version")
	private String osVersion;

	@ApiModelProperty("设备码")
	@Column(name = "device_id")
	private String deviceId;

	@ApiModelProperty("权限名称")
	@Column(name = "authority_info")
	private String authorityInfo;

	@ApiModelProperty("创建时间")
	@Column(name = "create_time")
	private Date createTime;
}
