package com.miguan.laidian.service;

import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.PushArticle;
import com.vivo.push.sdk.notofication.InvalidUser;

import java.util.List;

public interface PushSevice {
    /**
     * 消息推送
     *
     * @param id
     * @return
     */
    ResultMap realTimeSendInfo(Long id);

    /**
     * Android推送消息
     *
     * @param pushArticle
     * @return
     */
    ResultMap sendInfoToMobile(PushArticle pushArticle);

    /**
     * 立即推送测试接口
     *
     * @param id
     * @param tokens
     * @param pushChannel
     * @return
     */
    ResultMap realTimePushTest(Long id, String tokens, String pushChannel, String pushType);

    /**
     * 保存vivo无效token
     * @param invalidUsers
     */
    void vivoInvalidUserSave(List<InvalidUser> invalidUsers);
}
