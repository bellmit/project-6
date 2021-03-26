package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.PushArticleConfig;

/**
 * Created by shixh on 2019/12/23.
 */
public interface PushArticleConfigService {

  /**
   * 推送参数接口
   * @param pushChannel 参照枚举pushChannel
   * @param mobileType 参照Constant 1-IOS，2-安卓
   * @return
   */
  PushArticleConfig findPushArticleConfig(String pushChannel, String mobileType,String appPackage);
}
