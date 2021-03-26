package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "cl_collection")
@Data
public class ClCollection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
