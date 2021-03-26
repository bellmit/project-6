package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@ApiModel("AB测试用户实体")
@Entity(name = "ab_test_user")
@Data
public class AbTestUser extends BaseInfoModel {

    @ApiModelProperty("AB测试配置ID")
    @Column(name ="ab_test_config_id")
    private Long abTestConfigId;

    @ApiModelProperty("设备ID")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty("A面用户/B面用户")
    @Column(name = "ab_user")
    private String abUser;
}
