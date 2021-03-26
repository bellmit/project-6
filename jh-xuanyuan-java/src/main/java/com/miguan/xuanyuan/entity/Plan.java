package com.miguan.xuanyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 计划管理表
 * </p>
 *
 * @author zhangbinglin
 * @since 2021-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="XyPlan对象", description="计划管理表")
@TableName("xy_plan")
public class Plan extends Model<Plan> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "媒体账号id")
    private Integer userId;

    @ApiModelProperty(value = "计划名称")
    private String name;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "品牌logo")
    private String brandLogo;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间，为空给一个默认最大。")
    private Date endDate;

    @ApiModelProperty(value = "投放日期类型，0-长期投放，1-指定开始日期和结束日期")
    private Integer putTimeType;

    @ApiModelProperty(value = "投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段")
    private Integer timeSetting;

    @ApiModelProperty(value = "时间配置json[{week_day:1,start_hour:0,end_hour:47}]")
    private String timesConfig;

    @ApiModelProperty(value = "总预算（元）")
    private Double totalPrice;

    @ApiModelProperty(value = "广告类型，interaction:插屏, infoFlow:信息流广告, banner:Banner广告, open_screen:开屏广告")
    private String advertType;

    @ApiModelProperty(value = "1启用，0未启用")
    private Integer status;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
