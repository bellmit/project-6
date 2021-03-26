package com.miguan.xuanyuan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value="计划表单dto", description="计划表单dto")
public class PlanDto {
    @ApiModelProperty(value = "计划id", position = 10)
    private Integer id;

    @ApiModelProperty(value = "媒体账号id", position = 20)
    private Integer userId;

    @NotBlank(message="计划名称不能为空")
    @Length(message="计划名称不能超过20个字符", max=20)
    @ApiModelProperty(value = "计划名称", position = 30)
    private String name;

    @Length(message="计划名称不能超过20个字符", max=20)
    @ApiModelProperty(value = "产品名称", position = 31)
    private String productName;

    @ApiModelProperty(value = "品牌logo", position = 32)
    private String brandLogo;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value = "开始时间", position = 40)
    private Date startDate;

    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    @ApiModelProperty(value = "结束时间，为空给一个默认最大。", position = 50)
    private Date endDate;

    @ApiModelProperty(value = "投放日期类型，0-长期投放，1-指定开始日期和结束日期", position = 55)
    private Integer putTimeType;

    @ApiModelProperty(value = "投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段", position = 60)
    private Integer timeSetting;

    @ApiModelProperty(value = "时间配置json[{week_day:1,start_hour:0,end_hour:47}]", position = 70)
    private String timesConfig;

    @ApiModelProperty(value = "总预算（元）", position = 80)
    private Double totalPrice;

    @ApiModelProperty(value = "广告类型，interaction:插屏, infoFlow:信息流广告, banner:Banner广告, open_screen:开屏广告", position = 90)
    private String advertType;

    @ApiModelProperty(value = "状态：1启用，0未启用", position = 100)
    private Integer status;

    @ApiModelProperty(value = "广告位id列表", position = 110)
    private List<Integer> positionList;

    @Valid
    @ApiModelProperty(value = "创意列表", position = 120)
    private List<DesignDto> designList;

    @ApiModelProperty(value = "投放状态,0-未开始投放，1-投放中，2-投放结束", position = 130)
    private Integer putInState;
}
