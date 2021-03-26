package com.miguan.laidian.service;

import com.miguan.laidian.entity.UserLabelDefault;

public interface UserLabelDefaultService {

  /**
   * 根据渠道ID查询用户默认标签设置
   * @param channelId
   * @return
   */
  UserLabelDefault getUserLabelDefault(String channelId);
}
