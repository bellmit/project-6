package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CodeListVo{

    @ApiModelProperty("代码位列表信息")
    private List<PositionInfoVo> code_list;

    @ApiModelProperty("配置Id")
    private Integer config_id;
}
