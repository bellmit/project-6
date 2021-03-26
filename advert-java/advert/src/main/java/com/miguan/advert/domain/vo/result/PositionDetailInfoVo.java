package com.miguan.advert.domain.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("广告位置信息")
@Data
public class PositionDetailInfoVo {

    @ApiModelProperty("广告配置Id")
    private Integer id;

    @ApiModelProperty("广告位Id")
    private Integer position_id;

    @ApiModelProperty("应用名称")
    private String app_name;

    @ApiModelProperty("包名")
    private String app_package;

    @ApiModelProperty("算法：1-概率补充；2-手动排序")
    private Integer computer;

    @ApiModelProperty("算法名称")
    private String computer_name;

    @ApiModelProperty("首次加载")
    private String first_load_position;

    @ApiModelProperty("再次加载")
    private String second_load_position;

    @ApiModelProperty("配置组：1：单套配置，2：多套配置")
    private Integer group_number;

    @ApiModelProperty("是否是测量模式：0：否，1：是")
    private Integer is_site;

    @ApiModelProperty("手机类型:ios-ios，ANDROID-安卓")
    private String mobile_type;

    @ApiModelProperty("广告位名称")
    private String position_name;

    @ApiModelProperty("状态，0：关闭，1：启用")
    private Integer state;

    @ApiModelProperty("创建时间")
    private String created_at;

    @ApiModelProperty("更新时间")
    private String updated_at;

    @ApiModelProperty("广告相关信息")
    List<TypeInfoVo> type_data;
}
