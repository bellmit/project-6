package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "cl_interest_label")
@Data
public class ClInterestLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标签ID,逗号隔开")
    private String labelId;

    @ApiModelProperty("标签名称,逗号隔开")
    private String labelName;

    @ApiModelProperty("对应分类ID")
    private String catId;

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("uuid")
    private String uuid;

    @ApiModelProperty("创建时间")
    private Date createdAt;
}
