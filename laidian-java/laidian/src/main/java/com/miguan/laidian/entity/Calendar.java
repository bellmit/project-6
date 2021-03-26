package com.miguan.laidian.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "calendar")
@Data
@ApiModel("日历")
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty("日期")
    private String day;

    @ApiModelProperty("星期")
    private String week;

    @ApiModelProperty("节气")
    private String solarTerms;

    @ApiModelProperty("公历节日")
    private String gregorianFestival;

    @ApiModelProperty("农历节日")
    private String lunarFestival;

    @ApiModelProperty("特殊节日")
    private String specialFestival;
}
