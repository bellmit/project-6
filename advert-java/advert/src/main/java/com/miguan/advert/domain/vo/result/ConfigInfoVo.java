package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfigInfoVo{

    @ApiModelProperty("代码位主键Id")
    private Integer id;

    @ApiModelProperty("排序值或者概率值")
    private Integer option_value;

    @ApiModelProperty("配置ID")
    private Integer config_id;
}
