package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseRecommendDto {

    @ApiModelProperty(value = "最终需要返回的推荐视频ID集合", hidden = true)
    private List<String> recommendVideo;
    @ApiModelProperty(value = "最终需要返回的精选视频ID集合", hidden = true)
    private List<String> selectedVideo;
    @ApiModelProperty(value = "最终需要返回的激励视频ID", hidden = true)
    private List<String> jlvideo = new ArrayList<String>();
}
