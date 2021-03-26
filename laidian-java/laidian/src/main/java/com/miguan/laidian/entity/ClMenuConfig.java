package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 菜单栏配置表bean
 *
 * @author xy.chen
 * @date 2019-08-23
 **/
@Data
@ApiModel("菜单栏配置表实体")
public class ClMenuConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("关键字")
    private String key;

    @ApiModelProperty("图标URL")
    private String imgUrl;

    @ApiModelProperty("图标URL2")
    private String imgUrl2;

    @ApiModelProperty("链接地址")
    private String linkAddr;

    @ApiModelProperty("有无广告：0无广告  1有广告")
    private String hasAdv;

    @ApiModelProperty("状态：0启用  1禁用")
    private Integer state;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("更新时间")
    private String updatedAt;

    @ApiModelProperty("广告位置key")
    private String bannerId;

    @ApiModelProperty("马甲包类型")
    private String appType;

}
