package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.constants.SevenSmsConstant;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.HttpUtil;
import com.miguan.laidian.common.util.StringUtil;
import com.miguan.laidian.mapper.SmsConfigMapper;
import com.miguan.laidian.mapper.SmsMapper;
import com.miguan.laidian.mapper.SmsTplMapper;
import com.miguan.laidian.service.ClSmsService;
import com.miguan.laidian.vo.SmsConfigVo;
import com.miguan.laidian.vo.SmsTplVo;
import com.miguan.laidian.vo.SmsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 短信ServiceImpl
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-12
 */
@Service("clSmsService")
public class ClSmsServiceImpl implements ClSmsService {

    public static final Logger logger = LoggerFactory.getLogger(ClSmsServiceImpl.class);

    @Resource
    private SmsMapper smsMapper;
    @Resource
    private SmsTplMapper smsTplMapper;
    @Resource
    private SmsConfigMapper smsConfigMapper;

    @Override
    public long findTimeDifference(String phone, String type, String appType) {
        int countdown = Global.getInt("sms_countdown",appType);
        Map<String, Object> data = new HashMap<>();
        data.put("phone", phone);
        data.put("smsType", type);
        SmsVo sms = smsMapper.findTimeMsg(data);
        long times = 0;
        if (sms != null) {
            Date d1 = sms.getSendTime();
            Date d2 = DateUtil.getNow();
            long diff = d2.getTime() - d1.getTime();
            if (diff < countdown * 1000) {
                times = countdown - (diff / 1000);
            } else {
                times = 0;
            }
        }
        return times;
    }

    @Override
    public List<SmsVo> countDayTime(String phone, String type) {
        Map<String, Object> data = new HashMap<>();
        data.put("phone", phone);
        data.put("smsType", type);
        return smsMapper.countDayTime(data);
    }

    @Override
    public String sendSms(String phone, String type, String appType) {
        Map<String, Object> search = new HashMap<>();
        search.put("type", type);
        search.put("state", "10");
        SmsTplVo tpl = smsTplMapper.findSelective(search);
        if (tpl != null) {
            Map<String, Object> payload = new HashMap<>();
            int vcode = (int) (Math.random() * 9000) + 1000;
            payload.put("mobile", phone);
            payload.put("message", tpl.getTpl().replace("{$vcode}", String.valueOf(vcode)));
            Object result = doSend(payload, tpl.getNumber(), tpl.getType(),appType);
            logger.debug("发送短信，phone：" + phone + "， type：" + type + "，同步响应结果：" + result);
            return result((String) result, phone, type,vcode);
        }
        logger.error("发送短信，phone：" + phone + "， type：" + type + "，没有获取到smsTpl");
        return null;
    }


