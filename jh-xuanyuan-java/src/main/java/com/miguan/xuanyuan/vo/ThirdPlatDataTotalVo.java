package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/3/5 13:51
 **/
@Data
public class ThirdPlatDataTotalVo {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "广告来源平台，例如：kuai_shou，guang_dian_tong，chuan_shan_jia")
    private String adSource;

    @ApiModelProperty(value = "分类级别对应id（对应数据字典的 app_type）")
    private String appTypeCode;

    @ApiModelProperty(value = "应用分类名称（对应数据字典的 app_type）")
    private String appTypeName;

    @ApiModelProperty(value = "应用key")
    private String appKey;

    @ApiModelProperty(value = "应用名称")
    private String cutAppName;

    @ApiModelProperty(value = "客户端：1安卓 2ios")
    private Integer clientId;

    @ApiModelProperty(value = "马甲包")
    private String packageName;

    @ApiModelProperty(value = "广告位名称")
    private String totalName;

    @ApiModelProperty(value = "代码位id")
    private String adId;

    @ApiModelProperty(value = "代码位阶梯价格")
    private Double price;

    @ApiModelProperty(value = "广告位请求量")
    private Integer requestCount;

    @ApiModelProperty(value = "广告位返回量")
    private Integer returnCount;

    @ApiModelProperty(value = "广告请求量")
    private Integer adRequestCount;

    @ApiModelProperty(value = "广告返回量")
    private Integer adReturnCount;

    @ApiModelProperty(value = "曝光量")
    private Integer show;

    @ApiModelProperty(value = "点击量")
    private Integer click;

    @ApiModelProperty(value = "预估收益")
    private Double revenue;

    @ApiModelProperty(value = "广告类型code")
    private String adTypeCode;

    @ApiModelProperty(value = "广告类型名称")
    private String adTypeName;

    @ApiModelProperty(value = "广告配置来源，0--旧广告配置系统，1--轩辕广告配置")
    private Integer advConfigType;
}
