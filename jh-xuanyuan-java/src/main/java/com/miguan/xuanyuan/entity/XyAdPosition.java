package com.miguan.xuanyuan.entity;

import com.miguan.xuanyuan.common.validate.annotation.BaseStatus;
import com.miguan.xuanyuan.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel("轩辕广告位表")
@Data
public class XyAdPosition extends BaseModel {
    @ApiModelProperty("媒体账号id")
    private Long userId;

    @ApiModelProperty("应用id")
    @NotNull(message = "所属应用不能为空！")
    private Long appId;

    @ApiModelProperty("广告样式")
    @NotBlank(message = "广告样式不能为空！")
    private String adType;

    @ApiModelProperty("广告位KEY")
    private String positionKey;

    @ApiModelProperty("广告位名称")
    @NotBlank(message = "广告位名称不能为空！")
    @Size(max = 50,message = "广告位名称不能超过50个字符！")
    private String positionName;

    @ApiModelProperty("同一个用户，1个小时内最多展示限制")
    @Size(max = 10,message = "展示上限(时)不能超过10位!")
    private Long showLimitHour;

    @ApiModelProperty("同一个用户，1天内最多展示限制")
    @Size(max = 10,message = "展示上限(天)不能超过10位!")
    private Long showLimitDay;

    @ApiModelProperty("同一个用户，前后两次请求广告的间隔秒数")
    @Size(max = 10,message = "展示间隔(秒)不能超过10位!")
    private Long showIntervalSec;

    @ApiModelProperty("备注")
    @Size(max = 500,message = "备注不能超过500个字符!")
    private String note;

    @ApiModelProperty("状态，1启用，0禁用")
    @NotNull(message = "状态不能为空！")
    @BaseStatus
    private Integer status;

    @ApiModelProperty("自定义字段")
    @Size(max = 250,message = "自定义字段总共不能超过250个字符!")
    private String customField;

    @ApiModelProperty("是否删除，0正常，1删除")
    private Integer isDel;

    @ApiModelProperty("自定义字段")
    private Integer shapeId;

}
