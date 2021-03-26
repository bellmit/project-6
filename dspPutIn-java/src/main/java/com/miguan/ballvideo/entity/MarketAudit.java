package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 市场审核开关配置表
 * @Author shixh
 * @Date 2019/12/3
 **/
@Entity(name="market_audit")
@Data
public class MarketAudit extends BaseModel {

    @ApiModelProperty("渠道ID，这里是子渠道")
    private String channelId;
    @ApiModelProperty("屏蔽分类ID集合")
    private String catIds;
    @ApiModelProperty("起始版本1")
    private String version1;
    @ApiModelProperty("起始版本2")
    private String version2;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("状态 1开启 0关闭")
    private Integer state;

    //V2.0.0
    @ApiModelProperty("青少年模式：状态 1开启 0关闭")
    private Integer teenagerState;
    @ApiModelProperty("青少年模式：起始版本1")
    private String teenagerVersion1;
    @ApiModelProperty("青少年模式：起始版本2")
    private String teenagerVersion2;
    @ApiModelProperty("青少年模式：屏蔽分类ID集合")
    private String teenagerCatIds;

    //V2.1.0
    @ApiModelProperty("屏蔽合集ID")
    private String gatherIds;

    @ApiModelProperty("屏蔽热词ID")
    private String hotWords;
}
