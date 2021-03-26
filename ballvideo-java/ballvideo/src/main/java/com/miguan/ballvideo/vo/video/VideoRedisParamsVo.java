package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 视频不常操作字段
 * @Author shixh
 * @Date 2020/3/2
 **/
@Data
public class VideoRedisParamsVo {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("源图片url")
    private String urlImg;

    @ApiModelProperty("白山云视频地址")
    private String bsyUrl;

    @ApiModelProperty("创建时间")
    private String createdAt;

    @ApiModelProperty("白山云图片地址")
    private String bsyImgUrl;

    @ApiModelProperty("白云山头像地址")
    private String bsyHeadUrl;

    @ApiModelProperty("视频作者")
    private String videoAuthor;

    @ApiModelProperty("时长")
    private String videoTime;

    @ApiModelProperty("状态 1开启 2关闭 0-下线")
    private Integer state;

}
