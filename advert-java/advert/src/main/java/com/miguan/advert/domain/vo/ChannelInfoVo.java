package com.miguan.advert.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("魔方渠道信息")
@Data
public class ChannelInfoVo {

    @ApiModelProperty("渠道编码")
    private String channelId;

    @ApiModelProperty("渠道名称")
    private String channelName;
}
