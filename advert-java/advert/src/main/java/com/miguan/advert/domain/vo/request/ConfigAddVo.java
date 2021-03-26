package com.miguan.advert.domain.vo.request;

import com.miguan.advert.domain.vo.result.PositionInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ConfigAddVo {

    @ApiModelProperty("代码位列表信息")
    private List<PositionInfoVo> data;

    @ApiModelProperty("算法：1-概率补充；2-手动排序")
    private Integer computer;

    @ApiModelProperty("配置Id")
    private Integer config_id;
}
