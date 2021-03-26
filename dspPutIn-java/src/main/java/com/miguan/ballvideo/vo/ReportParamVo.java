package com.miguan.ballvideo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 报表入参vo
 * @Author zhangbinglin
 * @Date 2020/11/24 13:54
 **/
@Data
public class ReportParamVo {

    @ApiModelProperty("类型：1--计划组，2--计划，3--创意")
    private Integer type;

    @ApiModelProperty("计划组ID或计划ID或创意ID")
    private Integer id;

    @ApiModelProperty("推广目的：1-应用推广，2-品牌推广")
    private Integer promotionPurpose;

    @ApiModelProperty("花费;大于格式：>100、小于格式：<100、介于格式：50-100")
    private String spend;

    @ApiModelProperty("曝光量;大于格式：>100、小于格式：<100、介于格式：50-100")
    private String exposure;

    @ApiModelProperty("点击量;大于格式：>100、小于格式：<100、介于格式：50-100")
    private String validClick;

    @ApiModelProperty("开始日期，格式：yyyy-MM-dd")
    private String startDay;

    @ApiModelProperty("结束日期，格式：yyyy-MM-dd")
    private String endDay;

    @ApiModelProperty(value = "排序字段，字段+排序方式，例如：exposure asc(曝光量按升序)，exposure desc(曝光量按降序)")
    private String sort;

    @ApiModelProperty(value = "折线类型，1-曝光量，2-点击量，3-点击率，4-点击均价，5-花费，6-千次展示均价", hidden = true)
    private Integer lineType;

}
