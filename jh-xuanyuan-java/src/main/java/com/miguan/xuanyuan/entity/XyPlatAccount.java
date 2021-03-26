package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class XyPlatAccount extends BaseModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long platId;

//    private String codeName;

    private String accountName;

    private Integer openReportapi;

    private String appId;

    private String appSecret;

    private Integer isDel;
}
