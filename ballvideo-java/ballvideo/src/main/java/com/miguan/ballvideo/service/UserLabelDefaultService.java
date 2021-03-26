package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.UserLabelDefault;

/**
 * Created by shixh on 2019/10/22.
 */
public interface UserLabelDefaultService {

  /**
   * 根据渠道ID查询用户标签设置，如果没有则返回默认设置(channelId=default)
   * @param channelId
   * @return
   */
  public UserLabelDefault getUserLabelDefault(String channelId);
}
