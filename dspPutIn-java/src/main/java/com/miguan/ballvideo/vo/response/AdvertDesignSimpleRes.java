package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("简单广告创意")
public class AdvertDesignSimpleRes {
    @ApiModelProperty("广告计划id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty("计划名称")
    private String name;
}
