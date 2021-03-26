package com.miguan.ballvideo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.ballvideo.entity.common.BaseInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel("AB测试配置实体")
@Entity(name = "ab_test_config")
@Data
public class AbTestConfig extends BaseInfoModel {

    @ApiModelProperty("配置名称")
    @Column(name = "title")
    private String title;

    @ApiModelProperty("参数")
    @Column(name ="name")
    private String name;

    @ApiModelProperty("灰度开始时间")
    @JsonFormat
    @Column(name = "gray_begin_time")
    private Date grayBeginTime;

    @ApiModelProperty("灰度结束时间")
    @JsonFormat
    @Column(name = "gray_end_time")
    private Date grayEndTime;

    @ApiModelProperty("渠道ID")
    @Column(name = "channel_id")
    private String channelId;

    @ApiModelProperty("B面用户量上限")
    @Column(name = "total_b_user_num")
    private Long totalBUserNum;

    @ApiModelProperty("B面已灰度用户量")
    @Column(name = "b_user_num")
    private Long bUserNum;

    @ApiModelProperty("B面用户占比")
    @Column(name = "b_user_percentage")
    private BigDecimal bUserPercentage;

    @ApiModelProperty("状态 1开启 0关闭")
    @Column(name = "state")
    private Integer state;
}
