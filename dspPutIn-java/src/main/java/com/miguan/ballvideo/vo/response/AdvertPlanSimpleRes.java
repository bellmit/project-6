package com.miguan.ballvideo.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("简单广告计划")
public class AdvertPlanSimpleRes {
    @ApiModelProperty("广告计划id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty("计划名称")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("开始时间")
    private Date start_date;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("结束时间，为空给一个默认最大。2030-01-01 00:00:00")
    private Date end_date;

    @ApiModelProperty("出价方式：1-CPC")
    private Integer price_method;

    @ApiModelProperty("出价（元）")
    private BigDecimal price;

    @ApiModelProperty("广告日预算")
    private BigDecimal day_price;

    @ApiModelProperty("投放类型：1：标准投放,2：快速投放")
    private Integer put_in_type;

    @ApiModelProperty("素材类型，1:图片，2：视频")
    private Integer material_type;

    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private Integer material_shape;

    @ApiModelProperty("状态 1开启 0关闭")
    private Integer state;
}
