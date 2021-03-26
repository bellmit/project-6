package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 签到状态
 * @author xy.chen
 * @date 2020-08-15
 **/
@Data
@ApiModel("签到状态")
public class ActSignVo {

	@ApiModelProperty("类型 1签到")
	private Integer type;

	@ApiModelProperty("天数（1-7，中断就从第1天开始）")
	private int day;

	@ApiModelProperty("是否今天 1是  0否")
	private int isToday;

	@ApiModelProperty("状态 0未签到 1已签到")
	private int state;
}

