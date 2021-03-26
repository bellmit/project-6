package com.miguan.ballvideo.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("推广-广告计划列表")
public class AdvertPlanListExt {
    @ApiModelProperty("广告计划id")
    private Long id;

    @ApiModelProperty("计划名称")
    private String name;

    @ApiModelProperty("广告主名称")
    private String advert_user_name;

    @ApiModelProperty("0-暂停,1-投放中")
    private Integer state;

    @ApiModelProperty("素材类型，1:图片，2：视频")
    private Integer material_type;

    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private Integer material_shape;

    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private String material_shape_name;

    @ApiModelProperty("指定投放广告名称")
    private String point_adv_names;

}
