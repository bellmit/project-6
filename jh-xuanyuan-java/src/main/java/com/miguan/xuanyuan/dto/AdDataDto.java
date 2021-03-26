package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("广告数据封装")
@Data
public class AdDataDto {

    @ApiModelProperty(value = "同一个用户，1个小时内最多展示限制")
    private int showLimitHour;

    @ApiModelProperty(value = "同一个用户，1天内最多展示限制")
    private int showLimitDay;

    @ApiModelProperty(value = "同一个用户，前后两次请求广告的间隔秒数")
    private int showIntervalSec;

    private List<AdCodeDto> adCodes;

}
