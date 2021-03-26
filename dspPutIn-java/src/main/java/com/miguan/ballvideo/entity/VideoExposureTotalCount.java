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
@Entity(name="video_exposure_total_count")
@Data
@ApiModel("曝光视频统计总数")
public class VideoExposureTotalCount {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("视频Id")
    private Long videoId;

    @ApiModelProperty("曝光总次数")
    private Long totalCount;
}
