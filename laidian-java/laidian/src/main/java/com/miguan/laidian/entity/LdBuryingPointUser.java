package com.miguan.laidian.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "ld_burying_point_user")
@Data
@ApiModel("判断新老用户埋点类")
public class LdBuryingPointUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty("设备id")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty("APP类型 xld炫来电  wld微来电")
    @Column(name = "app_type")
    private String appType;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    private Date createTime;

}