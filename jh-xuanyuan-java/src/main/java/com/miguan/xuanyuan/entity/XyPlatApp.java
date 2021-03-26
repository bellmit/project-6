package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("第三方平台app关联表")
@Data
public class XyPlatApp extends BaseModel {
    @ApiModelProperty("应用的id")
    private Long appId;
    @ApiModelProperty("参数key")
    private String csjAppId;
    @ApiModelProperty("参数value")
    private String gdtAppId;
    @ApiModelProperty("排序编号")
    private String ksAppId;
    @ApiModelProperty("是否删除，1删除，0正常")
    private Integer isDel;
}
