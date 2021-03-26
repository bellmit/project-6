package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.dingtalk.SendResult;
import com.miguan.laidian.common.dingtalk.TextMessage;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.mapper.ClUserMapper;
import com.miguan.laidian.service.DingtalkChatbotClient;
import com.miguan.laidian.vo.ClUserOpinionVo;
import com.miguan.laidian.vo.ClUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author laiyd
 */
@Service
public class DingtalkChatbotClientImpl implements DingtalkChatbotClient {

    @Resource
    private ClUserMapper clUserMapper;

    //获取httpclient
    private HttpClient getHttpclient() {
        return HttpClients.createDefault();
    }

    @Override
    public SendResult send(ClUserOpinionVo opinionVo) {
        SendResult sendResult = new SendResult();
        String webhook = Constant.webhock;
        TextMessage message = getMessage(opinionVo);
        try {
            HttpClient httpclient = getHttpclient();
            HttpPost httppost = new HttpPost(webhook);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");
            StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode()==200){
                String result = EntityUtils.toString(response.getEntity());
                JSONObject obj = JSONObject.parseObject(result);
                Integer errcode = obj.getInteger("errcode");
                sendResult.setErrorCode(errcode);
                sendResult.setSuccess(errcode.equals(0));
                sendResult.setErrorMsg(obj.getString("errmsg"));
            }
            return sendResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendResult;
    }

    private TextMessage getMessage(ClUserOpinionVo opinionVo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", opinionVo.getUserId());
        List<ClUserVo> clUserList = clUserMapper.findClUserList(paramMap);
        String userName = opinionVo.getUserId().toString();
        String phone = opinionVo.getContact();
        if (CollectionUtils.isNotEmpty(clUserList)) {
            userName = clUserList.get(0).getName();
            phone = clUserList.get(0).getLoginName();
        }
        String today = DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
        StringBuilder opinionMsg = new StringBuilder();
        opinionMsg.append("反馈时间：").append(today).append("\n");
        opinionMsg.append("APP类型：").append(opinionVo.getAppType()).append("\n");
        opinionMsg.append("用户昵称：").append(userName).append("\n");
        opinionMsg.append("手机号：").append(phone).append("\n");
        opinionMsg.append("机型版本：").append(opinionVo.getTelTypeVision()).append("\n");
        opinionMsg.append("安卓版本：").append(opinionVo.getAndroidVision()).append("\n");
        opinionMsg.append("反馈内容：").append(opinionVo.getContent()).append("\n");
        if (StringUtils.isNotEmpty(opinionVo.getImageUrl())) {
            opinionMsg.append("反馈截图：").append(opinionVo.getImageUrl()).append("\n");
        }
        TextMessage textMessage = new TextMessage(opinionMsg.toString());
        textMessage.setAtAll(true);
        return textMessage;
    }
}
