package com.miguan.laidian.service;

import com.miguan.laidian.common.dingtalk.SendResult;
import com.miguan.laidian.vo.ClUserOpinionVo;

public interface DingtalkChatbotClient {

    /**
     * @param opinionVo 发送的消息
     * @return
     */
    SendResult send(ClUserOpinionVo opinionVo);
}
