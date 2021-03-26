package com.miguan.laidian.common.util.push;

import com.cgcg.context.SpringContextHolder;
import com.miguan.laidian.common.enums.PushChannel;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.redis.service.RedisDB6Service;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.vo.ClUserVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author shixh
 * @Date 2020/4/16
 **/
public class PushUtil {

    //获取各个厂商的tokens
    public static Map<String, List<String>> getTokensMap(List<ClUserVo> userVoList) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> huaweiTokens = new ArrayList<>();
        List<String> vivoTokens = new ArrayList<>();
        List<String> oppoTokens = new ArrayList<>();
        List<String> xiaomiTokens = new ArrayList<>();
        for (ClUserVo clUserVo : userVoList) {
            if (StringUtils.isNotEmpty(clUserVo.getHuaweiToken())) {
                String huaweiToken = clUserVo.getHuaweiToken();
                if (!huaweiTokens.contains(huaweiToken)){
                    saveTokenToRedis(huaweiToken, clUserVo.getId());
                    huaweiTokens.add(huaweiToken);
                }
            }
            if (StringUtils.isNotEmpty(clUserVo.getVivoToken())) {
                String vivoToken = clUserVo.getVivoToken();
                if (!vivoTokens.contains(vivoToken)){
                    saveTokenToRedis(vivoToken, clUserVo.getId());
                    vivoTokens.add(clUserVo.getVivoToken());
                }
            }
            if (StringUtils.isNotEmpty(clUserVo.getOppoToken())) {
                String oppoToken = clUserVo.getOppoToken();
                if (!oppoTokens.contains(oppoToken)){
                    saveTokenToRedis(oppoToken, clUserVo.getId());
                    oppoTokens.add(clUserVo.getOppoToken());
                }
            }
            if (StringUtils.isNotEmpty(clUserVo.getXiaomiToken())) {
                String xiaomiToken = clUserVo.getXiaomiToken();
                if (!xiaomiTokens.contains(xiaomiToken)){
                    saveTokenToRedis(xiaomiToken, clUserVo.getId());
                    xiaomiTokens.add(clUserVo.getXiaomiToken());
                }
            }
        }
        result.put(PushChannel.HuaWei.name(), huaweiTokens);
        result.put(PushChannel.VIVO.name(), vivoTokens);
        result.put(PushChannel.OPPO.name(), oppoTokens);
        result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        return result;
    }

    //推送token保存到redis
    private static void saveTokenToRedis(String token, Long id) {
        RedisDB6Service redisDB6Service = SpringContextHolder.getBean("redisDB6Service");
        String key = "pushToken:laidian:" + token;
        redisDB6Service.set(key, id, RedisKeyConstant.PUSH_TOKEN_SECONDS);
    }

    //推送有效期，如果实体类有没有配置有效期，取配置表的有效期
    public static Map<String, Object> getExpireTime(PushArticle pushArticle) {
        Map<String, Object> pushParams = new HashMap<>();
        String upush_expireTime = pushArticle.getExpireTime();
        long expireTime = 0L;
        if (!StringUtils.isEmpty(upush_expireTime)) {
            double v = Double.parseDouble(upush_expireTime);
            expireTime = Math.round(v * 60 * 60 * 1000);
        } else {
            upush_expireTime = Global.getValue("uPush_expireTime", pushArticle.getAppType());
            double v = Double.parseDouble(upush_expireTime);
            expireTime = Math.round(v * 60 * 1000);
        }
        pushParams.put("expireTime", expireTime);
        return pushParams;
    }
}
