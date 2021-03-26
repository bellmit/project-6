package com.miguan.ballvideo.vo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import com.miguan.ballvideo.vo.request.AdvertPlanVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("广告创意Res")
public class AdvertDesignRes {

    @ApiModelProperty("广告创意id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty("计划创意名称")
    private String name;

    @ApiModelProperty("素材类型，1:图片，2：视频")
    private Integer material_type;

    @ApiModelProperty("素材图片地址")
    private String material_url;

    @ApiModelProperty("0-关闭，1开启")
    private Integer state;

    @ApiModelProperty("文案")
    private String copy;

    @ApiModelProperty("按钮文案")
    private String button_copy;

    @ApiModelProperty("logo_url")
    private String logo_url;

    @ApiModelProperty("产品名称")
    private String product_name;

    @ApiModelProperty("是否展示产品名称与品牌logo, 1:展示，-1:不展示")
    private Integer is_show_logo_product;

    @ApiModelProperty("投放方式，1:落地页链接，2：下载地址")
    private Integer put_in_method;

    @ApiModelProperty("投放方式相对应的值")
    private String put_in_value;

    @ApiModelProperty(value = "广告位类型", hidden=true)
    private String position_type;

    @JsonIgnore
    @ApiModelProperty(value = "计划组id", hidden=true)
    private Long group_id;

    @JsonIgnore
    @ApiModelProperty(value = "广告计划id", hidden=true)
    private Long plan_id;

    @ApiModelProperty(value = "计划组")
    private AdvertGroupVo groupVo;

    @ApiModelProperty(value = "广告计划")
    private AdvertPlanVo planVo;

    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;


}
