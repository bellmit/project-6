package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.enums.PushChannel;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.SpringTaskUtil;
import com.miguan.laidian.common.util.push.PushUtil;
import com.miguan.laidian.entity.PushArticle;
import com.miguan.laidian.entity.PushArticleConfig;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.rabbitMQ.util.RabbitMQConstant;
import com.miguan.laidian.redis.service.RedisDB6Service;
import com.miguan.laidian.service.*;
import com.miguan.laidian.vo.ClUserVo;
import com.miguan.laidian.vo.SmallVideoVo;
import com.miguan.laidian.vo.mongodb.XldAutoPushLog;
import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.vivo.push.sdk.notofication.InvalidUser;
import com.vivo.push.sdk.notofication.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PushSeviceImpl implements PushSevice {
    @Resource
    private PushArticleService pushArticleService;

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private PushArticleMobileService pushArticleMobileService;

    @Resource
    private PushArticleSendResultService pushArticleSendResultService;

    @Resource
    private VideoService videoService;

    @Resource
    private SmallVideoService smallVideosService;

    @Resource
    private ClDeviceService clDeviceService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisDB6Service redisDB6Service;

    @Resource(name = "ldpushMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public ResultMap realTimeSendInfo(Long id) {
        try {
            PushArticle pushArticle = pushArticleService.findOneToPush(id);
            if (pushArticle == null || StringUtils.isBlank(pushArticle.getTitle())
                    || StringUtils.isBlank(pushArticle.getNoteContent())) {
                log.error("当前没有符合条件的推送数据");
                return ResultMap.error("当前没有符合条件的推送数据");
            }
            ResultMap resultMap = sendInfoToMobile(pushArticle);
            if (resultMap != null) {
                return resultMap;
            }
        } catch (Exception e) {
            return ResultMap.error("发送失败，请联系管理员");
        }
        return ResultMap.success();
    }

    @Override
    public ResultMap sendInfoToMobile(PushArticle pushArticle) {
        Map<String, String> param = getParaMap(pushArticle);
        Map<String, List<String>> tokensMap;
        //推送有效期，如果实体类有没有配置有效期，取配置表的有效期
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle);
        pushParams.put("pushArticle", pushArticle);
        // 开始条数
        int index = 0;
        //查询参数
        Map<String, Object> params = getParams(pushArticle, index);
        // 查出需要推送的设备总条数
        Integer count = clDeviceService.countClDevice(params);
        //分批查询推送
        while (true) {
            params.put("index", index);
            //获取各个厂商的tokens
            tokensMap = PushUtil.getTokensMap(clDeviceService.findAllTokens(params));
            // 推送所有平台
            puthAllPlatform(pushArticle, param, pushParams, tokensMap);
            if (index >= count)break;
            index += 5000;
        }
        return ResultMap.success();
    }

    private void puthAllPlatform(PushArticle pushArticle, Map<String, String> param, Map<String, Object> pushParams, Map<String, List<String>> tokensMap) {
        //华为广播推送
        this.huaweiPush(pushParams, param, tokensMap);
        //vivo广播推送
        //vivo没有测试账号,测试广播推送会影响到正式服用户
        String appEnvironment = Global.getValue("app_environment", pushArticle.getAppType());
        if ("prod".equals(appEnvironment)) {
            this.vivoPushAll(pushParams, param, tokensMap);
        }
        //oppo广播推送
        this.oppoPush(pushParams, param, tokensMap);
        //小米广播推送
        this.xiaomiPush(pushParams, param, tokensMap);
    }

    private Map<String, Object> getParams(PushArticle pushArticle, int index) {
        Map<String, Object> params = new HashMap<>();
        params.put("appType", pushArticle.getAppType());
        params.put("index",index);
        if (!"-1".equals(pushArticle.getChannelIds())) {
            List<String> channelIds = Arrays.asList(pushArticle.getChannelIds().split(","));
            params.put("channelIds", channelIds);
        } else {
            params.put("channelIds", null);
        }
        return params;
    }

    /**
     * 获取用户token
     * @param params
     * @return
     */
    private List<ClUserVo> getClUserVos(Map<String, Object> params) {
        return clDeviceService.findAllTokens(params);
    }

    @Override
    public ResultMap realTimePushTest(Long id, String tokens, String pushChannel, String pushType) {
        PushArticle pushArticle = pushArticleService.findOneToPush(id);
        if (pushArticle == null || StringUtils.isBlank(pushArticle.getTitle())
                || StringUtils.isBlank(pushArticle.getNoteContent())) {
            log.error("当前没有符合条件的推送数据");
            return ResultMap.error("当前没有符合条件的推送数据");
        }
        Map<String, String> param = getParaMap(pushArticle);
        //推送有效期，如果实体类有没有配置有效期，取配置表的有效期
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle);
        pushParams.put("pushArticle", pushArticle);
        //获取各个厂商的tokens
        Map<String, List<String>> tokensMap = new HashMap<>();
        //tokens有值时，取tokens值，否则取表里的值
        if (StringUtils.isNotEmpty(tokens)) {
            tokensMap.put(pushChannel, Arrays.asList(tokens.split(",")));
            puthAllPlatformTest(pushChannel, pushType, pushArticle, param,  pushParams,  tokensMap);
        } else {
            // 开始条数
            int index = 0;
            //查询参数
            Map<String, Object> params = getParams(pushArticle, index);
            // 查出需要推送的设备总条数
            Integer count = clDeviceService.countClDevice(params);
            //分批查询推送
            while (true) {
                params.put("index", index);
                //获取各个厂商的tokens
                tokensMap = PushUtil.getTokensMap(clDeviceService.findAllTokens(params));
                // 推送所有平台
                puthAllPlatformTest(pushChannel, pushType, pushArticle, param,  pushParams,  tokensMap);
                if (index >= count)break;
                index += 5000;
            }
        }
        return ResultMap.success();
    }

    private void puthAllPlatformTest(String pushChannel, String pushType, PushArticle pushArticle, Map<String, String> param, Map<String, Object> pushParams, Map<String, List<String>> tokensMap) {
        PushChannel channel = PushChannel.val(pushChannel);
        //根据厂商调用相对应的推送接口
        switch (channel) {
            case OPPO:
                this.oppoPush(pushParams, param, tokensMap);//oppo广播推送
                break;
            case XiaoMi:
                this.xiaomiPush(pushParams, param, tokensMap);//小米广播推送
                break;
            case VIVO:
                if ("1".equals(pushType)) {
                    this.vivoPush(pushParams, param, tokensMap);//vivo指定用户推送
                } else {
                    //vivo没有测试账号,测试广播推送会影响到正式服用户
                    String appEnvironment = Global.getValue("app_environment", pushArticle.getAppType());
                    if ("prod".equals(appEnvironment)) {
                        this.vivoPushAll(pushParams, param, tokensMap);//vivo广播推送
                    }
                }
                break;
            case HuaWei:
                this.huaweiPush(pushParams, param, tokensMap);//华为广播推送
                break;
        }
    }

    //华为推送
    private ResultMap huaweiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig huaWei = pushArticleConfigService.findPushArticleConfig(PushChannel.HuaWei.name(), Constant.Android, pushArticle.getAppType());
        if (huaWei == null) return ResultMap.error("推送参数未配置！");
        pushParams.put(PushChannel.HuaWei.name(), huaWei);
        List<String> list = tokensMap.get(PushChannel.HuaWei.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("huaWeiTokens为空，不调用推送接口");
            return ResultMap.error("huaWeiTokens为空，不调用推送接口");
        }
        List<SendResponce> sendResponceList = pushArticleMobileService.huaweiPushInfo(pushParams, param, list);
        if (CollectionUtils.isEmpty(sendResponceList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.HuaWei.name(),"0");
            return ResultMap.error("华为推送失败！");
        }
        for (SendResponce sendResponce : sendResponceList) {
            if (sendResponce != null) {
                saveSendResultInfo(pushArticle, sendResponce.getRequestId(), PushChannel.HuaWei.name(),"1");
            }
        }
        return ResultMap.success();
    }

    //vivo指定用户推送
    private ResultMap vivoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig vivo = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.Android, pushArticle.getAppType());
        if (vivo == null) return ResultMap.error("推送参数未配置！");
        pushParams.put(PushChannel.VIVO.name(), vivo);
        List<String> list = tokensMap.get(PushChannel.VIVO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("vivoTokens为空，不调用推送接口");
            return ResultMap.error("vivoTokens为空，不调用推送接口");
        }
        List<Result> resultList = pushArticleMobileService.vivoPushByRegIds(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.VIVO.name(),"0");
            return ResultMap.error("vivo指定用户推送失败");
        }
        for (Result result : resultList) {
            if (result != null && result.getResult() == 0) {
                saveSendResultInfo(pushArticle, result.getTaskId(), PushChannel.VIVO.name(),"1");
                vivoPushErrorToken(result);
            }
        }
        return ResultMap.success();
    }

    //vivo广播推送
    private ResultMap vivoPushAll(Map<String, Object> pushParams, Map<String, String> param, Map<String, List<String>> tokensMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig vivo = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.Android, pushArticle.getAppType());
        pushParams.put(PushChannel.VIVO.name(), vivo);
        List<String> list = tokensMap.get(PushChannel.VIVO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("vivoTokens为空，不调用推送接口");
            return ResultMap.error("vivoTokens为空，不调用推送接口");
        }
        List<Result> resultList = pushArticleMobileService.vivoPushAll(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.VIVO.name(),"0");
            return ResultMap.error("vivo广播推送失败");
        }
        for (Result result : resultList) {
            if (result != null && result.getResult() == 0) {
                saveSendResultInfo(pushArticle, result.getTaskId(), PushChannel.VIVO.name(),"1");
                vivoPushErrorToken(result);
            }
        }
        return ResultMap.success();
    }

    /**
     * vivo无效token保存redis
     * @param result
     */
    private void vivoPushErrorToken(Result result) {
        List<InvalidUser> invalidUsers = result.getInvalidUsers();
        if (CollectionUtils.isNotEmpty(invalidUsers)) {
            String dataStr = JSON.toJSONString(invalidUsers);
            rabbitTemplate.convertAndSend(RabbitMQConstant.PUSH_VIVO_ERROR_EXCHANGE, RabbitMQConstant.PUSH_VIVO_ERROR_KEY, dataStr);
        }
    }

    @Override
    public void vivoInvalidUserSave(List<InvalidUser> invalidUsers) {
        for (InvalidUser invalidUser : invalidUsers) {
            if (invalidUser.getStatus() == 1 || invalidUser.getStatus() == 2) {
                String key = "pushVivoToken:laidian";
                redisDB6Service.lpush(key, invalidUser.getUserid());
            }
        }
    }

    //oppo推送
    private ResultMap oppoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig oppo = pushArticleConfigService.findPushArticleConfig(PushChannel.OPPO.name(), Constant.Android, pushArticle.getAppType());
        pushParams.put(PushChannel.OPPO.name(), oppo);
        List<String> list = tokensMap.get(PushChannel.OPPO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("oppoTokens为空，不调用推送接口");
            return ResultMap.error("oppoTokens为空，不调用推送接口");
        }
        List<com.oppo.push.server.Result> resultList = pushArticleMobileService.oppoPushByRegIds(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.OPPO.name(),"0");
            return ResultMap.error("OPPO推送失败");
        }
        for (com.oppo.push.server.Result result : resultList) {
            if (result != null) {
                String taskId = result.getTaskId() == null ? result.getMessageId() : result.getTaskId();
                saveSendResultInfo(pushArticle, taskId, PushChannel.OPPO.name(),"1");
            }
        }
        return ResultMap.success();
    }

    //小米推送
    private ResultMap xiaomiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig xiaomi = pushArticleConfigService.findPushArticleConfig(PushChannel.XiaoMi.name(), Constant.Android, pushArticle.getAppType());
        pushParams.put(PushChannel.XiaoMi.name(), xiaomi);
        List<String> list = tokensMap.get(PushChannel.XiaoMi.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("xiaomiTokens为空，不调用推送接口");
            return ResultMap.error("xiaomiTokens为空，不调用推送接口");
        }
        List<com.xiaomi.xmpush.server.Result> resultList = pushArticleMobileService.xiaomiPushInfo(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.XiaoMi.name(),"0");
            return ResultMap.error("小米推送失败");
        }
        for (com.xiaomi.xmpush.server.Result result : resultList) {
            if (result != null) {
                saveSendResultInfo(pushArticle, result.getMessageId(), PushChannel.XiaoMi.name(),"1");
            }
        }
        return ResultMap.success();
    }

    /**
     * 保存推送信息结果
     *
     * @param pushArticle
     * @param businessId
     * @param pushChannel
     */
    private void saveSendResultInfo(PushArticle pushArticle, String businessId, String pushChannel, String isSucc) {
        //存储到mongo日志
        List<XldAutoPushLog> autoPushLogs = Lists.newArrayList();
        try {
            XldAutoPushLog autoPushLog = new XldAutoPushLog();
            autoPushLog.setPush_id(pushArticle.getId());
            autoPushLog.setApp_package(pushArticle.getAppPackage());
            autoPushLog.setTitle(pushArticle.getTitle());
            autoPushLog.setType("0");
            autoPushLog.setContent(JSON.toJSONString(pushArticle));
            autoPushLog.setPush_channel(pushChannel);
            autoPushLog.setIs_succ(isSucc);
            autoPushLogs.add(autoPushLog);
            saveAutoPushInfoToMongo(autoPushLogs);
        } catch (Exception e) {
            log.error("【手动推送】保存monggo日志,日志数据：" + JSON.toJSONString(autoPushLogs) +  ";异常:" + e.getMessage(), e);
        }

        if(Integer.parseInt(isSucc) == 1){
            try {
                pushArticleSendResultService.saveSendResult(pushArticle.getId(), pushChannel, businessId, pushArticle.getAppType());
            } catch (Exception e) {
                log.error("保存推送参数:" + JSON.toJSONString(pushArticle));
                log.error("保存推送参数:" + "businessId=" + businessId + ",pushChannel=" + pushChannel);
                log.error("保存推送息异常:" + e.getMessage(), e);
            }
        }
    }

    //组装自定义参数
    public Map<String, String> getParaMap(PushArticle pushArticle) {
        Map<String, String> param = new HashMap<>();
        param.put("xy_id", pushArticle.getId() + "");
        param.put("xy_type", pushArticle.getType() + "");
        param.put("xy_typeValue", pushArticle.getTypeValue() == null ? "" : pushArticle.getTypeValue());
        param.put("xy_title", pushArticle.getTitle() + "");
        param.put("xy_noteContent", pushArticle.getNoteContent() == null ? "" : pushArticle.getNoteContent());
        param.put("xy_sendTime", SpringTaskUtil.getMillisecond(pushArticle.getPushTime()) + "");
        param.put("intent_url", "huaweipush://com.mg.phonecall.push/notify_detail");
        if (pushArticle.getType() == 3 || pushArticle.getType() == 4) {
            Map<String, Object> params = new HashMap<>();
            params.put("state", Constant.open);
            params.put("id", Integer.parseInt(pushArticle.getTypeValue()));
            params.put("appType", pushArticle.getAppType());
            if (pushArticle.getType() == 3) {
                Video videosVo = videoService.findOne(params);
                if (videosVo != null) {
                    param.put("thumbnail_url", videosVo.getBsyImgUrl() == null ? "" : videosVo.getBsyImgUrl());
                }
            } else {
                SmallVideoVo smallVideoVo = smallVideosService.findVideosDetailByOne(params);
                if (smallVideoVo != null) {
                    param.put("thumbnail_url", smallVideoVo.getBsyImgUrl() == null ? "" : smallVideoVo.getBsyImgUrl());
                }
            }
        }
        param.put("push_method", "0");
        return param;
    }

    private void saveAutoPushInfoToMongo(List<XldAutoPushLog> autoPushLogs) {
        mongoTemplate.insert(autoPushLogs,Constant.XLD_AUTO_PUSH_LOG);
    }

}
