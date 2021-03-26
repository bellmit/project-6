package com.miguan.recommend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分类视频推荐返回参数")
public class CatRecommendVo {

    @ApiModelProperty("热度视频列表")
    private List<String> video;
    @ApiModelProperty("激励视频列表")
    private List<String> incentiveVideo;

    public CatRecommendVo(List<String> video){
        this.video = video;
    }
}
