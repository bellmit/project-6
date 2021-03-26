package com.miguan.idmapping.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.idmapping.common.utils.Global;
import com.miguan.idmapping.mapper.SmsMapper;
import com.miguan.idmapping.service.ClSmsService;
import com.miguan.idmapping.vo.SmsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(type) || StringUtils.isBlank(code)) {
            return 0;
        }

        if (!isPhone(phone)) {
            return 0;
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("phone", phone);
        data.put("smsType", type);
        SmsVo sms = smsMapper.findTimeMsg(data);
        if (sms != null) {
            String mostTimes = Global.getValue("sms_day_most_times");
            int mostTime = JSON.parseObject(mostTimes).getIntValue("verifyTime");
            data = new HashMap<>();
            data.put("verifyTime", sms.getVerifyTime() + 1);
            data.put("id", sms.getId());
            smsMapper.updateSelective(data);

            if (StringUtils.equals("40", sms.getState()) || sms.getVerifyTime() + 1 > mostTime) {
                return 0;
            }

            long timeLimit = Long.parseLong(Global.getValue("sms_time_limit"));
            Date d1 = sms.getSendTime();
            Date d2 = new Date();
            long diff = d2.getTime() - d1.getTime();
            if (diff > timeLimit * 60 * 1000) {
                return -1;
            }
            if (StringUtils.isNotBlank(sms.getCode())) {
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

    final Pattern regex = Pattern.compile("^\\d{11}$");

    public boolean isPhone(String str) {
        String phone = StringUtils.trimToEmpty(str);
        int length = phone.length();
        if (length != 11 && length != 13 && length != 14) {
            return false;
        }
        if (phone.startsWith("+86")) {
            phone = phone.substring(3, length);
        } else if (phone.startsWith("86")) {
            phone = phone.substring(2, length);
        }
        Matcher matcher = regex.matcher(phone);
        boolean isMatched = matcher.matches();
        if (!isMatched) {
            return false;
        }
        return true;
    }
}
