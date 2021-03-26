package com.miguan.xuanyuan.dto.ab;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * ab分层
 */
@ApiModel("AB分层信息")
@Data
public class AbLayer {

    @ApiModelProperty("分层id")
    private Integer id;

    private String traffic;

    @ApiModelProperty("名称")
    private String name; //名称

    @ApiModelProperty("app_id")
    private Integer app_id;

    @ApiModelProperty("app_key")
    private String app_key;

    @ApiModelProperty("描述")
    private String description; //描述

    @ApiModelProperty("选择的标签")
    private List<Integer> tags; //选择的标签

    @ApiModelProperty("弹出框的标题")
    private String dialogTitle; //弹出框的标题

    public AbLayer() {
    }

    public AbLayer(Integer id,String name,String traffic) {
        this.id = id;
        this.traffic = traffic;
        this.name = name;
    }
}
