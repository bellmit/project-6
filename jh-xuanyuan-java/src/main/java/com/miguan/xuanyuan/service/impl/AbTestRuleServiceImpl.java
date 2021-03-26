package com.miguan.xuanyuan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.mapper.AbTestRuleMapper;
import com.miguan.xuanyuan.service.AbTestRuleService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.sdk.AbTestRuleVo;
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
public class AbTestRuleServiceImpl implements AbTestRuleService {

    @Resource
    private AbTestRuleMapper abTestRuleMapper;
    @Resource
    private RedisService redisService;

    /**
     * 查询AB实验数据(有缓存,每5分钟重新刷新缓存)
     * @param positionKey 广告位KEY
     * @param appKey 包名
     * @param mobileType  手机类型1*应用端:1-安卓，2-ios
     * @return
     */
    public List<AbTestRuleVo> getABTextAdversByRule(String positionKey, String appKey, String mobileType) {
        Map<String, Object> params = new HashMap<>();
        params.put("positionKey", positionKey);
        params.put("appKey", appKey);
        params.put("clientType", XyConstant.IOS.equals(mobileType) ? 2 : 1);

        String key = RedisConstant.XY_AB_TEST_DATA + params.toString();
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
