package com.miguan.reportview.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Setter
@Getter
@ApiModel
public class UserKeepStrDto {

    @Excel(name = "日期")
    private String date;

    /**
     * 应用包名，不使用该维度使用null
     */
    @Excel(name = "应用")
    private String packageName;

    /**
     * 应用版本，不使用该维度使用null
     */
    @Excel(name = "版本")
    private String appVersion;

    @Excel(name = "父渠道")
    private String fatherChannel;
    /**
     * 更新渠道，不使用该维度使用null
     */
    @Excel(name = "渠道")
    private String changeChannel;


    /**
     * 用户数
     */
    @ApiModelProperty("新增用户/活跃用户")
    @Excel(name = "新增用户/活跃用户", orderNum = "1")
    private String user;

    /**
     * 次日留存
     */
    @Excel(name = "次日留存", orderNum = "2")
    private String keep1;

    /**
     * 2日留存
     */
    @Excel(name = "2日留存", orderNum = "3")
    private String keep2;

    /**
     * 3日留存
     */
    @Excel(name = "3日留存", orderNum = "4")
    private String keep3;

    /**
     * 4日留存
     */
    @Excel(name = "4日留存", orderNum = "4")
    private String keep4;

    /**
     * 5日留存
     */
    @Excel(name = "5日留存", orderNum = "5")
    private String keep5;

    /**
     * 6日留存
     */
    @Excel(name = "6日留存", orderNum = "6")
    private String keep6;

    /**
     * 7日留存
     */
    @Excel(name = "7日留存", orderNum = "7")
    private String keep7;

    /**
     * 14日留存
     */
    @Excel(name = "14日留存", orderNum = "8")
    private String keep14;

    /**
     * 30日留存
     */
    @Excel(name = "30日留存", orderNum = "8")
    private String keep30;
}
