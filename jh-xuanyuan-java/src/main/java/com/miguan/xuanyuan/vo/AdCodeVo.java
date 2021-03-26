package com.miguan.xuanyuan.vo;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * 广告源详情
 *
 */
@ApiModel("广告源详情")
@Data
public class AdCodeVo {
    @ApiModelProperty("广告位id")
    private Long id;

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("广告源名称")
    private String codeName;

    @ApiModelProperty("第三方广告平台key")
    private String sourcePlatKey;

    @ApiModelProperty("第三方广告平台id")
    private Long sourcePlatAccountId;

    @ApiModelProperty("广告平台应用id")
    private String sourceAppId;

    @ApiModelProperty("广告平台代码位id")
    private String sourceCodeId;

    @ApiModelProperty("渲染方式")
    private String renderType;

    @ApiModelProperty("是否为阶梯广告")
    private Integer isLadder;

    @ApiModelProperty("阶梯价格")
    private Long ladderPrice;


    @ApiModelProperty("同一个用户，1个小时内最多展示限制")
    private Long showLimitHour;

    @ApiModelProperty("同一个用户，1天内最多展示限制")
    private Long showLimitDay;

    @ApiModelProperty("同一个用户，前后两次请求广告的间隔秒数")
    private Long showIntervalSec;

    @ApiModelProperty("版本操作")
    private String versionOperation;

    @ApiModelProperty("版本")
    private String versions = "";

    @ApiModelProperty("渠道操作")
    private String channelOperation;

    @ApiModelProperty("渠道")
    private String channels;

    @ApiModelProperty("备注")
    private String note = "";

    @ApiModelProperty("状态，1启用，0禁用")
    private Integer status = 1;

    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel = 0;
}
