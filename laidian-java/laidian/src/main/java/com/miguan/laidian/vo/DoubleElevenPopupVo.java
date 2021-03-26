package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author zhangbinglin
 * @Date 2020/10/23 18:30
 **/
@Data
@ApiModel("双十一淘宝红包和淘口令DTO")
public class DoubleElevenPopupVo {

    @ApiModelProperty("红包大图链接")
    private String bigImageUrl;

    @ApiModelProperty("红包缩小后的图片链接")
    private String smallImageUrl;

    @ApiModelProperty("淘宝的deeplink")
    private String deeplink;

    @ApiModelProperty("淘口令红包剪贴板串")
    private String taobaoPassword;

    @ApiModelProperty("红包弹窗单日单用户出现次数上限")
    private Integer maxCount;
}
