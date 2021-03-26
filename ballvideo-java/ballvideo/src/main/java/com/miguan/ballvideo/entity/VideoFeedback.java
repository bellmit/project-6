package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 视频反馈表
 **/
@Entity(name="video_feedback")
@Data
public class VideoFeedback {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("视频Id")
    private Integer videoId;

    @ApiModelProperty("反馈时间")
    private Date feedbackDate;

    @ApiModelProperty("联系方式")
    private String contactUs;

    @ApiModelProperty("反馈类型（多选的话逗号分隔）：1.播放问题,2.标题与内容不符,3.广告,4.低俗色情,5.过期旧闻,6.虚假谣言,7.违法反动,8.视频侵权")
    private String feedbackType;

    @ApiModelProperty("反馈明细: 1.持续加载,2.播放卡顿,3.播放失败,4.无视频封面,5.有声音无画面,6.有画面无声音")
    private Integer feedbackDetail;

    @ApiModelProperty("创建时间戳")
    private Long createTime;

    @ApiModelProperty("更新时间戳")
    private Long updateTime;
}
