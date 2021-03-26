package com.miguan.laidian.entity;

import io.swagger.annotations.*;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Entity(name = "act_user_prize_exchange_record")
@ApiModel("用户奖品兑换记录表")
public class ActUserPrizeExchangeRecord {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    @Column(name = "user_id")
    private Long userId;

    @ApiModelProperty(value = "活动id")
    @Column(name = "activity_id")
    private Long activityId;

    @ApiModelProperty(value = "奖品配置id")
    @Column(name = "activity_config_id")
    private Long activityConfigId;

    @ApiModelProperty(value = "联系信息(手机或qq号码)")
    @Column(name = "contast_info")
    private String contastInfo;

    @ApiModelProperty(value = "是否已发放 0否 1是")
    @Column(name = "state")
    private Integer state;

    @ApiModelProperty(value = "设备id")
    @Column(name = "device_id")
    private String deviceId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at")
    private Date updatedAt;

    @ApiModelProperty(value = "收货人姓名")
    @Column(name = "rcvr_name")
    private String rcvrName;

    @ApiModelProperty(value = "收货人地址")
    @Column(name = "rcvr_addr")
    private String rcvrAddr;

    @ApiModelProperty(value = "收货人手机号")
    @Column(name = "rcvr_phone")
    private String rcvrPhone;
}