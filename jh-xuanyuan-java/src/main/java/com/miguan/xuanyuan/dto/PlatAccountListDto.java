package com.miguan.xuanyuan.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 广告平台账号表
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-26
 */
@Data
@ApiModel(value="广告平台账号", description="广告平台账号")
public class PlatAccountListDto {

    @ApiModelProperty(value = "主键id", position = 10)
    private Long id;

    @ApiModelProperty(value = "用户账号", position = 15)
    private String username;

    @ApiModelProperty(value = "广告平台", position = 30)
    private String platName;

    @ApiModelProperty(value = "账号名称", position = 40)
    private String accountName;

    @ApiModelProperty(value = "是否开启三方包名api，1开通，0未开通", position = 50)
    private Integer openReportapi;


}
