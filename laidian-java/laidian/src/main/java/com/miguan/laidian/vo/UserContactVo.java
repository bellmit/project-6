package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * bean
 * @author xy.chen
 * @date 2019-11-04
 **/
@Data
@ApiModel("用户通讯录实体")
public class UserContactVo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("联系人")
	private String name;

	@ApiModelProperty("手机号码")
	private String phone;

	@ApiModelProperty("创建时间")
	private Date createTime;

}
