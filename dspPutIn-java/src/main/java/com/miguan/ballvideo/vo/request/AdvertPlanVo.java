package com.miguan.ballvideo.vo.request;

import com.cgcg.context.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.miguan.ballvideo.common.constants.DefaultConstant;
import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.common.exception.ValidateException;
import com.miguan.ballvideo.common.util.TimeConfigFormat;
import com.miguan.ballvideo.common.util.ValidatorUtil;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("广告计划")
public class AdvertPlanVo {

    @ApiModelProperty(value = "广告计划id",position = 10)
    private Long id;

    @ApiModelProperty(value = "广告主id",required = true, hidden=true,position = 20)
    private Long advert_user_id;

    @ApiModelProperty(value = "计划组",required = true, position = 30)
    private AdvertGroupVo group;

    @ApiModelProperty(value = "计划组id",hidden=true, position = 30)
    private Integer group_id;

    //用户定向
    @ApiModelProperty(value = "区域类型,1:不限区域，2：指定区域",required = true, position = 40)
    private Integer area_type;

    @ApiModelProperty(value = "区域;多个用逗号隔开。", position = 50)
    private String area;

    @ApiModelProperty(value = "手机类型,1:不限品牌，2：指定品牌",required = true, position = 60)
    private Integer phone_type;

    @ApiModelProperty(value = "手机品牌;多个用逗号隔开。", position = 70)
    private String brand;

    @ApiModelProperty(value = "用户兴趣, 0:不限, 1:自定义",required = true, position = 80)
    private Integer cat_type;

    @ApiModelProperty(value = "用户兴趣标签。两个及以上用,号隔开", position = 90)
    private String cat_ids;

    //预算与出价
    @ApiModelProperty(value = "投放类型：1：标准投放,2：快速投放", position = 100)
    private Integer put_in_type;

    @ApiModelProperty(value = "日预算（元）",required = true, position = 110)
    private BigDecimal day_price;

    @ApiModelProperty(value = "总预算（元）",required = true, position = 120)
    private BigDecimal total_price;

    @ApiModelProperty(value = "时间类型",required = true, position = 130)
    private Integer date_type;

    @ApiModelProperty(value = "开始时间", position = 140)
    private String start_date;

    @ApiModelProperty(value = "结束时间，为空给一个默认最大。2030-01-01 00:00:00", position = 150)
    private String end_date;

    @ApiModelProperty(value = "投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段", position = 160)
    private Integer time_setting;

    @ApiModelProperty(value = "时间配置json[{arr: [20, 21, 22]}]", position = 170)
    private String times_config;

    @ApiModelProperty(value = "出价方式：1-CPC",required = true, position = 180)
    private Integer price_method;

    @ApiModelProperty(value = "出价（元）",required = true, position = 190)
    private BigDecimal price;

    @ApiModelProperty(value = "计划名称",required = true, position = 200 )
    private String name;


    //创意
    @ApiModelProperty(value = "素材类型，1:图片，2：视频",required = true, position = 210)
    private Integer material_type;

    @ApiModelProperty(value = "创意形式：1.竖版大图9:16;  2.横版大图16:9;  3.横版长图6:1;  4.左图右文1.5:1;   5.右图左文1.5:1；选中创意类型为视频，可选择创意形式：  6.竖版视频9：16;  7.横版视频：16:9",required = true, position = 220)
    private Integer material_shape;

    //广告创意
    @ApiModelProperty(value = "创意的列表",required = true, position = 230)
    private List<AdvertDesignVo> design_list;



    @ApiModelProperty(value = "0-暂停,1-投放中", hidden=true, position = 240)
    private Integer state;

    @ApiModelProperty(value = "是否展示广告主logo和名称", position = 250)
    private String show_logo;

    @ApiModelProperty(value = "广告类型", position = 260)
    private String advert_type;

    @ApiModelProperty(value = "预算平滑启动时间", hidden=true)
    private Date smooth_date;


    @ApiModelProperty(value = "创建时间", hidden=true)
    private Date created_at;

    @ApiModelProperty(value = "修改时间" , hidden=true)
    private Date updated_at;




    //以下字段暂时不使用
    @ApiModelProperty(value = "版本区间1,字段暂时不使用" , hidden=true)
    private String version1;

    @ApiModelProperty(value = "版本区间2,字段暂时不使用", hidden=true)
    private String version2;

    @ApiModelProperty(value = "应用ID,字段暂时不使用", hidden=true)
    private Long app_id;

    @ApiModelProperty(value = "代码位ID,字段暂时不使用", hidden=true)
    private Long code_id;

    @ApiModelProperty(value = "广告位类型,字段暂时不使用" , hidden=true)
    private String position_type;

    public void validate() throws ValidateException {
        // todo advert_user_id 校验还原
//        if(planVo.getAdvert_user_id() == null){  //记得再校验下该广告主是否存在！
//            throw new ValidateException("必须传入广告主!");
//        }
        ValidatorUtil.checkRequest(group, "计划组");
        ValidatorUtil.checkRequest(time_setting, "投放时间类型");
        ValidatorUtil.checkRequest(price_method, "出价方式");
        ValidatorUtil.checkRequest(cat_type, "兴趣类别");
        ValidatorUtil.checkRequestCollection(design_list, "广告创意");
        for (AdvertDesignVo designVo:design_list) {
            designVo.validate();
            designVo.init();
            if(designVo != null && designVo.getId() == null && state != null ){
                designVo.setState(state);
            }
        }
        ValidatorUtil.checkRequest(material_type, "创意类型");
        ValidatorUtil.checkRequestType(MaterialShapeConstants.materialShapeMap,material_shape, "创意形式");
        if(date_type == null){
            throw new ValidateException("必须选择投放日期类型!");
        } else if (date_type == TypeConstant.DATE_TYPE_ALL && StringUtils.isEmpty(start_date)){
            throw new ValidateException("投放日期,必须选择起始时间!");
        } else if (date_type == TypeConstant.DATE_TYPE_APPOINT && StringUtils.isEmpty(start_date) && StringUtils.isEmpty(end_date)){
            throw new ValidateException("投放日期,必须选择起始时间与终止时间!");
        }
        ValidatorUtil.checkRequest(area_type, "地理位置");
        ValidatorUtil.checkRequest(put_in_type, "投放方式");
        ValidatorUtil.checkRequest(phone_type, "手机类型");
        ValidatorUtil.checkRequestMaxStr(name, "计划名称", 30);
        ValidatorUtil.checkMaxStr(area,"区域",1000);
        ValidatorUtil.checkMaxStr(brand,"手机品牌",250);
        ValidatorUtil.checkRequestPriceNum(price,"价格");
        ValidatorUtil.checkRequestPriceNum(day_price,"日预算");
        ValidatorUtil.checkPriceNum(total_price,"总预算");
    }

    public void init() throws ValidateException {
        if(state == null){
            state = 1;
        }
        if(StringUtils.isEmpty(version1)){
            version1 = DefaultConstant.MIN_VERSION;
        }
        if(StringUtils.isEmpty(version2)){
            version2 = DefaultConstant.MAS_VERSION;
        }
        if(day_price == null){
            day_price = new BigDecimal(0);
        }
        if(total_price == null){
            total_price = new BigDecimal(0);
        }
        if(advert_user_id == null){
            advert_user_id = -1L;
        }
        if(group.getDayPrice() == null){
            group.setDayPrice(-1d);
        }
        times_config = TimeConfigFormat.analysisTimeConfig(time_setting, times_config);
    }

}
