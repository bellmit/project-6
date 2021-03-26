package com.miguan.xuanyuan.service.common;

import com.alibaba.fastjson.JSON;
import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.util.SerializeUtil;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisService extends RedisBaseService {

    /**
     * 获取用户token缓存key
     * @param userId
     * @return
     */
    public String getUserTokenKey(int userId) {
        return RedisConstant.USER_TOKEN_PREFIX + userId;
    }

    /**
     * 注册验证码缓存key
     *
     * @param phone
     * @return
     */
    public String getRegisterVerifiCodeCntCacheKey(String phone) {
        return RedisConstant.REGISTER_VERIFI_CODE_CNT + phone;
    }

    /**
     * 注册验证码缓存key
     *
     * @param phone
     * @return
     */
    public String getRegisterVerifiCodeCacheKey(String phone) {
        return RedisConstant.REGISTER_VERIFI_CODE + phone;
    }

    /**
     * 注册验证码次数缓存
     *
     * @param phone
     * @return
     */
    public int getRegisterVerifiCodeCntCache(String phone) {
        String key = this.getRegisterVerifiCodeCntCacheKey(phone);
        String result = this.get(key);
        int cnt = 0;
        if (StringUtils.isEmpty(result)) {
            return cnt;
        }
        cnt = Integer.parseInt(result);
        return cnt;
    }

    /**
     * 设置注册验证码次数缓存
     *
     * @param phone
     */
    public void setRegisterVerifiCodeCntCache(String phone) {
        String key = this.getRegisterVerifiCodeCntCacheKey(phone);
        int cnt = this.getRegisterVerifiCodeCntCache(phone);
        this.incr(key);
        if (cnt == 0) {
            this.expire(key, RedisConstant.REGISTER_VERIFI_CODE_CNT_EXPIRES);
        }
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @return
     */
    public String getRegisterVerifiCodeCache(String phone) {
        String key = this.getRegisterVerifiCodeCacheKey(phone);
        String result = this.get(key);
        if (StringUtils.isEmpty(result)) {
            return "";
        }
        return result;
    }

    /**
     * 设置注册验证码缓存
     *
     * @param phone
     */
    public void setRegisterVerifiCodeCache(String phone, String code) {
        String key = this.getRegisterVerifiCodeCacheKey(phone);
        this.set(key, code, RedisConstant.REGISTER_VERIFI_CODE_EXPIRES);
    }





}
