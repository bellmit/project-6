package com.miguan.flow.service.impl;

import com.miguan.flow.common.constant.FlowConstant;
import com.miguan.flow.common.constant.RedisConstant;
import com.miguan.flow.mapper.MofangMapper;
import com.miguan.flow.service.AdvCodeService;
import com.miguan.flow.service.MofangService;
import com.miguan.flow.service.common.RedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description 魔方serviceImpl
 **/
@Service
public class MofangServiceImpl implements MofangService{

    @Resource
    private MofangMapper mofangMapper;
    @Resource
    private RedisService redisService;


    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    public boolean stoppedByMofang(Map<String, Object> param) {
        String key = RedisConstant.SHIELD_CHANNEL + param.toString();
        String value = redisService.get(key);
        if(StringUtils.isNotBlank(value)) {
            return Boolean.valueOf(value);
        } else {
            String mobileType = param.get("mobileType") + "";
            int tagType = FlowConstant.IOS.equals(mobileType) ? 2 : 1;
            param.put("tagType", tagType);
            int count1 = mofangMapper.countVersion(param);
            //根据版本判断是否屏蔽全部广告
            if (count1 > 0) {
                redisService.set(key, String.valueOf(true), RedisConstant.DEFALUT_SECONDS);
                return true;
            }
            //非全部的屏蔽根据渠道查询是否屏蔽广告
            int count2 = mofangMapper.countChannel(param);
            if (count2 > 0) {
                redisService.set(key, String.valueOf(true), RedisConstant.DEFALUT_SECONDS);
                return true;
            }
            redisService.set(key, String.valueOf(false), RedisConstant.DEFALUT_SECONDS);
            return false;
        }

    }
}
