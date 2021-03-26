package com.miguan.ballvideo.vo.push;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * 推送埋点参数
 * @author suhhong
 * @date 2020-10-28
 **/
@Data
@ApiModel("推送埋点参数实体")
public class PushBuryParamVo {

	@ApiModelProperty("推送消息ID")
	private Integer push_id;

	@ApiModelProperty("友盟-YouMeng，华为-HuaWei，VIVO-VIVO，OPPO-OPPO")
	private String push_phannel;

	@ApiModelProperty("推送分类 1-push，2-消息，3-短信")
	private Integer push_category;

	@ApiModelProperty("推送的项目 1：内容 2：签到提醒 3：活动  4：用户行为")
	private Integer push_project;

	@ApiModelProperty("推送内容类型 1：详情页  2：铃声列表页  3：来电首页")
	private Integer push_type;

	@ApiModelProperty("推送分类 0-老推送，1-自动推送")
	private Integer push_method;

	@ApiModelProperty("推送操作方法")
	private Integer push_opt;

	@ApiModelProperty("推送周期类型")
	private Integer push_cycle;


}
