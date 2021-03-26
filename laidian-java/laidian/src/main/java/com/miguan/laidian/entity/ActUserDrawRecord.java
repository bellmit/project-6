package com.miguan.laidian.entity;

import io.swagger.annotations.*;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity(name = "act_user_draw_record")
@ApiModel("用户抽奖记录表")
public class ActUserDrawRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "设备id")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty(value = "活动id")
    @Column(name = "activity_id")
    private Long activityId;

    @ApiModelProperty(value = "奖品配置id")
    @Column(name = "activity_config_id")
    private Long activityConfigId;

    @ApiModelProperty(value = "是否已领取 0否 1是")
    @Column(name = "state")
    private Integer state;

    @ApiModelProperty(value = "是否已兑换 0否 1是")
    @Column(name = "is_exchange")
    private Integer isExchange;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Date updatedAt;
}