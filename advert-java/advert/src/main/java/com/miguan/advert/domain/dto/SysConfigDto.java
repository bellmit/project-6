package com.miguan.advert.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 系统参数配置dto
 * @Author zhangbinglin
 * @Date 2020/11/10 18:19
 **/
@Data
public class SysConfigDto {

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("类型,10-开关类型，20--业务参数，30--第三方配置")
    private Integer type;

    @ApiModelProperty("参数名称")
    private String name;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("参数对应的值")
    private String value;

    @ApiModelProperty("状态  -1不启用  1启用")
    private Integer status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}
