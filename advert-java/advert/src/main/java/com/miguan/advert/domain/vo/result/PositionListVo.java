package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PositionListVo extends PageResultVo{

    @ApiModelProperty("广告配置列表信息")
    private List<PositionDetailInfoVo> data;
}
