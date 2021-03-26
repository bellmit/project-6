package com.miguan.ballvideo.vo.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("视频分享信息")
public class ShareInfoVo {

    @ApiModelProperty("图片路径")
    private String imgUrl;

    @ApiModelProperty("分享内容")
    private String shareContent;

    @ApiModelProperty("分享标题")
    private String shareTitle;

    @ApiModelProperty("分享url")
    private String shareUrl;

    @ApiModelProperty("分享功能开关：0:小程序分享 1:H5链接分享")
    private String shareSwitch;

    @ApiModelProperty("小程序userName")
    private String shareUserName;

    @ApiModelProperty("小程序分享好友url")
    private String shareWeChatUrl;

    @ApiModelProperty("小程序分享朋友圈url")
    private String wecharXcxShareUrl;

    @ApiModelProperty("H5分享剩余次数")
    private int shareH5Number;

    @ApiModelProperty("小程序分享剩余次数")
    private int shareWeChatNumber;

    @ApiModelProperty("万年历的剩余次数")
    private int shareCalendarNumber;
}
