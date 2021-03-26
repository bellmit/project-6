package com.miguan.ballvideo.vo.response;

import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("广告计划列表")
public class AdvertPlanListRes {
    @ApiModelProperty("广告计划id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty("计划名称")
    private String name;

    @ApiModelProperty("0-暂停,1-投放中")
    private Integer state;

    @ApiModelProperty("消耗")
    private Double consume;

    @ApiModelProperty("出价（元）")
    private Double price;

    @ApiModelProperty("计划预算 日预算（元） || 日预算")
    private Double plan_price;

    @ApiModelProperty("展示数")
    private Integer exposure;

    @ApiModelProperty("千次展示均价 消耗/展示数 * 1000")
    private Double exposure_price;

    @ApiModelProperty("点击数")
    private Integer valid_click;

    @ApiModelProperty("点击用户数")
    private Integer click_user;

    @ApiModelProperty("点击率  点击数/展示数")
    private Double pre_click_rate;

    @ApiModelProperty("投放时间")
    private String put_time;

    @ApiModelProperty("计划组名称")
    private String group_name;

    @ApiModelProperty(value = "创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9",hidden = true)
    private Integer material_shape_tmp;

    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private String material_shape;

    @ApiModelProperty("投放类型")
    private String put_in_type;

    @ApiModelProperty("投放类型值")
    private String put_in_value;

    @ApiModelProperty("计划组状态,0-暂停,1-投放中")
    private Integer group_state;

    public String getMaterial_shape() {
        if(material_shape_tmp == null){
            return "";
        }
        return material_shape = MaterialShapeConstants.materialShapeNameMap.get(material_shape_tmp);
    }
}
