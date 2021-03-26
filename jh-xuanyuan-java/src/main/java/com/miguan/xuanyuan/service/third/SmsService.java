package com.miguan.xuanyuan.service.third;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.xuanyuan.common.enums.SevenSmsConstant;
import com.miguan.xuanyuan.common.util.AbResultMap;
import com.miguan.xuanyuan.common.util.RandomUtil;
import com.miguan.xuanyuan.vo.SmsResultMap;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tool.util.HttpUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信服务
 *
 */
@Service
public class SmsService {


    @Value("${sms.url}")
    private String url;

    @Value("${sms.account}")
    private  String account;

    @Value("${sms.password}")
    private String password;

    @Value("${sms.sendSMApi}")
    private String sendSMApi;

    public final static String MSG_SIGN = "轩辕聚合";

    //注册验证文案
    private String RegisterMsg = "您的验证码为%s,有效期为10分钟，请勿泄漏。";

    @Resource
    private RestTemplate restTemplate;

    public JSONObject getRegisterVerifiCode(String phone) throws Exception {
        String code = RandomUtil.getRandomNumber(6);
        String message = String.format(RegisterMsg, code);
        JSONObject result = this.sendSms(phone, message);
        result.put("verifiCode", code);
        return result;
    }

    public JSONObject sendSms(String phone, String message) {
        return this.sendSms(phone, message, SmsService.MSG_SIGN);
    }

    public JSONObject sendSms(String phone, String message, String msgSign) {
        final HashMap<String, String> params = new HashMap<>();
        message = "【" + msgSign + "】" + message;
        params.put("mobile", phone);
        params.put("account", this.account);
        params.put("pswd", this.password);
        params.put("resptype", "json");
        params.put("needstatus", "true");
        params.put("msg", message);

        String url = this.url + this.sendSMApi;
        String result = HttpUtil.postClient(url, params);
        JSONObject resultJson = JSON.parseObject(result);
        Integer resultCode = resultJson.getInteger("result");
        JSONObject res = new JSONObject();
        res.put("message", message);
        res.put("code", resultCode);
        if (resultCode == 0) {
            res.put("successCount", "1");
        } else {
            res.put("successCount", "0");
        }
        res.put("orderNo", resultJson.getString("msgid"));
        res.put("ts", resultJson.getString("ts"));
        return res;
    }


//    public String sendSms(String phone, String message, String msgSign) {
//
//        message = "【" + msgSign + "】" + message;
//        MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
//        params.add("mobile", phone);
//        params.add("account", this.account);
//        params.add("pswd", this.password);
//        params.add("resptype", "json");
//        params.add("needstatus", "true");
//        params.add("message", message);
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        HttpEntity httpEntity = new HttpEntity(params, requestHeaders);
//
//        String api = this.url + this.sendSMApi;
//        try {
//            ResponseEntity<String> res = restTemplate.postForEntity(api, httpEntity, String.class);
//            if (res.getStatusCode().is2xxSuccessful()) {
//                String result = res.getBody();
//                return result;
//            }
//        } catch (RestClientException e) {
//            e.printStackTrace();
//
//        }
//        return null;
//    }




}
