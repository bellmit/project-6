package com.miguan.xuanyuan.common.util;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

public class RobotUtil {


    private final static String send_url = "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * 获得签名
     * @return
     */
    public static String getSign(Long timestamp, String secret){
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void talkText(String content, String secret, String accessToken) {
        try {
            Long timestamp = System.currentTimeMillis();
            String sign = getSign(timestamp,secret);
            DingTalkClient client = new DefaultDingTalkClient(send_url+accessToken +"&timestamp="+timestamp+"&sign="+sign);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(content);
            request.setText(text);
            OapiRobotSendResponse response = client.execute(request);
            System.out.println("=====机器人======");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //talk();
    }
}
