package com.miguan.laidian.entity;

import com.miguan.laidian.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel("用户标签初始化表")
@Entity(name="user_label_default")
@Data
public class UserLabelDefault extends BaseModel {

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("第一标签（分类ID1）")
    private Long catId1;

    @ApiModelProperty("第二标签（分类ID2）")
    private Long catId2;

    @ApiModelProperty("备注")
    private String remake;

    @ApiModelProperty("状态：1启用  0禁用")
    private Integer state;

}
