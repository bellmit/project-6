package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel("广告配置与代码位关联结果(迁移前)")
@Data
public class AdvertConfigAndCodeVo {
    @ApiModelProperty("主键Id")
    private Long id;

    @ApiModelProperty("创建时间")
    private Date createdAt;

    @ApiModelProperty("算法：1-概率补充；2-手动排序")
    private Integer computer;

    @ApiModelProperty("广告位置ID")
    private String positionId;

    @ApiModelProperty("0-关闭，1开启")
    private Integer state;

    @ApiModelProperty("关联主键Id")
    private String configId;

    @ApiModelProperty("排序值或者概率值")
    private Integer optionValue;

    @ApiModelProperty("代码位id")
    private Integer codeId;
}
