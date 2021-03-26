package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AdConfigLogVo {

    @ApiModelProperty(value = "日期,格式：yyyy-MM-dd")
    private String date;

    @ApiModelProperty(value = "第三方平台可以，例如：kuai_shou，guang_dian_tong，chuan_shan_jia")
    private String platKey;

    @ApiModelProperty(value = "app包名")
    private String packageName;

    @ApiModelProperty(value = "客户端类型，1安卓，2ios")
    private String clientType;

    @ApiModelProperty(value = "广告位id")
    private Integer positionId;

    @ApiModelProperty(value = "广告位key")
    private String positionKey;

    @ApiModelProperty(value = "广告位名称")
    private String totalName;

    @ApiModelProperty(value = "代码位id")
    private String adId;

    @ApiModelProperty(value = "代码位阶梯价")
    private Double price;

    @ApiModelProperty(value = "是否AB实验：0-非AB实验，1-AB实验")
    private Integer isTest;

    @ApiModelProperty(value = "AB实验组别，格式：实验id-测试组id")
    private String abExp;

    @ApiModelProperty(value = "代码位排序")
    private Integer adList;

    @ApiModelProperty(value = "广告类型code")
    private String adTypeCode;

    @ApiModelProperty(value = "广告类型name")
    private String adTypeName;

    @ApiModelProperty(value = "广告配置来源，0--旧广告配置系统，1--轩辕广告配置")
    private Integer advConfigType;
}
