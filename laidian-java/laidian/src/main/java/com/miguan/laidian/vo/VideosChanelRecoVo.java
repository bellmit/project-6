package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("渠道内容推荐表")
public class VideosChanelRecoVo {

    @ApiModelProperty("渠道名称")
    private String channelName;

    @ApiModelProperty("渠道ID")
    private String channelId;

    @ApiModelProperty("推荐分类（JSON格式）")
    private String recoJson;
}
