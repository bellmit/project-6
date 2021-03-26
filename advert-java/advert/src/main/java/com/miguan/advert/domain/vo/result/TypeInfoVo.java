package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TypeInfoVo {

    @ApiModelProperty("广告配置代码位Id")
    private Integer id;

    @ApiModelProperty("代码位主键Id")
    private Integer code_id;

    @ApiModelProperty("配置ID")
    private Integer config_id;

    @ApiModelProperty("代码位ID")
    private String ad_id;

    @ApiModelProperty("渲染方式")
    private String ad_render;

    @ApiModelProperty("渠道类型：1-全部 2-仅限 3-排除")
    private Integer channel_type;

    @ApiModelProperty("渠道ids")
    private String channel_ids;

    @ApiModelProperty("渠道名称")
    private String channel_name;

    @ApiModelProperty("阶梯价格")
    private String ladder_price;

    @ApiModelProperty("广告信息")
    private String name;

    @ApiModelProperty("算法：1-手动配比;2-手动排序")
    private Integer computer;

    @ApiModelProperty("配比序号")
    private String number;

    @ApiModelProperty("排序值或者概率值")
    private Integer option_value;

    @ApiModelProperty("是否需要权限 0否 1是")
    private String permission;

    @ApiModelProperty("平台名称")
    private String plat_name;

    @ApiModelProperty("使用SDK")
    private String sdk_key;

    @ApiModelProperty("广告类型名称")
    private String type_name;

    @ApiModelProperty("版本区间")
    private String version;

    @ApiModelProperty("创建时间")
    private String created_at;

    @ApiModelProperty("更新时间")
    private String updated_at;
}
