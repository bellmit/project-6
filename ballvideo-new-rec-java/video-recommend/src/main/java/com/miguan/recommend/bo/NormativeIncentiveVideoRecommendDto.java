package com.miguan.recommend.bo;

import com.miguan.recommend.entity.mongo.IncentiveVideoHotspot;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@ApiModel("通用激励视频推荐请求参数实体")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NormativeIncentiveVideoRecommendDto extends NormativeCommonDto {

    @ApiModelProperty(required = true, value = "普通视频个数")
    private Integer hotspotNum;
    @ApiModelProperty(required = true, value = "激励视频个数")
    private Integer incentiveNum;

    @ApiModelProperty(value = "激励视频集合", hidden = true)
    private List<IncentiveVideoHotspot> incetiveVideoList = new ArrayList<IncentiveVideoHotspot>();
}
