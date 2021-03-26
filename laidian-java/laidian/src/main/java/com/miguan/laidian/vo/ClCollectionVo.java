package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏信息表实体
 *
 * @Author xy.chen
 * @Date 2019/7/9
 **/
@Data
@ApiModel("收藏信息表实体")
public class ClCollectionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("视频ID")
    private String videosId;

    @ApiModelProperty("收藏类型 10 专属  20默认 30其他")
    private String type;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建时间")
    private Date updateTime;

}