    public Object doSend(Map<String, Object> payload, String smsNo, String type, String appType) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type", type);
        List<SmsConfigVo> list = smsConfigMapper.queryEnableSmsConfig(param);
        if (list == null || list.size() == 0) {
            logger.error("没有找到短信接口配置");
            return null;
        }
        SmsConfigVo smsConfig = list.get(0);
        String smsCode = smsConfig.getSmsCode();   //短信第三方code
        if ("SEVEN_SMS".equals(smsCode)) {
            // 七位数短信
            return sevenSendSms(payload, smsConfig, appType);
        }
        return null;
    }


    /**
     * 七位数短信
     *
     * @param payload
     * @param smsConfig
     * @return
     */
    private String sevenSendSms(Map<String, Object> payload, SmsConfigVo smsConfig, String appType) {
        final HashMap<String, String> params = new HashMap<>();
        for (String key : payload.keySet()) {
            params.put(key, payload.get(key).toString());
        }
        params.put("account", smsConfig.getAccount());
        params.put("pswd", smsConfig.getPassword());
        params.put("resptype", "json");
        params.put("needstatus", "true");
        String sign = "【炫来电】";
        if (Constant.appWld.equals(appType)){
            sign = "【微来电】";
        }
        params.put("msg", sign + payload.get("message").toString());

        final String result = HttpUtil.postClient(smsConfig.getInterfaceUrl(), params);
        final JSONObject resultJson = JSON.parseObject(result);
        final Integer resultCode = resultJson.getInteger("result");
        final JSONObject res = new JSONObject();
        res.put("result", payload.get("message"));
        resultJson.put("code", resultCode);
        if (resultCode == 0) {
            resultJson.put("code", 200);
            res.put("successCount", "1");
            resultJson.put("successCount", "1");
        } else {
            res.put("successCount", "0");
            resultJson.put("successCount", "0");
        }
        resultJson.put("res", res);
        resultJson.put("orderNo", resultJson.getString("msgid"));
        resultJson.put("tempParame", new HashMap<>());
        resultJson.put("message", SevenSmsConstant.MESSAGE.get(resultCode));
        return resultJson.toJSONString();
    }

    /**
     * 保存短信发送记录
     * @param result
     * @param phone
     * @param type
     * @return
     */
    private String result(String result, String phone, String type, int vcode) {
        String msg = null;
        JSONObject resultJson = JSONObject.parseObject(result);

        Integer code;
        if (StringUtil.isNotBlank(resultJson)) {
            code = resultJson.getInteger("code");
            logger.debug("发送短信，phone：" + phone + "， type：" + type + "，保存sms时code：" + code);
            Date now = DateUtil.getNow();
            SmsVo sms = new SmsVo();
            sms.setPhone(phone);
            sms.setSendTime(now);
            sms.setRespTime(now);
            sms.setSmsType(type);
            sms.setVerifyTime(0);

            if (code == 200) {
                JSONObject resJson = JSONObject.parseObject(StringUtil.isNull(resultJson.get("res")));
                JSONObject tempJson = JSONObject.parseObject(StringUtil.isNull(resultJson.get("tempParame")));
                logger.info("resJson = " + resJson);
                logger.info("tempJson = " + tempJson);
                String orderNo = StringUtil.isNull(resultJson.get("orderNo"));
                sms.setContent(resJson != null ? resJson.getString("result") : "");
                sms.setResp("短信已发送");
                sms.setCode(StringUtil.isNull(vcode));
                sms.setOrderNo(orderNo);
                sms.setState("30");
                int ms = smsMapper.save(sms);
                if (ms > 0) {
                    msg = orderNo;
                }
            } else {
                String message = resultJson.getString("message");
                sms.setContent(message);
                sms.setResp("短信发送失败");
                sms.setCode("");
                sms.setOrderNo("");
                sms.setState("40");
                smsMapper.save(sms);
            }
        }
        return msg;
    }

    public SmsTplVo querySmsTplInfoByType(String type) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type", type);
        return smsTplMapper.querySmsTplInfo(param);
    }

    @Override
    public int verifySms(String phone, String type, String code,String appType) {
        if ("dev".equals(Global.getValue("app_environment",appType)) && "0000".equals(code)) {
            return 1;
        }
        //万能验证码
        String loginWhite = Global.getValue("login_white", appType);
        List<String> list = Arrays.asList(loginWhite.split(","));
        for (String loginName : list) {
            if ("prod".equals(Global.getValue("app_environment",appType)) && loginName.equals(phone)) {
                return 1;
            }
        }

        if (StringUtil.isBlank(phone) || StringUtil.isBlank(type) || StringUtil.isBlank(code)) {
            return 0;
        }

        if (!StringUtil.isPhone(phone)) {
            return 0;
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("phone", phone);
        data.put("smsType", type);
        SmsVo sms = smsMapper.findTimeMsg(data);
        if (sms != null) {
            String mostTimes = Global.getValue("sms_day_most_times",appType);
            int mostTime = JSONObject.parseObject(mostTimes).getIntValue("verifyTime");
            data = new HashMap<>();
            data.put("verifyTime", sms.getVerifyTime() + 1);
            data.put("id", sms.getId());
            smsMapper.updateSelective(data);

            if (StringUtil.equals("40", sms.getState()) || sms.getVerifyTime() + 1 > mostTime) {
                return 0;
            }

            long timeLimit = Long.parseLong(Global.getValue("sms_time_limit",appType));
            Date d1 = sms.getSendTime();
            Date d2 = DateUtil.getNow();
            long diff = d2.getTime() - d1.getTime();
            if (diff > timeLimit * 60 * 1000) {
                return -1;
            }
            if (StringUtil.isNotBlank(sms.getCode())) {
                if (sms.getCode().equals(code)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", sms.getId());
                    map.put("state", "20");
                    map.put("resp", "短信验证码已使用");
                    smsMapper.updateSelective(map);
                    return 1;
                }
            }
        }
        return 0;
    }
}
