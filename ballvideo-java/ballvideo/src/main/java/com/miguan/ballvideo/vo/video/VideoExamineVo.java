package com.miguan.ballvideo.vo.video;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author daoyu
 * @Date 2020/6/2
 **/

@Entity(name="video_examine")
@Data
public class VideoExamineVo {

    @Id
    @ApiModelProperty("申请id")
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("应用包名")
    private String appPackage;

    @ApiModelProperty("视频id")
    private Integer videoId;

    @ApiModelProperty("消息模板id")
    private Integer messageModelId;

    @ApiModelProperty("审核状态:1=通过,-1=不通过")
    private Integer examineStatus;

    @ApiModelProperty("新增时间")
    private Date createdTime;

    @ApiModelProperty("更新时间")
    private Date updatedTime;
}
