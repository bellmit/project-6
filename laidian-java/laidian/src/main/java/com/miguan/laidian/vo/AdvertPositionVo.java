package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 广告位置VO 2.6及以上版本使用
 * @author laiyd
 * @date 2020/05/09
 **/
@ApiModel("广告位置VO(V2.6)")
@Data
public class AdvertPositionVo {

    @ApiModelProperty("广告位置ID")
    private Long positionId;

    @ApiModelProperty("广告位关键字")
    private String positionType;

    @ApiModelProperty("首次加载位置")
    private Integer firstLoadPosition;

    @ApiModelProperty("再次加载位置")
    private Integer secondLoadPosition;

    @ApiModelProperty("广告展示次数限制")
    private Integer maxShowNum;

}
