package com.miguan.laidian.service;

import com.miguan.laidian.entity.PushArticleSendResult;

/**
 * Created by laiyd on 2020/4/14.
 */
public interface PushArticleSendResultService {

  /**
   * 记录推送记录
   * @param pushArticleId 推送ID
   * @param pushChannel 参照枚举PushChannel
   * @param businessId 推送成功返回的业务ID
   * @param appPackage 马甲包
   */
  void saveSendResult(Long pushArticleId, String pushChannel, String businessId, String appPackage);

  PushArticleSendResult findByPushChannelAndBusinessId(String name, String requestId);

  void save(PushArticleSendResult sendResult);
}
