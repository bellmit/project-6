package com.miguan.recommend.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("视频推荐请求参数")
public class VideoRecommendDto extends BaseRecommendDto {
    @ApiModelProperty(value = "请求头：publicInfo")
    public String publicInfo;
    @ApiModelProperty(value = "旧设备标识DeviceId")
    private String deviceId;
    @ApiModelProperty(value = "渠道默认分类, 多个逗号间隔", required = true)
    private String defaultCat;
    @ApiModelProperty(value = "需要屏蔽的分类, 多个逗号间隔", required = true)
    private String excludeCat;
    @ApiModelProperty(value = "热度视频个数", required = true)
    private Integer videoNum;
    @ApiModelProperty(value = "激励视频个数", required = true)
    private Integer incentiveVideoNum;
    @ApiModelProperty(value = "敏感词开关 1开 0关", required = true)
    private Integer sensitiveState;

    @ApiModelProperty(value = "渠道默认分类集合", hidden = true)
    private List<Integer> defaultCatList;
    @ApiModelProperty(value = "用户选择的标签分类", hidden = true)
    private List<Integer> userChooseCatList;
    @ApiModelProperty(value = "需要屏蔽的分类集合", hidden = true)
    private List<Integer> excludeCatList;
    @ApiModelProperty(value = "用户的兴趣分类", hidden = true)
    private List<Integer> userCats;
    @ApiModelProperty(value = "用户的离线兴趣分类", hidden = true)
    private List<Integer> userOfflineCats;
    @ApiModelProperty(value = "与用户的第1个兴趣分类，相似的热度分类", hidden = true)
    private List<Integer> similarCats;
    @ApiModelProperty(value = "推荐的视频分类集合", hidden = true)
    private List<Integer> recommendCat;
    @ApiModelProperty(value = "推荐的视频分类,以及分类视频的个数集合", hidden = true)
    private Map<Integer, Integer> recommendCatNumMap;
    @ApiModelProperty(value = "相关性分类", hidden = true)
    private List<Integer> relevantCats;
    @ApiModelProperty(value = "随机分类", hidden = true)
    private List<Integer> randomCats;
    @ApiModelProperty(value = "根据用户的兴趣分类，查询到的视频ID", hidden = true)
    private Map<Integer, List<String>> userCatVideoMap = new HashMap<Integer, List<String>>();
    @ApiModelProperty(value = "相似的热度分类视频ID", hidden = true)
    private List<String> similarvideo;
    @ApiModelProperty(value = "查询到的离线视频ID", hidden = true)
    private List<String> offLinevideo;
    @ApiModelProperty(value = "最终需要返回的激励视频ID、分类", hidden = true)
    private Map<String, Integer> jlvideoCat;
    @ApiModelProperty(value = "最终需要返回的推荐视频ID、分类", hidden = true)
    private Map<String, Integer> recommendVideoCat;
}
