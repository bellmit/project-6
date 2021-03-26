package com.miguan.ballvideo.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("广告计划Res")
public class AdvertPlanRes {
    @ApiModelProperty("广告计划id")
    private Long id;

    @ApiModelProperty("广告主id")
    private Long advert_user_id;

    @ApiModelProperty(value = "计划组")
    private AdvertGroupVo group;

    @ApiModelProperty(value = "计划组id",hidden = true)
    private Integer group_id;

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

    @ApiModelProperty("0-暂停,1-投放中")
    private Integer state;

    @ApiModelProperty("投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段")
    private Integer time_setting;


    @ApiModelProperty("临时时间配置字段")
    @JsonIgnore
    private String tmp_times_config;

    @ApiModelProperty("时间配置 [{arr: [20, 21, 22]}]")
    private Object times_config;

    @ApiModelProperty("日预算（元）")
    private BigDecimal day_price;

    @ApiModelProperty("总预算（元）")
    private BigDecimal total_price;

    @ApiModelProperty("是否展示广告主logo和名称")
    private String show_logo;

    @ApiModelProperty("投放类型：1：标准投放,2：快速投放")
    private Integer put_in_type;

    @ApiModelProperty("广告类型")
    private String advert_type;

    @ApiModelProperty("预算平滑启动时间")
    private Date smooth_date;

    @ApiModelProperty("用户兴趣, 0:不限, 1:自定义")
    private Integer cat_type;

    @ApiModelProperty("用户兴趣标签。两个及以上用,号隔开")
    private String cat_ids;

    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;


    //新增时,传递的字段

    @ApiModelProperty("区域类型,1:不限区域，2：指定区域")
    private Integer area_type;

    @ApiModelProperty("区域列表")
    private List<DistrictRes> area;

    @ApiModelProperty("手机类型,1:不限品牌，2：指定品牌")
    private Integer phone_type;

    @ApiModelProperty("手机品牌列表")
    private List<PhoneBrandRes> phone;

    @ApiModelProperty("创意的id")
    private List<AdvertDesignVo> design_list;

    @ApiModelProperty("素材类型，1:大图，2：视频")
    private Integer material_type;

    @ApiModelProperty("创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9")
    private Integer material_shape;

    //以下字段暂时不使用
    @ApiModelProperty("版本区间1,字段暂时不使用")
    private String version1;

    @ApiModelProperty("版本区间2,字段暂时不使用")
    private String version2;

    @ApiModelProperty("应用ID,字段暂时不使用")
    private Long app_id;

    @ApiModelProperty("代码位ID,字段暂时不使用")
    private Long code_id;

    @ApiModelProperty("广告位类型,字段暂时不使用")
    private String position_type;

    @ApiModelProperty("时间类型,字段暂时不使用")
    private Integer date_type;

}
