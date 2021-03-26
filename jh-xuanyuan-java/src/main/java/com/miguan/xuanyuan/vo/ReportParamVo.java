package com.miguan.xuanyuan.vo;

import com.miguan.xuanyuan.common.constant.XyConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @Description 报表入参vo
 * @Author zhangbinglin
 * @Date 2020/11/24 13:54
 **/
@Data
public class ReportParamVo {

    @ApiModelProperty("计划ID或创意ID")
    private Integer id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户id")
    private List<Long> ids;

    @ApiModelProperty("类型：1--计划，2--创意")
    @NotNull(message = "必须传入查询类型")
    @Min(value = 1,message = "查询类型不能小于1")
    @Max(value = 2,message = "查询类型不能超过2")
    private Integer type;

    @ApiModelProperty("曝光量;大于格式：>100、小于格式：<100、介于格式：50-100")
    @Pattern(message = "曝光量格式不正确！" , regexp = "^((>[0-9]+)|(<[0-9]+)|([0-9]+-[0-9]+))$")
    private String showNum;

    @ApiModelProperty("点击量;大于格式：>100、小于格式：<100、介于格式：50-100")
    @Pattern(message = "点击量格式不正确！" , regexp = "^((>[0-9]+)|(<[0-9]+)|([0-9]+-[0-9]+))$")
    private String clickNum;

    @ApiModelProperty("开始日期，格式：yyyy-MM-dd")
    @Pattern(message = "开始日期格式不正确！" , regexp = "^([0-9]{4}-[0-9]{2}-[0-9]{2})$")
    private String startDay;

    @ApiModelProperty("结束日期，格式：yyyy-MM-dd")
    @Pattern(message = "开始日期格式不正确！" , regexp = "^([0-9]{4}-[0-9]{2}-[0-9]{2})$")
    private String endDay;

    @ApiModelProperty(value = "排序字段，字段+排序方式，例如：showNum asc(曝光量按升序)，showNum desc(曝光量按降序)")
    @Pattern(message = "排序字段格式不正确！" , regexp = "^([a-zA-Z]{0,10}\\s(asc|desc))$")
    private String sort;

    @ApiModelProperty(value = "折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", hidden = true)
    private Integer lineType;

    @ApiModelProperty(value = "折线类型:1-点击量,2-曝光量,3-曝光用户量,4-点击用户,5-点击率", hidden = true)
    private int noDesign = XyConstant.REPORT_NO_DESIGN;

}
