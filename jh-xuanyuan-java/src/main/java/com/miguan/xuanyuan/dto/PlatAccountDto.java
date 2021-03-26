package com.miguan.xuanyuan.dto;

import com.miguan.xuanyuan.service.XyPlatAccountService;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 广告平台账号表
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-02-26
 */
@Data
@ApiModel(value="广告平台账号参数", description="广告平台账号参数")
public class PlatAccountDto {

    @ApiModelProperty(value = "主键id", position = 10)
    private Long id;

    @ApiModelProperty(value = "用户id(后台系统必填)", position = 15)
    @NotNull(message = "媒体账号不能为空", groups={XyPlatAccountService.class})
    private Long userId;

    @ApiModelProperty(value = "广告平台id", position = 30)
    @NotNull(message = "平台名称不能为空", groups={XyPlatAccountService.class})
    private Long platId;

    @ApiModelProperty(value = "账号名称", position = 40)
    @NotBlank(message = "账号名称不能为空")
    private String accountName;

    @ApiModelProperty(value = "是否开启三方包名api，1开通，0未开通", position = 50)
    private Integer openReportapi;

    @ApiModelProperty(value = "第三方平台id", position = 60)
    private String appId;

    @ApiModelProperty(value = "第三方secret key", position = 70)
    private String appSecret;

    @ApiModelProperty(value = "状态，1禁用，0启用", position = 80)
    private Integer isDel = 0;

}
