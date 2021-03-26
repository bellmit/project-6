package com.miguan.laidian.entity;

import com.miguan.laidian.entity.common.BaseModel;
import lombok.Data;

import javax.persistence.Entity;

/**
 * 推送配置信息
 * @Author laiyd
 * @Date 2020/4/14
 **/
@Data
@Entity(name="push_article_config")
public class PushArticleConfig extends BaseModel {

    private String pushChannel;//推送渠道，参照枚举PushChannel
    private String appId;
    private String appKey;
    private String appSecret;
    private String pushModel;//推送模式，友盟分测试和正式，VIVO，OPPO，HUAWEI只有正式
    private String mobileType;//参照Constant 1-IOS，2-安卓
    private String appType;//app类型 微来电-wld,炫来电-xld
    private String remarke;
}
