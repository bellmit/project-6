package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(value = "分类视频推荐请求参数")
public class CatRecommendDto extends BaseRecommendDto{

    @ApiModelProperty(value = "请求头：publicInfo")
    public String publicInfo;
    @ApiModelProperty(value = "分类ID", required = true)
    private Integer catid;
    @ApiModelProperty(value = "视频数量", required = true)
    private Integer num;
    @ApiModelProperty(value = "敏感词开关 1开 0关", required = true)
    private Integer sensitiveState;
}
