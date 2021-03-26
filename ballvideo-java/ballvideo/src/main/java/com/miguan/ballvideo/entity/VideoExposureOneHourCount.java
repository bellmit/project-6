package com.miguan.ballvideo.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


/**
 * @author laiyd
 */
@Entity(name="video_exposure_one_hour_count")
@Data
@ApiModel("曝光视频统计1小时")
public class VideoExposureOneHourCount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("视频Id")
    private Long videoId;

    @ApiModelProperty("曝光次数")
    private Long count;

    @ApiModelProperty("创建时间")
    private Long createTime;
}
