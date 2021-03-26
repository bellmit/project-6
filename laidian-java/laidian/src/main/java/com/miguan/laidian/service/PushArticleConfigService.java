package com.miguan.laidian.service;

import com.miguan.laidian.entity.PushArticleConfig;

/**
 * Created by laiyd on 2020/4/14.
 */
public interface PushArticleConfigService {

    /**
     * 推送参数接口
     *
     * @param pushChannel 参照枚举pushChannel
     * @param mobileType  参照Constant 1-IOS，2-安卓
     * @return
     */
    PushArticleConfig findPushArticleConfig(String pushChannel, String mobileType, String appType);
}
