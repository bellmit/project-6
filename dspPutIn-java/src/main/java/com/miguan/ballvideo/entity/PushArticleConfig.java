package com.miguan.ballvideo.entity;

import com.miguan.ballvideo.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
@Data
@Entity(name="push_article_config")
public class PushArticleConfig extends BaseModel{

    private String pushChannel;//推送渠道，参照枚举PushChannel
    private String appId;
    private String appKey;
    private String appSecret;
    private String pushModel;//推送模式，友盟分测试和正式，VIVO，OPPO，HUAWEI只有正式
    private String mobileType;//参照Constant 1-IOS，2-安卓
    private String appPackage;//马甲包，对应数据参照魔方后台的channel_group表
    private String remarke;
}
