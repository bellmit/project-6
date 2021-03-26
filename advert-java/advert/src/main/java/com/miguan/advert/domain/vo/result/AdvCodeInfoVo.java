package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @program: advert-java
 * @description: 广告配置代码位信息
 * @author: suhj
 * @create: 2020-09-25 20:12
 **/

@ApiModel("广告配置代码位信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvCodeInfoVo implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("配比序号")
    private Integer number;

    @ApiModelProperty("配比率（%）")
    private Integer matching;

    @ApiModelProperty("排序序号")
    private Integer orderNum;

    @ApiModelProperty("所属广告平台")
    private String plat_key;

    @ApiModelProperty("所属广告平台名称")
    private String plat_name;

    @ApiModelProperty("代码位ID")
    private String ad_id;

    @ApiModelProperty("阶梯类型：1阶梯，2非阶梯")
    private Integer ladder;

    @ApiModelProperty("阶梯价格")
    private String ladder_price;

    @ApiModelProperty("渠道类型：1-全部 2-仅限 3-排除")
    private Integer channel_type;

    @ApiModelProperty("渠道ID：所有：-1，一个或者多个逗号隔开")
    private String channel_ids;

    @ApiModelProperty("渠道名称：所有，一个或者多个逗号隔开")
    private String channel_names;

    @ApiModelProperty("广告类型,对应表ad_type")
    private String type_key;

    @ApiModelProperty("版本区间1")
    private String version1;

    @ApiModelProperty("版本区间2")
    private String version2;

    @ApiModelProperty("是否需要权限 0否 1是")
    private String permission;

    @ApiModelProperty("投放状态，0：未投放，1：已投放")
    private Integer put_in;

    @ApiModelProperty("创建时间")
    private String created_at;

    @ApiModelProperty("更新时间")
    private String updated_at;

    @ApiModelProperty("ecpm")
    private Double ecpm = 0d;


}
