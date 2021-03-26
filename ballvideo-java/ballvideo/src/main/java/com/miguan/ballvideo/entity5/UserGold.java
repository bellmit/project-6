package com.miguan.ballvideo.entity5;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

@ApiModel("用户金币信息")
@Entity(name = "user_gold")
@Data
public class UserGold extends BaseModel {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("总金币")
    private Long totalGold;

    @ApiModelProperty("已用金币")
    private Long usedGold;

    @ApiModelProperty("作用包")
    private String appPackage;

    @ApiModelProperty("应用类别：xysp-茜柚视频，ggsp-果果视频")
    private String appType;

    @ApiModelProperty("ios内购套餐过期时间")
    private String iosExpireDay;

    @ApiModelProperty("第一季度金币")
    private Long firstGold;

    @ApiModelProperty("第二季度金币")
    private Long secondGold;

    @ApiModelProperty("第三季度金币")
    private Long thirdGold;

    @ApiModelProperty("第四季度金币")
    private Long fourthGold;
}
