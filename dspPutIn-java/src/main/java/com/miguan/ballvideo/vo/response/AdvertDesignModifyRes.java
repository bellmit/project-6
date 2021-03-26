package com.miguan.ballvideo.vo.response;

import com.miguan.ballvideo.vo.AdvertGroupVo;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import com.miguan.ballvideo.vo.request.AdvertPlanVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("广告创意(多个)查询时返回")
public class AdvertDesignModifyRes {

    @ApiModelProperty(value = "计划组id",hidden = true)
    private Long group_id;
    @ApiModelProperty("广告计划id")
    private Long plan_id;
    @ApiModelProperty("广告创意id")
    private Long edit_design_id;
    @ApiModelProperty(value = "计划组")
    private AdvertGroupVo groupVo;
    @ApiModelProperty("广告计划id")
    private AdvertPlanVo planVo;
    @ApiModelProperty("素材类型，1:图片，2：视频")
    private Integer material_type;
    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private Integer material_shape;

    @ApiModelProperty("广告创意列表（多个）")
    private List<AdvertDesignVo> designList;
}
