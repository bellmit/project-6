package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 弹窗配置的配置明细dto
 * @Author suhj
 * @Date 2020/09/04 11:41
 **/
@Data
@ApiModel("弹窗配置的配置明细dto")
public class PopConfDetailDto {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "pop_conf表的id")
    private Long popId;

    @ApiModelProperty(value = "1h5,2短视频，3小视频，4启动app")
    private int jump_type;

    @ApiModelProperty(value = "图片")
    private String img;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "跳转连接")
    private String jump_link;
}
