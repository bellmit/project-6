package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户发布视频返回实体")
public class VideoPublicationVo {

    @ApiModelProperty("视频上传到白山云的链接地址")
    private String bsyUrl;

    @ApiModelProperty("视频图片上传到白山云的链接地址")
    private String bsyImgUrl;
}
