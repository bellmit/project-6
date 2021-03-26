package com.miguan.idmapping.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.base.core.exception.CommonException;
import com.miguan.idmapping.common.utils.HttpUtils;
import com.miguan.idmapping.service.WecharService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenwf
 * @date 2020/5/27
 */
@Service
@Transactional
@Slf4j
public class WecharServiceImpl implements WecharService {

    /**
     * 微信授权，根据code获取 openid请求地址
     */
    private static final String REQ_WECHAR_AUTH_CODE_URL = "https://api.weixin.qq.com/sns/jscode2session";
    private static final String REQ_WECHAR_AUTH_CODE_URL_PARAMS = "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 微信授权
     * @param code
     */
    @Override
    public Map<String, Object> wecharAuth(String code) {
        String appid = "wx494ddaa6e62e0900";//茜柚极速APPID
        String secret = "992eb4af30e3e6be8838f176a3039b1d";//茜柚极速密钥
        Map<String, Object> restMap = new HashMap<>(16);
        String param = String.format(REQ_WECHAR_AUTH_CODE_URL_PARAMS, appid, secret, code);
        String result = HttpUtils.sendGet(REQ_WECHAR_AUTH_CODE_URL, param);
        JSONObject jsonStr = JSONObject.parseObject(result);
        if (log.isDebugEnabled()) {
            log.debug("微信授权返回信息：{}", jsonStr);
        }
        if (!jsonStr.containsKey("errcode")) {
            restMap.put("openId", jsonStr.getString("openid"));
        } else {
            log.error("微信授权失败：errcode={} errmsg={}", jsonStr.getString("errcode"), jsonStr.getString("errmsg"));
            throw new CommonException("微信授权失败：errcode=" + jsonStr.getString("errcode") + "  {" + jsonStr.getString("errmsg") + "}");
        }
        return restMap;
    }
}
