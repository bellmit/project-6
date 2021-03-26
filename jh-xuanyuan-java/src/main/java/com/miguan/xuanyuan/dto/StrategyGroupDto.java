package com.miguan.xuanyuan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

import java.time.LocalDateTime;

@ApiModel("策略分组")
@Data
public class StrategyGroupDto {

    @ApiModelProperty("分组主键id")
    private Long id;

    @ApiModelProperty("广告位id")
    private Integer positionId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("ab实验id")
    private Integer abId;

    @ApiModelProperty("ab实验标识")
    private String abExpCode;

    @ApiModelProperty("运行状态")
    private Integer status;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "begin_time")
    private String beginTime;



}
