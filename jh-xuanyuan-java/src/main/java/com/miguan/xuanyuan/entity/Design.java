package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 创意管理表
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XyDesign对象", description="创意管理表")
@TableName("xy_design")
public class Design extends Model<Design> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "媒体账号id")
    private Integer userId;

    @ApiModelProperty(value = "计划id")
    private Integer planId;

    @ApiModelProperty(value = "创意标题")
    private String title;

    @ApiModelProperty(value = "创意名称")
    private String name;

    @ApiModelProperty(value = "创意描述")
    private String describeText;

    @ApiModelProperty(value = "按钮文字")
    private String buttonText;

    @ApiModelProperty(value = "素材类型，1:图片，2：视频")
    private Integer materialType;

    @ApiModelProperty(value = "素材url")
    private String materialUrl;

    @ApiModelProperty(value = "创意形式：1--(视频)9:16，2--(视频)16:9，3--(视频)3:2，4--(视频)2:3，5-(视频)-2:1，6--(视频)1:1，7--(图片)9:16，8--(图片)16:9")
    private String materialShape;

    @ApiModelProperty(value = "是否展示平台logo；0：不展示， 1:展示")
    private Integer showLogo;

    @ApiModelProperty(value = "落地页类型，1:落地页链接，2：应用下载地址")
    private Integer landingPageType;

    @ApiModelProperty(value = "落地页链接")
    private String landingPageUrl;

    @ApiModelProperty(value = "创意权重")
    private Integer weight;

    @ApiModelProperty(value = "状态：1启用，0未启用")
    private Integer status;

    @ApiModelProperty(value = "是否删除，0正常，1删除")
    private Integer isDel;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
