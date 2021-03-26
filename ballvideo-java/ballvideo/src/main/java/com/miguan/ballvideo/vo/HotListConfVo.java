package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 榜单banner配置Vo
 *
 * @author xy.chen
 * @date 2019-08-26
 **/
@Data
@ApiModel("榜单banner配置Vo")
public class HotListConfVo {
    @ApiModelProperty("图片")
    private String imgLink;
}
