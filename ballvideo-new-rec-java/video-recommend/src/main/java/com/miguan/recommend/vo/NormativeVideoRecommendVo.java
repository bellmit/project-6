package com.miguan.recommend.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("通用视频推荐返回参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormativeVideoRecommendVo {
    @ApiModelProperty("激励视频列表")
    private List<String> incentiveVideo;
    @ApiModelProperty("热度视频列表")
    private List<String> hotspotVideo;
}
