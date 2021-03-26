package com.miguan.ballvideo.common.util.push;

import com.cgcg.context.SpringContextHolder;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.SpringTaskUtil;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisDB6Service;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.ClUserVo;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

/**
 * @Author shixh
 * @Date 2020/4/16
 **/
public class PushUtil {

    //组装自定义参数
    public static Map<String, String> getParaMap(PushArticle pushArticle, Integer pushMethod) {
        Map<String, String> param = new HashMap<>();
        param.put("xy_id", pushArticle.getId() + "");
        param.put("xy_type", pushArticle.getType() + "");
        param.put("xy_typeValue", pushArticle.getTypeValue() == null ? "" : pushArticle.getTypeValue());
        param.put("xy_title", pushArticle.getTitle() + "");
        param.put("xy_noteContent", pushArticle.getNoteContent() == null ? "" : pushArticle.getNoteContent());
        param.put("xy_sendTime", SpringTaskUtil.getMillisecond(pushArticle.getPushTime()) + "");
        param.put("thumbnail_url", pushArticle.getThumbnailUrl() == null ? "" : pushArticle.getThumbnailUrl());
        param.put("intent_url", "intent://com.mg.xyvideo.module.home/notify_detail");
        param.put("push_method", ""+pushMethod);
        return param;
    }



    //获取各个厂商的tokens，全部用户
    public static Map<String, List<String>> getTokensMapByUser(List<ClUserVo> userVoList) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> huaweiTokens = new ArrayList<>();
        List<String> oppoTokens = new ArrayList<>();
        List<String> vivoTokens = new ArrayList<>();
        List<String> xiaomiTokens = new ArrayList<>();
        for (ClUserVo clUserVo : userVoList) {
            if (StringUtils.isNotEmpty(clUserVo.getHuaweiToken())) {
                huaweiTokens.add(clUserVo.getHuaweiToken());
            }
            if (StringUtils.isNotEmpty(clUserVo.getOppoToken())) {
                oppoTokens.add(clUserVo.getOppoToken());
            }
            if (StringUtils.isNotEmpty(clUserVo.getVivoToken())) {
                vivoTokens.add(clUserVo.getVivoToken());
            }
            if (StringUtils.isNotEmpty(clUserVo.getXiaomiToken())) {
                xiaomiTokens.add(clUserVo.getXiaomiToken());
            }
        }
        result.put(PushChannel.HuaWei.name(), huaweiTokens);
        result.put(PushChannel.OPPO.name(), oppoTokens);
        result.put(PushChannel.VIVO.name(),vivoTokens);
        result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        return result;
    }

    //推送token保存到redis
    private static void saveTokenToRedis(String token, Long id) {
        RedisDB6Service redisDB6Service = SpringContextHolder.getBean("redisDB6Service");
        String key = "pushToken:ballvideo:" + token;
        redisDB6Service.set(key, id, RedisKeyConstant.PUSH_TOKEN_SECONDS);
    }

    //获取各个厂商的tokens，全部设备
    public static Map<String, List<String>> getTokensMapByDevice(List<ClDeviceVo> clDeviceVoList) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> huaweiTokens = new ArrayList<>();
        List<String> oppoTokens = new ArrayList<>();
        List<String> vivoTokens = new ArrayList<>();
        List<String> xiaomiTokens = new ArrayList<>();
        List<String> youmengTokens = new ArrayList<>();
        // 排重处理
        Set<ClDeviceVo> clDeviceVoSet = new HashSet<>(clDeviceVoList);
        clDeviceVoList.clear();
        clDeviceVoList.addAll(clDeviceVoSet);
        for (ClDeviceVo clDeviceVo : clDeviceVoList) {
            if (StringUtils.isNotEmpty(clDeviceVo.getHuaweiToken())) {
                saveTokenToRedis(clDeviceVo.getHuaweiToken(), clDeviceVo.getId());
                huaweiTokens.add(clDeviceVo.getHuaweiToken());
            }
            if (StringUtils.isNotEmpty(clDeviceVo.getOppoToken())) {
                saveTokenToRedis(clDeviceVo.getOppoToken(), clDeviceVo.getId());
                oppoTokens.add(clDeviceVo.getOppoToken());
            }
            if (StringUtils.isNotEmpty(clDeviceVo.getVivoToken())) {
                saveTokenToRedis(clDeviceVo.getVivoToken(), clDeviceVo.getId());
                vivoTokens.add(clDeviceVo.getVivoToken());
            }
            if (StringUtils.isNotEmpty(clDeviceVo.getXiaomiToken())) {
                saveTokenToRedis(clDeviceVo.getXiaomiToken(), clDeviceVo.getId());
                xiaomiTokens.add(clDeviceVo.getXiaomiToken());
            }
            if (StringUtils.isNotEmpty(clDeviceVo.getDeviceToken())) {
                saveTokenToRedis(clDeviceVo.getDeviceToken(), clDeviceVo.getId());
                youmengTokens.add(clDeviceVo.getDeviceToken());
            }
        }
        result.put(PushChannel.YouMeng.name(),youmengTokens);
        result.put(PushChannel.HuaWei.name(), huaweiTokens);
        result.put(PushChannel.OPPO.name(), oppoTokens);
        result.put(PushChannel.VIVO.name(),vivoTokens);
        result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        return result;
    }

    //获取各个厂商的tokens，指定用户（从 pushArticle 获取）
    public static Map<String, List<String>> getTokensMap(PushArticle pushArticle) {
        Map<String, List<String>> result = new HashMap<>();
        if (StringUtils.isNotEmpty(pushArticle.getHuaweiTokens())) {
            List<String> huaweiTokens = new ArrayList<>();
            huaweiTokens.add(pushArticle.getHuaweiTokens());
            result.put(PushChannel.HuaWei.name(), huaweiTokens);
        }
        if (StringUtils.isNotEmpty(pushArticle.getVivoTokens())) {
            List<String> vivoTokens = new ArrayList<>();
            vivoTokens.add(pushArticle.getVivoTokens());
            result.put(PushChannel.VIVO.name(), vivoTokens);
        }
        if (StringUtils.isNotEmpty(pushArticle.getOppoTokens())) {
            List<String> oppoTokens = new ArrayList<>();
            oppoTokens.add(pushArticle.getOppoTokens());
            result.put(PushChannel.OPPO.name(), oppoTokens);
        }
        if (StringUtils.isNotEmpty(pushArticle.getXiaomiTokens())) {
            List<String> xiaomiTokens = new ArrayList<>();
            xiaomiTokens.add(pushArticle.getXiaomiTokens());
            result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        }
        return result;
    }

    //推送有效期，如果实体类有没有配置有效期，取配置表的有效期
    public static Map<String, Object> getExpireTime(String upush_expireTime) {
        Map<String, Object> pushParams = new HashMap<>();
        long expireTime = 0L;
        if (!StringUtils.isEmpty(upush_expireTime)) {
            double v = Double.parseDouble(upush_expireTime);
            expireTime = Math.round(v * 60 * 60 * 1000);
        } else {
            upush_expireTime = Global.getValue("uPush_expireTime");
            double v = Double.parseDouble(upush_expireTime);
            expireTime = Math.round(v * 60 * 1000);
        }
        pushParams.put("expireTime", expireTime);
        return pushParams;
    }
}
