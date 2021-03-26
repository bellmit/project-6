package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;

/**
 * @Author shixh
 * @Date 2019/9/10
 **/
@Entity(name="push_article")
@Data
public class PushArticle extends BaseModel{
    private String title;
    private String noteContent;
    private String pushTime;
    private int pushType; //1-定时 2-推送
    private int state;//1-开启
    private int type;//1-app启动； 2-链接； 3-短视频； 4-小视频；
    private String typeValue;
    private String userType;//用户类型：10全部用户 20指定用户
    private String deviceTokens;//友盟对设备的唯一标识，多个用逗号隔开
    private String huaweiTokens;//友盟对设备的唯一标识，多个用逗号隔开
    private String vivoTokens;//vivo对设备的唯一标识，多个用逗号隔开
    private String oppoTokens;//oppo对设备的唯一标识，多个用逗号隔开
    private String expireTime;//推送有效期
    private String thumbnailUrl;//缩略图URL
    private String videoTitle;//视频标题
    private String xiaomiTokens;//小米对设备的唯一标识，多个用逗号隔开
    private String appPackage;//马甲包，对应数据参照魔方后台的channel_group表
    private String userIds;//用户Id，多用户
}
