package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("即时相关推荐请求参数")
public class VideoRelavantRecommendDto {

    @ApiModelProperty(value = "请求头：publicInfo")
    public String publicInfo;
    @ApiModelProperty(value = "视频ID", required = true)
    private String videoId;
    @ApiModelProperty(value = "视频数量", required = true)
    private Integer num;
    @ApiModelProperty(value = "敏感词开关 1开 0关", required = true)
    private Integer sensitiveState;

    @ApiModelProperty(value = "最终需要返回的推荐视频ID集合", hidden = true)
    private List<String> recommendVideo;
}
