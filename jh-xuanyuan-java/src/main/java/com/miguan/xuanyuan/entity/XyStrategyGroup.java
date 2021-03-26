package com.miguan.xuanyuan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@ApiModel("广告位策略分组")
@Data
public class XyStrategyGroup extends BaseModel {

    @ApiModelProperty("广告位id")
    private Long positionId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("ab实验id")
    private Long abId;

    @ApiModelProperty("ab实验标识")
    private String abExpCode;

    @ApiModelProperty("自定义开关")
    private Integer customSwitch;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    @Column(name = "begin_time")
    private String beginTime;

    @ApiModelProperty("状态，1启用，0禁用")
    private Integer status = 1;

    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel = 0;
}
