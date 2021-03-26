package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FlowCountVo {

    @ApiModelProperty("广告位Id")
    private Integer position_id;

    @ApiModelProperty("分组个数")
    private Integer num;
}
