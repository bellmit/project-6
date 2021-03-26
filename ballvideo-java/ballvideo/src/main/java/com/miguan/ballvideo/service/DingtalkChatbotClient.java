package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.dingtalk.SendResult;
import com.miguan.ballvideo.vo.ClUserOpinionVo;

public interface DingtalkChatbotClient {

    /**
     * @param opinionVo 发送的消息
     * @return
     */
    SendResult send(ClUserOpinionVo opinionVo);

    /**
     * @param opinionVo 发送的消息
     * @return
     */
    String sendMsg(ClUserOpinionVo opinionVo);
}
