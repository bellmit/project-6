package com.miguan.ballvideo.vo.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 推送结果统计表
 * @author laiyd
 * @date 2020-10-22
 **/
@Data
@ApiModel("推送结果统计实体")
public class PushResultCountVo {

	@ApiModelProperty("主键ID")
	private Long id;

	@ApiModelProperty("推送表ID")
	private Long pushArticleId;

	@ApiModelProperty("友盟-YouMeng，华为-HuaWei，VIVO-VIVO，OPPO-OPPO")
	private String pushChannel;

	@ApiModelProperty("自填发送数")
	private Integer sendNum;

	@ApiModelProperty("马甲包")
	private String appPackage;

}
