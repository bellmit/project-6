package com.miguan.bigdata.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 代码位展示量
 * @Author zhangbinglin
 * @Date 2020/11/12 9:17
 **/
@Data
public class AdIdShowDto {

    @ApiModelProperty("代码位")
    private String adId;

    @ApiModelProperty("展示量")
    private Integer show;
}
