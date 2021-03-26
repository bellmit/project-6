package com.miguan.recommend.vo;

import com.miguan.recommend.bo.VideoRecommendDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("视频推荐返回参数")
@AllArgsConstructor
public class VideoRecommendVo {

    @ApiModelProperty("热度视频列表")
    private List<String> video;
    @ApiModelProperty("精选视频列表")
    private List<String> selectedVideo;
    @ApiModelProperty("激励视频列表")
    private List<String> incentiveVideo;

    public VideoRecommendVo(List<String> video){
        this.video = video;
    }

    public VideoRecommendVo(VideoRecommendDto recommendDto) {
        this.video = recommendDto.getRecommendVideo();
        this.selectedVideo = recommendDto.getSelectedVideo();
        this.incentiveVideo = recommendDto.getJlvideo();
    }
}
