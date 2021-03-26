package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.enums.PushChannel;
import com.miguan.laidian.common.util.HttpUtils;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.PushArticleConfig;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.PushArticleConfigService;
import com.miguan.laidian.service.TaskSearchService;
import com.vivo.push.sdk.notofication.Result;
import com.vivo.push.sdk.server.Sender;
import com.xiaomi.xmpush.server.Tracer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author shixh
 * @Date 2020/4/13
 **/
@Service
public class TaskSearchServiceImpl implements TaskSearchService {

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private RedisService redisService;
    /**
     * vivo返回参数说明：
     * |send -下发总数  |
     * |receive-到达总数  |
     * |display-展示总数  |
     * |click-点击总数  |
     *
     * @param appType
     * @param taskId
     * @return
     * @throws Exception
     */
    @Override
    public ResultMap searchVivo(String taskId, String appType) throws Exception {
        PushArticleConfig pushArticleConfig = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.Android, appType);
        if (pushArticleConfig == null)
            return ResultMap.error("马甲包" + appType + "的" + PushChannel.VIVO.name() + "缺少推送配置数据");
        String appSecret = pushArticleConfig.getAppSecret();
        String appKey = pushArticleConfig.getAppKey();
        int appId = StringUtils.isEmpty(pushArticleConfig.getAppId()) ? 0 : Integer.parseInt(pushArticleConfig.getAppId());
        Sender sender = new Sender(appSecret);
        sender.initPool(20, 10);
        String authToken = redisService.get(RedisKeyConstant.VIVO_TOKEN + pushArticleConfig.getAppId(), String.class);
        if (StringUtils.isEmpty(authToken)) {
            Result result = sender.getToken(appId, appKey);
            int code = result.getResult();
            if (code != 0) return ResultMap.error(result);
            authToken = result.getAuthToken();
            redisService.set(RedisKeyConstant.VIVO_TOKEN + pushArticleConfig.getAppId(), authToken, 3600);
        }

        sender.setAuthToken(authToken);
        Set<String> taskIds = new HashSet<>();
        taskIds.add(taskId);
        Result result = sender.getStatistics(taskIds);
        int code = result.getResult();
        if (code != 0) return ResultMap.error(result);
        return ResultMap.success(result.getStatistics());
    }

    @Override
    public ResultMap searchYouMeng(String taskId, String appType) throws Exception {
        String mobileType = Constant.appXld.equals(appType) || Constant.appWld.equals(appType) ? Constant.Android : Constant.IOS;
        PushArticleConfig pushArticleConfig = pushArticleConfigService.findPushArticleConfig(PushChannel.YouMeng.name(), mobileType, appType);
        if (pushArticleConfig == null)
            return ResultMap.error("马甲包" + appType + "的" + PushChannel.YouMeng.name() + "缺少推送配置数据");
        String url = "http://msg.umeng.com/api/status";
        Map<String, String> map = new HashMap<>();
        map.put("appkey", pushArticleConfig.getAppKey());
        map.put("timestamp", System.currentTimeMillis() + 1000 * 60 * 10 + "");//10分钟有效期
        map.put("task_id", taskId);
        String postBody = JSON.toJSONString(map);
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + pushArticleConfig.getAppSecret()).getBytes("utf8"));
        url = url + "?sign=" + sign;
        String result = HttpUtils.post(url, postBody);
        if (result != null) {
            HashMap hashMap = JSON.parseObject(result, HashMap.class);
            return ResultMap.success(hashMap);
        }
        return ResultMap.error("请求失败");
    }

    /**
     * xiaomi返回参数说明：
     * id表示单条消息的id，
     * delivered表示消息的送达数，
     * resolved表示消息的计划送达数，
     * delivery_rate表示送达率，
     * click表示消息的点击数，
     * click_rate表示消息的点击率，
     * create_time表示消息的发送时间，
     * time_to_live表示消息的有效期
     *
     * @param taskId
     * @param appType
     * @return
     * @throws Exception
     */
    @Override
    public ResultMap searchXiaoMi(String taskId, String appType) throws Exception {
        PushArticleConfig pushArticleConfig = pushArticleConfigService.findPushArticleConfig(PushChannel.XiaoMi.name(), Constant.Android, appType);
        if (pushArticleConfig == null)
            return ResultMap.error("马甲包" + appType + "的" + PushChannel.XiaoMi.name() + "缺少推送配置数据");
        Tracer tracer = new Tracer(pushArticleConfig.getAppSecret()); // 使用AppSecretKey创建一个Tracer对象
        // 获取单条消息的送达状态， 参数： msgId, retry次数
        String messageStatus = tracer.getMessageStatus(taskId, 0);//retries重试次数
        if (messageStatus.contains("error")) {
            HashMap hashMap = JSON.parseObject(messageStatus, HashMap.class);
            return ResultMap.error(hashMap);
        }
        HashMap hashMap = JSON.parseObject(messageStatus, HashMap.class);
        return ResultMap.success(hashMap);
    }

}
