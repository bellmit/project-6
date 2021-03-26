package com.miguan.laidian.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("视频设置记录")
public class VideoSettingVo {

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("历史记录详情列表")
    private List<VideoSettingDetailVo> videoSettingDetailVoList;
}
