package com.miguan.ballvideo.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("广告创意(多个)")
public class AdvertDesignModifyVo {

    @ApiModelProperty("计划组id")
    private Long group_id;
    @ApiModelProperty("广告计划id")
    private Long plan_id;
    @ApiModelProperty("修改时的广告创意id")
    private Long edit_design_id;
    @ApiModelProperty("素材类型，1:大图，2：视频")
    private Integer material_type;
    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private Integer material_shape;

    @ApiModelProperty("广告创意列表（多个）")
    private List<AdvertDesignVo> designList;
    @ApiModelProperty(value = "0-暂停,1-投放中", position = 240)
    private Integer state;
}
