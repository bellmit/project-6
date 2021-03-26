package com.miguan.bigdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自动推送结果表
 */
@Data
public class PushResultVo {

    @ApiModelProperty(value = "app包名", position = 5)
    private String packageName;

    @ApiModelProperty(value = "用户标识", position = 10)
    private String distinctId;

    @ApiModelProperty(value = "视频id", position = 20)
    private Integer videoId;
}
