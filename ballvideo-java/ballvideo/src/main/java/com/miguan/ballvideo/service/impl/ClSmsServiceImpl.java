package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.SevenSmsConstant;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.HttpUtil;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.vo.ChannelGroup;
import com.miguan.ballvideo.mapper.SmsConfigMapper;
import com.miguan.ballvideo.mapper.SmsMapper;
import com.miguan.ballvideo.mapper.SmsTplMapper;
import com.miguan.ballvideo.service.ClSmsService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.SmsConfigVo;
import com.miguan.ballvideo.vo.SmsTplVo;
import com.miguan.ballvideo.vo.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 短信ServiceImpl
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-12
 */
@Service("clSmsService")
@Slf4j
public class ClSmsServiceImpl implements ClSmsService {


    @Resource
    private SmsMapper smsMapper;
    @Resource
    private SmsTplMapper smsTplMapper;
    @Resource
    private SmsConfigMapper smsConfigMapper;
    @Resource
    private ToolMofangService toolMofangService;

    @Override
    public long findTimeDifference(String phone, String type) {
        int countdown = Global.getInt("sms_countdown");
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
    public String sendSms(String phone, String type, String appPackage, String appVersion) {
        Map<String, Object> search = new HashMap<>();
        search.put("type", type);
        search.put("state", "10");
        SmsTplVo tpl = smsTplMapper.findSelective(search);
        if (tpl != null) {
            Map<String, Object> payload = new HashMap<>();
            int vcode = (int) (Math.random() * 9000) + 1000;
            payload.put("mobile", phone);
            payload.put("message", tpl.getTpl().replace("{$vcode}", String.valueOf(vcode)));
            Object result = doSend(payload, tpl.getNumber(), tpl.getType(), appPackage, appVersion);
            log.debug("发送短信，phone：" + phone + "， type：" + type + "，同步响应结果：" + result);
            result((String) result, phone, type, vcode);
            // V2.0.0  返回vcode（验证码） 提供给前端使用
            return String.valueOf(vcode);
        }
        log.error("发送短信，phone：" + phone + "， type：" + type + "，没有获取到smsTpl");
        return null;
    }


    public Object doSend(Map<String, Object> payload, String smsNo, String type, String appPackage, String appVersion) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type", type);
        List<SmsConfigVo> list = smsConfigMapper.queryEnableSmsConfig(param);
        if (list == null || list.size() == 0) {
            log.error("没有找到短信接口配置");
            return null;
        }
        SmsConfigVo smsConfig = list.get(0);
        String smsCode = smsConfig.getSmsCode();   //短信第三方code
        if ("SEVEN_SMS".equals(smsCode)) {
            // 七位数短信
            return sevenSendSms(payload, smsConfig, appPackage, appVersion);
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
    private String sevenSendSms(Map<String, Object> payload, SmsConfigVo smsConfig, String appPackage, String appVersion) {
        final HashMap<String, String> params = new HashMap<>();
        for (String key : payload.keySet()) {
            params.put(key, payload.get(key).toString());
        }
        params.put("account", smsConfig.getAccount());
        params.put("pswd", smsConfig.getPassword());
        params.put("resptype", "json");
        params.put("needstatus", "true");
        //V2.5.0西柚视频改名茜柚视频,服务端要兼容旧版本2.5.0以下的还是西柚视频
        boolean high = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_240);
        String msgSign;
        if ("com.mg.xyvideo".equals(appPackage) && !high){
            msgSign = "西柚视频";
        }else {
            msgSign = getMsgSignByAppPackage(appPackage);
        }
        params.put("msg", "【" + msgSign + "】" + payload.get("message").toString());

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

    //跨库查询魔方后台数据，根据包名查询短信签名
    private String getMsgSignByAppPackage(String appPackage) {
        if (StringUtils.isEmpty(appPackage)) return "西柚视频";
        List<ChannelGroup> list = toolMofangService.getChannelGroups(appPackage);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0).getMsgSign();
        }
        return "西柚视频";
    }

    /**
     * 保存短信发送记录
     *
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
            log.debug("发送短信，phone：" + phone + "， type：" + type + "，保存sms时code：" + code);
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
                log.info("resJson = " + resJson);
                log.info("tempJson = " + tempJson);
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
    public int verifySms(String phone, String type, String code) {
        if ("dev".equals(Global.getValue("app_environment")) && "0000".equals(code)) {
            return 1;
        }
        //TODO 苹果审核万能验证码
        if ("prod".equals(Global.getValue("app_environment")) && "19959289578".equals(phone)) {
            return 1;
        }
        if ("prod".equals(Global.getValue("app_environment")) && "15259712180".equals(phone)) {
            return 1;
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
            String mostTimes = Global.getValue("sms_day_most_times");
            int mostTime = JSONObject.parseObject(mostTimes).getIntValue("verifyTime");
            data = new HashMap<>();
            data.put("verifyTime", sms.getVerifyTime() + 1);
            data.put("id", sms.getId());
            smsMapper.updateSelective(data);

            if (StringUtil.equals("40", sms.getState()) || sms.getVerifyTime() + 1 > mostTime) {
                return 0;
            }

            long timeLimit = Long.parseLong(Global.getValue("sms_time_limit"));
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
