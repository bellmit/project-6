package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 视频曝光数统计表
 * @Author laiyd
 * @Date 2020/6/3
 **/
@Entity(name="first_video_extend")
@Data
public class FirstVideoExtend extends BaseModel{
    @ApiModelProperty("视频Id")
    private String videoId;

    @ApiModelProperty("操作具体业务")
    private int total_click_rate;//1-开启 0-关闭
    private int baseWeight;//权重
    private String editor;//操作者
}
