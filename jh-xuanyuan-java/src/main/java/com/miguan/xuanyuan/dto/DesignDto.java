package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="创意表单dto", description="创意表单dto")
public class DesignDto {

    @ApiModelProperty(value = "创意id", position = 10)
    private Integer id;

    @ApiModelProperty(value = "媒体账号id", position = 20)
    private Integer userId;

    @ApiModelProperty(value = "计划id", position = 30)
    private Integer planId;

    @Length(message="创意标题不能超过30个字符", max=30)
    @NotBlank(message = "创意标题不能为空")
    @ApiModelProperty(value = "创意标题", position = 40)
    private String title;

    @ApiModelProperty(value = "创意名称", position = 41)
    private String name;

    @Length(message="创意描述不能超过100个字符", max=100)
    @ApiModelProperty(value = "创意描述", position = 50)
    private String describeText;

    @Length(message="按钮文字不能超过6个字符", max=6)
    @ApiModelProperty(value = "按钮文字", position = 60)
    private String buttonText;

    @ApiModelProperty(value = "素材类型，1:图片，2：视频", position = 70)
    private Integer materialType;

    @ApiModelProperty(value = "素材url", position = 71)
    private String materialUrl;

    @ApiModelProperty(value = "创意形式：1--(视频)9:16，2--(视频)16:9，3--(视频)3:2，4--(视频)2:3，5-(视频)-2:1，6--(视频)1:1，7--(图片)9:16，8--(图片)16:9", position = 80)
    private String materialShape;

    @ApiModelProperty(value = "是否展示平台logo；0：不展示， 1:展示", position = 90)
    private Integer showLogo;

    @ApiModelProperty(value = "落地页类型，1:落地页链接，2：应用下载地址", position = 100)
    private Integer landingPageType;

    @ApiModelProperty(value = "落地页链接", position = 105)
    private String landingPageUrl;

    @ApiModelProperty(value = "创意权重", position = 110)
    private Integer weight;

    @ApiModelProperty(value = "状态：1启用，0未启用", position = 111)
    private Integer status;

    @ApiModelProperty(value = "是否删除，0正常，1删除", position = 120)
    private Integer isDel;
}
