package com.miguan.xuanyuan.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreativeInfoVo {
    @ApiModelProperty("代码位ID")
    private String code;
    @ApiModelProperty("计划ID")
    private Long planId;
    @ApiModelProperty("创意ID")
    private Long designId;
    @ApiModelProperty("应用ID")
    private Long appId;

    @ApiModelProperty("产品名称")
    private String productName;
    @ApiModelProperty("品牌logo")
    private String brandLogo;

    @ApiModelProperty("广告类型，interaction:插屏, infoFlow:信息流广告, banner:Banner广告, open_screen:开屏广告")
    private String advertType;

    @ApiModelProperty("创意标题")
    private String designTitle;
    @ApiModelProperty("按钮文字")
    private String buttonText;
    @ApiModelProperty("文案") // 创意描述 即 文案
    private String ideaTitle;

    @ApiModelProperty("素材类型，1:图片，2：视频")
    private String materialType;

    @ApiModelProperty("创意形式：1--竖版大图9:16，2--横版大图16:9，3--横版长图6:1，4--左图右文1.5:1，5--右图作文1.5:1，6--竖版视频9:16，7--横版视频16:9")
    private String materialShape;
    @ApiModelProperty("尺寸 - 9:16,16:9")
    private String shape;

    @ApiModelProperty("创意素材")
    private String materialUrl;

    @ApiModelProperty("轩辕平台logo")
    private String xyPlatLogo;

    @ApiModelProperty("是否展示平台logo；0：不展示， 1:展示")
    private String showLogo;

    @ApiModelProperty("跳转链接类型")
    private String landingPageType;
    @ApiModelProperty("跳转链接")
    private String landingPageUrl;

    @ApiModelProperty("权重")
    private int weight;

//    @ApiModelProperty("广告主ID")
//    private String usrId;
//    @ApiModelProperty("广告主名称")
//    private String userName;
//    @ApiModelProperty("产品名称")
//    private String productName;

//    @ApiModelProperty("广告来源,默认:98_adv=98自投")
//    private String advSource;
//    @ApiModelProperty("视频广告自动播放开启声音,默认：true")
//    private boolean isView;
//    @ApiModelProperty("广告类型")
//    private String positionType;
//    @ApiModelProperty("广告规格")
//    private String styleSize;
}
