package com.miguan.laidian.entity;


import com.miguan.laidian.entity.common.BaseModel;
import lombok.Data;
import org.hibernate.annotations.Proxy;
import javax.persistence.*;

/**
 * @Author shixh
 * @Date 2019/9/10
 **/
@Entity(name = "push_article")
@Data
@Proxy(lazy = false)//json串反序列化对象时抛出的异常，在访问关联对象时session已关闭
public class PushArticle extends BaseModel {
    private String title;
    private String noteContent;
    private String pushTime;
    private int state;//1-开启
    private int type;//1-app启动； 2-链接； 3-短视频； 4-小视频；
    private String typeValue;
    private String appType;//xld--炫来电 wld--微来电
    private int pushType; //1-定时 2-推送
    private String expireTime;//推送有效期
    private String huaweiTokens;//友盟对设备的唯一标识，多个用逗号隔开
    private String vivoTokens;//vivo对设备的唯一标识，多个用逗号隔开
    private String oppoTokens;//oppo对设备的唯一标识，多个用逗号隔开
    private String xiaomiTokens;//小米对设备的唯一标识，多个用逗号隔开
    private String channelIds;//推送渠道,以,隔开
    private String appPackage;//马甲包，对应数据参照魔方后台的channel_group表
    private String thumbnailUrl;//缩略图URL
    private Integer actType;
}
