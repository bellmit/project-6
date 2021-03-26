package com.miguan.recommend.bo;

import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiModel("通用视频推荐请求参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormativeVideoRecommendDto extends NormativeCommonDto {

    @ApiModelProperty(required = true, value = "普通视频个数")
    private Integer hotspotNum;
    @ApiModelProperty(required = true, value = "激励视频个数")
    private Integer incentiveNum;


    @ApiModelProperty(value = "默认分类", hidden = true)
    private List<Integer> defaultCat;
    @ApiModelProperty(value = "用户兴趣分类", hidden = true)
    private List<Integer> userCat;
    @ApiModelProperty(value = "相似分类", hidden = true)
    private List<Integer> similarCat;
    @ApiModelProperty(value = "最终推荐的分类", hidden = true)
    private List<Integer> recommendCat;

    @ApiModelProperty(value = "激励视频集合", hidden = true)
    private List<IncentiveVideoHotspot> incetiveVideoList = new ArrayList<IncentiveVideoHotspot>();
    @ApiModelProperty(value = "分类热度视频集合", hidden = true)
    private Map<Integer, List<VideoHotspotVo>> hotspotVideoMap = new HashMap<Integer, List<VideoHotspotVo>>();
}
