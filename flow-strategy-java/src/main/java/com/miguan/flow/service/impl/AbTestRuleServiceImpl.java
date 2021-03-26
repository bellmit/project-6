package com.miguan.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.miguan.flow.common.constant.RedisConstant;
import com.miguan.flow.dto.AdvertCodeDto;
import com.miguan.flow.mapper.AbTestRuleMapper;
import com.miguan.flow.mapper.MofangMapper;
import com.miguan.flow.service.AbTestRuleService;
import com.miguan.flow.service.MofangService;
import com.miguan.flow.service.common.RedisService;
import com.miguan.flow.vo.AbTestRuleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description AB测试serviceImpl
 **/
@Service
public class AbTestRuleServiceImpl implements AbTestRuleService{

    @Resource
    private AbTestRuleMapper abTestRuleMapper;
    @Resource
    private RedisService redisService;

    /**
     * 查询AB实验数据(有缓存,每5分钟重新刷新缓存)
     * @param positionType 广告位key
     * @param appPackage 包名
     * @param mobileType  手机类型1*应用端:1-ios，2-安卓
     * @return
     */
    public List<AbTestRuleVo> getABTextAdversByRule(String positionType, String appPackage, String mobileType) {
        Map<String, Object> params = new HashMap<>();
        params.put("positionType", positionType);
        params.put("appPackage", appPackage);
        params.put("mobileType", mobileType);

        String key = RedisConstant.ABTEST_DATA + params.toString();
        String value = redisService.get(key);
        if(RedisConstant.EMPTY_VALUE.equals(value)) {
            return null;
        }
        if(StringUtils.isNotBlank(value)) {
            return JSONArray.parseArray(value, AbTestRuleVo.class);
        } else {
            List<AbTestRuleVo> abTestRuleVos = abTestRuleMapper.getABTextAdversByRule(params);
            String cache = RedisConstant.EMPTY_VALUE;
            if(abTestRuleVos != null && !abTestRuleVos.isEmpty()) {
                cache = JSON.toJSONString(abTestRuleVos);
            }
            redisService.set(key, cache, RedisConstant.DEFALUT_SECONDS);  //把查询结果存入缓存
            return abTestRuleVos;
        }
    }
}
