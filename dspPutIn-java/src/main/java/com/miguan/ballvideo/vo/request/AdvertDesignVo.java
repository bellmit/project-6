package com.miguan.ballvideo.vo.request;

import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.ValidatorUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("广告创意")
public class AdvertDesignVo {

    @ApiModelProperty(value = "广告创意id",position = 10)
    private Long id;

    @ApiModelProperty(value = "广告主id", hidden=true,position = 20)
    private Long advert_user_id;

    @ApiModelProperty(value = "素材类型，1:图片，2：视频",position = 30)
    private Integer material_type;

    @ApiModelProperty(value = "素材图片地址",position = 40)
    private String material_url;

    @ApiModelProperty(value = "文案",position = 50)
    private String copy;

    @ApiModelProperty(value = "按钮文案",position = 60)
    private String button_copy;

    @ApiModelProperty(value = "是否展示产品名称与品牌logo, 1:展示，-1:不展示",position = 70)
    private Integer is_show_logo_product;

    @ApiModelProperty(value = "产品名称",position = 80)
    private String product_name;

    @ApiModelProperty(value = "logo_url",position = 90)
    private String logo_url;

    @ApiModelProperty(value = "投放方式，1:落地页链接，2：下载地址",position = 100)
    private Integer put_in_method;

    @ApiModelProperty(value = "投放方式相对应的值",position = 110)
    private String put_in_value;

    @ApiModelProperty(value = "计划创意名称",position = 120)
    private String name;

    @ApiModelProperty(value = "0-关闭，1开启",position = 130)
    private Integer state;

    @ApiModelProperty(value = "广告位类型", hidden=true)
    private String position_type;

    @ApiModelProperty(value = "计划组id", hidden=true)
    private Long group_id;

    @ApiModelProperty(value = "广告计划id", hidden=true)
    private Long plan_id;

    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;


    public void validate() throws ValidateException {
        // todo advert_user_id 校验还原
        ValidatorUtil.checkRequestMaxStr(name,"广告创意名称",30);
        ValidatorUtil.checkMaxStr(copy,"广告创意文案",125);
        ValidatorUtil.checkRequest(material_url,"创意图片或视频");
        ValidatorUtil.checkMaxStr(logo_url,"logo图片地址",100);
        ValidatorUtil.checkMaxStr(product_name,"广告创意的产品名称",50);
        ValidatorUtil.checkRequestUrl(put_in_value,"落地页");
    }

    public void init() {
        if(state == null){
            state = 1;
        }
        if(is_show_logo_product == null){
            is_show_logo_product = -1;
        }
        //todo 如下字段会废弃掉。等废弃了再删除
        if(advert_user_id == null){
            advert_user_id = -1L;
        }
    }
}
