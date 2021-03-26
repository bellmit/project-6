package com.miguan.xuanyuan.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("代码位DTO")
@Data
public class AdCodeDto {

    @ApiModelProperty("代码位ID：第三方或98广告后台生成的广告ID")
    private String sourceCodeId;

    @ApiModelProperty("广告类型（例如：信息流-c_flow，插屏广告-c_table_screen，Draw信息流广告-c_draw_flow）")
    private String adType;

    @ApiModelProperty("算法：1-手动；2-自动排序（多维度）")
    private int sortType;

    @ApiModelProperty("广告代码位主键ID")
    private Long codeId;

    @ApiModelProperty("阶梯价格")
    private Integer ladderPrice;

    @ApiModelProperty("广告平台plat_key")
    private String sourcePlatKey;

    @ApiModelProperty("广告位置ID")
    private Long positionId;

    @ApiModelProperty("广告位置KEY")
    private String positionKey;

    @ApiModelProperty("渲染方式（例如：模版渲染-c_template_render，自渲染-c_self_render）")
    private String renderType;

    @ApiModelProperty("自定义字段")
    private String customField;

    //@JsonIgnore
    @ApiModelProperty("展示概率/展示序号（后端已经排序）")
    private int optionValue;

    @ApiModelProperty("配比")
    private Integer rateNum;

    @ApiModelProperty("自定义规则1")
    private String customRule1;

    @ApiModelProperty("自定义规则2")
    private String customRule2;

    @ApiModelProperty("自定义规则3")
    private String customRule3;

    @ApiModelProperty("自定义规则4")
    private String customRule4;

    @ApiModelProperty("自定义规则5")
    private String customRule5;

    @ApiModelProperty("自定义规则6")
    private String customRule6;

    @ApiModelProperty("是否走AB实验:0-非AB实验，1-AB实验")
    private Integer testFlag;

    @ApiModelProperty(value = "同一个用户，1个小时内最多展示限制")
    private int showLimitHour;

    @ApiModelProperty(value = "同一个用户，1天内最多展示限制")
    private int showLimitDay;

    @ApiModelProperty(value = "同一个用户，前后两次请求广告的间隔秒数")
    private int showIntervalSec;

    @ApiModelProperty(value = "延迟毫秒数")
    private int delayMillis;

    @ApiModelProperty(value = "ecpm", hidden = true)
    private Double ecpm;

    @JsonIgnore
    @ApiModelProperty(value = "版本操作符，=,>,>=,<,<=,in，not in，-1代表全部")
    private String versionOperation;

    @JsonIgnore
    @ApiModelProperty(value = "版本，多个逗号隔开")
    private String versions;

    @JsonIgnore
    @ApiModelProperty(value = "渠道操作符in，not in，-1代表全部")
    private String channelOperation;

    @JsonIgnore
    @ApiModelProperty(value = "渠道")
    private String channels;
}
