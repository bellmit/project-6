package com.miguan.ballvideo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

@Data
@ApiModel("发布视频对象")
@Entity(name = "first_videos")
public class FirstVideosDto {

    @ApiModelProperty(value = "包名", required = true)
    private String appPackage;

    @ApiModelProperty(value = "用户ID", required = true)
    private String userId;

    @ApiModelProperty(value = "作者")
    private String videoAuthor;

    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @ApiModelProperty(value = "简介")
    private String brief;

    @ApiModelProperty(value = "白山云视频地址, 当视频由客户端直接上传到白山云时必填")
    private String bsyUrl;

    @ApiModelProperty(value = "时长，格式--:--", required = true)
    private String videoTime;

    @ApiModelProperty(value = "大小, 单位MB", required = true)
    private String videoSize;

    @ApiModelProperty(value = "白山云音频地址")
    private String bsyAudioUrl;

    @ApiModelProperty(value = "白山云图片地址")
    private String bsyImgUrl;

    @ApiModelProperty(value = "白云山头像地址")
    private String bsyHeadUrl;

    @ApiModelProperty(hidden = true)
    private String localAudioUrl;
    @ApiModelProperty(hidden = true)
    private Integer updateType;
    @ApiModelProperty(hidden = true)
    private Integer catId;
    @ApiModelProperty(hidden = true)
    private Integer state;
    @ApiModelProperty(hidden = true)
    private Integer examState;
    @ApiModelProperty(hidden = true)
    private Integer loveCount;
    @ApiModelProperty(hidden = true)
    private Long watchCount;
    @ApiModelProperty(hidden = true)
    private Date createdAt;

}
