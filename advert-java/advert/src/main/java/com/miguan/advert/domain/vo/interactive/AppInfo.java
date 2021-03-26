package com.miguan.advert.domain.vo.interactive;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("app信息")
@Data
public class AppInfo {

    @ApiModelProperty("app的id")
    private Integer id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("app_id")
    private Integer app_id;
    private Integer is_estimate;
    private Integer isolations_count;
    private Integer layers_count;
    private Integer exps_count;
}
