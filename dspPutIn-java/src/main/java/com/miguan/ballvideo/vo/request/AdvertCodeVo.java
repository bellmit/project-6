package com.miguan.ballvideo.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 代码位
 **/
@ApiModel("代码位")
@Data
public class AdvertCodeVo {

    @ApiModelProperty("代码位ID")
    private Long id;

    @ApiModelProperty("代码位名称")
    private String name;

    @ApiModelProperty("样式类型")
    private String style_type;

    @ApiModelProperty("样式规格（这里冗余，方便前端查询）")
    private String style_size;

    @ApiModelProperty("eCPM")
    private String ecpm;

    @ApiModelProperty("0-关闭，1开启")
    private String state;

    @ApiModelProperty("应用ID")
    private Long app_id;

    @ApiModelProperty("同步配置平台关系ID")
    private Long rela_id;

    @ApiModelProperty("代码位ID")
    private Long code_id;

    @ApiModelProperty("广告类型")
    private String advert_type;

    @ApiModelProperty("创建日期")
    private Date created_at;
    @ApiModelProperty("更新日期")
    private Date updated_at;
}

