package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.dingtalk.SendResult;
import com.miguan.ballvideo.common.dingtalk.TextMessage;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.HttpUtils;
import com.miguan.ballvideo.mapper.ClUserMapper;
import com.miguan.ballvideo.service.DingtalkChatbotClient;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.ChannelGroup;
import com.miguan.ballvideo.vo.ClUserOpinionVo;
import com.miguan.ballvideo.vo.ClUserVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author laiyd
 */
@Service
public class DingtalkChatbotClientImpl implements DingtalkChatbotClient {

    @Resource
    private ClUserMapper clUserMapper;
    @Resource
    private ToolMofangService toolMofangService;

    @Value("${spring.service.nconf.host}")
    private String nconfUrl;
    @Value("${dingding.phone_number}")
    private String phoneNumber;

    //获取httpclient
    private HttpClient getHttpclient() {
        return HttpClients.createDefault();
    }

    @Override
    public SendResult send(ClUserOpinionVo opinionVo) {
        SendResult sendResult = new SendResult();
        String webhook = Constant.webhock;
        String appEnvironment = Global.getValue("app_environment");
        if ("dev".equals(appEnvironment)) {
            webhook = Constant.webhockDev;
        }
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

    @Override
    public String sendMsg(ClUserOpinionVo opinionVo) {
        StringBuilder opinionMsg = getOpinionMsg(opinionVo);
        try {
            String code = Constant.proCode;
            String appEnvironment = Global.getValue("app_environment");
            if ("dev".equals(appEnvironment)) {
                code = Constant.devCode;
            }
            String url = "http://" + nconfUrl + "/api/dingtalk/pushByCode";
            List<String> mobiles = Arrays.asList(phoneNumber.split(","));
            Map<String,Object> param = new HashMap<>();
            param.put("code",code);
            param.put("content",opinionMsg.toString());
            param.put("mobiles",mobiles);
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip,deflate,sdch");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8");
            headers.put("Connection", "keep-alive");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36 SE 2.X MetaSr 1.0");
            String sendResult = HttpUtils.doPostHttp(url,headers,param);
            JSONObject jsonObject = JSONObject.parseObject(sendResult);
            if(jsonObject.getInteger("code") == null || 0 != jsonObject.getInteger("code")){
                return jsonObject.getString("msg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private TextMessage getMessage(ClUserOpinionVo opinionVo) {
        StringBuilder opinionMsg = getOpinionMsg(opinionVo);
        TextMessage textMessage = new TextMessage(opinionMsg.toString());
        textMessage.setAtAll(true);
        return textMessage;
    }

    private StringBuilder getOpinionMsg(ClUserOpinionVo opinionVo) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", opinionVo.getUserId());
        List<ClUserVo> clUserList = clUserMapper.findClUserList(paramMap);
        String userName = opinionVo.getUserId().toString();
        String phone = "";
        if (CollectionUtils.isNotEmpty(clUserList)) {
            userName = clUserList.get(0).getName();
            phone = clUserList.get(0).getLoginName();
        }
        List<ChannelGroup> channelGroups = toolMofangService.getChannelGroups(opinionVo.getAppPackage());
        String today = DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
        StringBuilder opinionMsg = new StringBuilder();
        opinionMsg.append("反馈时间：").append(today).append("\n");
        opinionMsg.append("APP类型：").append(channelGroups.get(0).getName()).append("\n");
        opinionMsg.append("用户昵称：").append(userName).append("\n");
        opinionMsg.append("手机号：").append(phone).append("\n");
        //西柚没有机型版本和安卓版本参数
        //opinionMsg.append("机型版本：").append(opinionVo.getTelTypeVision()).append("\n");
        //opinionMsg.append("安卓版本：").append(opinionVo.getAndroidVision()).append("\n");
        opinionMsg.append("反馈内容：").append(opinionVo.getContent()).append("\n");
        if (StringUtils.isNotEmpty(opinionVo.getImageUrl())) {
            opinionMsg.append("反馈截图：").append(opinionVo.getImageUrl()).append("\n");
        }
        return opinionMsg;
    }
}
