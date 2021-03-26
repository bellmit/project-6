package com.miguan.report.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("代码位分析详细数据实体")
@Data
public class CodeSpaceDataVo {

    @ApiModelProperty("代码位")
    private String adSpaceId;
    @ApiModelProperty("价格")
    private Double price;
    @ApiModelProperty("渠道类型")
    private Integer channelType;
    @ApiModelProperty("渠道类型名称")
    private String channelTypeStr;
    @ApiModelProperty("排序")
    private Integer optionValue;
    @ApiModelProperty("广告请求量")
    private Integer adRequest;
    @ApiModelProperty("广告返回量")
    private Integer adReturn;
    @ApiModelProperty("广告填充率")
    private Double adFilling;
    @ApiModelProperty("展现量")
    private Integer showNumber;
    @ApiModelProperty("展现率(展示数/广告返回数)")
    private Double showNumberRate;
    @ApiModelProperty("点击数")
    private Integer clickNumber;
    @ApiModelProperty("点击率")
    private Double clickRate;
    @ApiModelProperty("收益")
    private Double earnings;
    @ApiModelProperty("CPM")
    private Double cpm;
    @ApiModelProperty("请求数")
    private Integer requestNumber;
    @ApiModelProperty("报错数")
    private Integer errorNumber;
    @ApiModelProperty("报错率")
    private Double errorRate;
    /**
     * 算法：1-概率补充；2-手动排序
     */
    @ApiModelProperty("算法：1-概率补充；2-手动排序")
    private Integer computer;
}
