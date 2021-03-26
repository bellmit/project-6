package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.vo.push.PushResultCountVo;
import com.vivo.push.sdk.notofication.InvalidUser;

import java.util.List;

public interface PushSevice {
    /**
     * 消息推送
     *
     * @param id
     * @return
     */
    ResultMap realTimeSendInfo(Long id, List<String> distinctIds);

    /**
     * IOS推送消息
     * @param pushArticle
     * @return
     */
    ResultMap sendInfoToIOS(PushArticle pushArticle, List<String> distinctIds);

    /**
     * Android推送消息
     *
     * @param pushArticle
     * @return
     */
    ResultMap sendInfoToAndroid(PushArticle pushArticle, List<String> distinctIds);

    /**
     * Android推送消息
     *
     * @param pushArticle
     * @return
     */
    ResultMap sendInfoToCleanPage(PushArticle pushArticle);

    /**
     * 立即推送测试接口
     *
     * @param id
     * @param tokens
     * @param pushChannel
     * @return
     */
    ResultMap realTimePushTest(Long id, String tokens, String pushChannel, List<String> distinctIds);

    /**
     * 保存vivo无效token
     * @param invalidUsers
     */
    void vivoInvalidUserSave(List<InvalidUser> invalidUsers);

    /**
     * 保存消息推送总数
     * @param countVo
     */
    void savePushCountInfo(PushResultCountVo countVo);

    /**
     * 执行定向兴趣人群推送服务
     * @param params
     */
    void executeInterestCat(String params);
}
