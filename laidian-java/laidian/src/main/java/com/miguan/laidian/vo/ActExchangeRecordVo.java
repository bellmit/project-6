package com.miguan.laidian.vo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 兑奖记录
 * @author xy.chen
 * @date 2020-08-17
 **/
@Data
@ApiModel("兑奖记录")
public class ActExchangeRecordVo {

	@ApiModelProperty("奖品名称")
	private String prizeName;

	@ApiModelProperty("兑奖状态 0核实中 1已发放")
	private int state;

	@ApiModelProperty("兑奖时间")
	private String createdAt;
}

