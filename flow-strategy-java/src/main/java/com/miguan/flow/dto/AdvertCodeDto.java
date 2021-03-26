package com.miguan.flow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("广告VO")
@Data
public class AdvertCodeDto {

    @ApiModelProperty("广告代码位主键ID")
    private Long id;

    @ApiModelProperty("代码位ID：第三方或98广告后台生成的广告ID")
    private String adId;

    @ApiModelProperty("广告位置ID")
    private Long positionId;

    @ApiModelProperty("广告位关键字")
    private String positionType;

    @ApiModelProperty("手机类型")
    private String mobileType;

    @ApiModelProperty("首次加载位置")
    private Integer firstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private Integer secondLoadPosition;

    @ApiModelProperty("广告展示次数限制")
    private Integer maxShowNum;

    @ApiModelProperty("广告平台（例如：穿山甲-chuan_shan_jia，广点通-guang_dian_tong，快手-kuai_shou，广告-98_adv）")
    private String plat;

    @ApiModelProperty("广告类型（例如：信息流-c_flow，插屏广告-c_table_screen，Draw信息流广告-c_draw_flow）")
    private String adType;

    //@JsonIgnore
    @ApiModelProperty("展示概率/展示序号（后端已经排序）")
    private int optionValue;

    @ApiModelProperty("算法：1-概率补充；2-手动排序;3-自动排序;4-自动排序（多维度）")
    private int computer;

    @ApiModelProperty("sdk版本号")
    private String sdk;

    @ApiModelProperty("渲染方式（例如：模版渲染-c_template_render，自渲染-c_self_render）")
    private String render;

    @ApiModelProperty("广告素材（例如：图文-g_img_text，视频-g_video，图文+视频-g_img_text_video）")
    private String material;

    @ApiModelProperty("广告标题")
    private String title;

    @ApiModelProperty("落地页/应用包地址")
    private String url;

    @ApiModelProperty("落地页应用包类型：0-落地页，1-应用包")
    private String linkType;

    @ApiModelProperty("原生图片路径")
    private String imgPath;

    @ApiModelProperty("当前广告是否需要权限（测试用）")
    private String permission;

    @ApiModelProperty("阶梯价格")
    private Integer ladderPrice;

    @ApiModelProperty("自定义规则1")
    private String customRule1;

    @ApiModelProperty("自定义规则1名称")
    private String customRule1Name;

    @ApiModelProperty("自定义规则2")
    private String customRule2;

    @ApiModelProperty("自定义规则2名称")
    private String customRule2Name;

    @ApiModelProperty("自定义规则3")
    private String customRule3;

    @ApiModelProperty("自定义规则3名称")
    private String customRule3Name;

    @ApiModelProperty("是否走AB实验:0-非AB实验，1-AB实验")
    private Integer testFlag;

    @ApiModelProperty("配比序号")
    private Integer sortNumber;

    @ApiModelProperty(value = "配比序号", hidden = true)
    private Double ecpm;

    @ApiModelProperty("阶梯价延迟毫秒数")
    private Integer ladderDelayMillis;

    @ApiModelProperty("通跑延迟毫秒数")
    private Integer commonDelayMillis;

    @ApiModelProperty(value = "延迟毫秒数")
    private int delayMillis;

}
