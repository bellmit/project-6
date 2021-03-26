package com.miguan.laidian.vo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 签到计划bean
 * @author xy.chen
 * @date 2020-08-14
 **/
@Data
@ApiModel("签到记录")
public class ActSignRecordVo {

	@ApiModelProperty("主键")
	private Long id;

	@ApiModelProperty("活动ID")
	private Long activityId;

	@ApiModelProperty("设备ID")
	private String deviceId;

	@ApiModelProperty("签到时间")
	private String signTime;

	@ApiModelProperty("天数（1-7，中断就从第1天开始）")
	private int day;
}

