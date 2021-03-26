package com.miguan.ballvideo.entity.dsp;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;
import java.util.Date;

/**sql查询广告内容数据集合
 * @Author suhj
 * @Date 2020/4/24
 **/
@Data
public class AdvDspSqlInfo extends BaseModel{
    //代码位ID
    private String codeId;
    //投放计划ID
    private String planId;
    //投放创意ID
    private String designId;
    //广告主ID
    private String usrId;
    //账号ID
    private String accId;
    //应用ID
    private String appId;
    //日预算
    private double dayPrice;
    //总预算
    private double totalPrice;
    //出价
    private double price;
    //剩余日预算
    private double remainDayPrice;
    //剩余总预算
    private double remainTotalPrice;
    //地域名称
    private String areaName;
    //地域类型,1:不限区域，2：指定区域
    private String areaType;
    //手机型号名称
    private String devName;
    //手机型号类型,1:不限区域，2：指定区域
    private String devType;
    //计划开始时间
    private Date planStartDate;
    //预算平滑启动时间
    private Date smoothDate;
    //计划结束时间
    private Date planEndDate;
    //投放时段：0-全天，1-指定开始时间和结束时间，2-指定多个时段
    private String timeSetting;
    //计划时间配置
    private String timesConfig;
    //创意名称
    private String desName;
    //素材类型，1:图片，2：视频
    private String materialType;
    //素材地址
    private String materialUrl;
    //文案
    private String copy;
    //按钮文案
    private String buttonCopy;
    //logo url（广告主logo）
    private String logoUrl;
    //投放方式，1:落地页链接，2：下载地址，3DEEPLINK,4自定义
    private String putInMethod;
    //投放方式相对应的值
    private String putInValue;
    //投放类型：1：标准投放,2：快速投放
    private String putInType;
    //权重
    private int weight;
    //广告主名称
    private String usrName;
    //产品名称
    private String productName;
    //是否展示产品名称与品牌logo, 1:展示，-1:不展示
    private Integer isShowLogoProduct;
    //广告主联系人
    private String linkMan;
    //广告规格
    private String styleSize;
    //广告类型
    private String positionType;
    //用户兴趣标签。两个及以上用,号隔开
    private String catIds;
    //用户兴趣, 0:不限, 1:自定义
    private Integer catType;
}
