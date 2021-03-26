package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.constants.PushArticleConstant;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.push.PushUtil;
import com.miguan.ballvideo.entity.PushArticle;
import com.miguan.ballvideo.entity.PushArticleConfig;
import com.miguan.ballvideo.entity.UserBuryingPointGarbage;
import com.miguan.ballvideo.mapper.PushResultCountMapper;
import com.miguan.ballvideo.rabbitMQ.listener.common.ProducerMqCallers;
import com.miguan.ballvideo.rabbitMQ.util.RabbitMQConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.repositories.BuryingPointGarbageRepository;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.mongodb.AutoPushLog;
import com.miguan.ballvideo.vo.push.PushResultCountVo;
import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.vivo.push.sdk.notofication.InvalidUser;
import com.vivo.push.sdk.notofication.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PushSeviceImpl implements PushSevice {
    @Resource
    private PushArticleService pushArticleService;

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private ClDeviceService clDeviceService;

    @Resource
    private PushArticleMobileService pushArticleMobileService;

    @Resource
    private PushArticleSendResultService pushArticleSendResultService;

    @Resource
    private ClUserOpinionService clUserOpinionService;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisDB6Service redisDB6Service;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private PushResultCountMapper pushResultCountMapper;

    @Resource(name = "xypushMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Resource
    private BuryingPointGarbageRepository garbageRepository;

    @Resource
    private ProducerMqCallers producerMqCallers;

    @Override
    public ResultMap realTimeSendInfo(Long id, List<String> distinctIds) {
        try {
            PushArticle pushArticle = pushArticleService.findOneToPush(id);
            if (pushArticle == null || StringUtils.isBlank(pushArticle.getTitle())
                    || StringUtils.isBlank(pushArticle.getNoteContent())) {
                log.error("当前没有符合条件的推送数据");
                return ResultMap.error("当前没有符合条件的推送数据");
            }
            //如果distinctIds不为空，则已经推送给大数据获取定向兴趣用户。则不能重复统计
            if(distinctIds == null){
                // 实时推送前，新增到系统消息中去 saveClUserOpinionPlus
                clUserOpinionService.saveClUserOpinionByPushArticle(pushArticle);
            }


            ResultMap resultMap;
            /* 旧的写法限制了 IOS 的 appPackage 名称，新增常量 List，判断包名称的时候，取 List 来判断 */
            //if (Constant.IOSPACKAGE.equals(pushArticle.getAppPackage())) {
            if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
                resultMap = sendInfoToIOS(pushArticle,distinctIds);
            } else {
                resultMap = sendInfoToAndroid(pushArticle,distinctIds);
            }
            if (resultMap != null) {
                return resultMap;
            }
        } catch (Exception e) {
            log.error("通知栏推送出错======>" + e.getMessage());
            return ResultMap.error("发送失败，请联系管理员");
        }
        return ResultMap.success();
    }

    /**
     * 定时器推送任务系统信息
     */
    //@Scheduled(cron = "0 30 8 * * ?")
    private void taskPushInfo1() {
        pushInfo();
    }

    /**
     * 定时器推送任务系统信息
     */
    //@Scheduled(cron = "0 30 12 * * ?")
    private void taskPushInfo2() {
        pushInfo();
    }

    /**
     * 定时器推送任务系统信息
     */
    //@Scheduled(cron = "0 0 19 * * ?")
    private void taskPushInfo3() {
        pushInfo();
    }

    /**
     * 定时器推送任务系统信息
     */
    //@Scheduled(cron = "0 0 22 * * ?")
    private void taskPushInfo4() {
        pushInfo();
    }

    private void pushInfo() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.TASK_PUSH_CLOCK, RedisKeyConstant.defalut_seconds);
        if (redisLock.lock()) {
            log.info("定时器推送任务系统信息开始----");
            String pushTime = DateUtil.parseDateToStr(new Date(), DateUtil.DATEFORMAT_STR_010);
            handTimeSendInfo(pushTime);
        }
    }

    private ResultMap handTimeSendInfo(String pushTime) {
        try {
            List<PushArticle> pushArticles = pushArticleService.findByType(5);
            if (CollectionUtils.isNotEmpty(pushArticles)) {
                for (PushArticle pushArticle : pushArticles) {
                    if (pushArticle == null || StringUtils.isBlank(pushArticle.getTitle())
                            || StringUtils.isBlank(pushArticle.getNoteContent())) {
                        log.error("当前没有符合条件的推送数据");
                        return ResultMap.error("当前没有符合条件的推送数据");
                    }
                    pushArticle.setPushTime(pushTime);
                    // 实时推送前，新增到系统消息中去 saveClUserOpinionPlus
                    clUserOpinionService.saveClUserOpinionByPushArticle(pushArticle);
                    ResultMap resultMap;
                    if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
                        resultMap = sendInfoToIOS(pushArticle,null);
                    } else {
                        resultMap = sendInfoToAndroid(pushArticle,null);
                    }
                    if (resultMap != null) {
                        return resultMap;
                    }
                }
            }
        } catch (Exception e) {
            return ResultMap.error("发送失败，请联系管理员");
        }
        return ResultMap.success();
    }

    @Override
    public ResultMap sendInfoToIOS(PushArticle pushArticle, List<String> distinctIds) {
        log.info("IOS通知栏推送------->" + pushArticle.toString());
        Map<String, String> param = PushUtil.getParaMap(pushArticle,0);
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle.getExpireTime());
        pushParams.put("pushArticle", pushArticle);
        if (consumeInterestCat(pushArticle, distinctIds)) {
            return null;
        }
        return youMengPush(pushParams, param, distinctIds);
    }

    @Override
    public ResultMap sendInfoToAndroid(PushArticle pushArticle, List<String> distinctIds) {
        log.info("Android通知栏推送------->id:{},pushArticle:{},distinctIds:{}" + pushArticle.getId(),JSON.toJSONString(pushArticle),JSON.toJSONString(distinctIds));
        Map<String, String> param = PushUtil.getParaMap(pushArticle,0);
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle.getExpireTime());
        pushParams.put("pushArticle", pushArticle);
        pushParams.put("packageName", "com.mg.xyvideo.module.main.MainActivity");
        Map<String, List<String>> tokensMap;
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
            log.info("########Android指定用户推送########");
            tokensMap = PushUtil.getTokensMap(pushArticle);
            // 推送所有平台
            puthAllPlatform(pushParams, param, tokensMap);
        } else {
            if (consumeInterestCat(pushArticle, distinctIds)) {
                return null;
            }
            //友盟广播推送 不需要获取所有的设备token 直接推送
            this.youMengPush(pushParams, param, distinctIds);

            // 每次查出5000条设备token数据，处理一次推送
            Map<String, Object> userParam = new HashMap<>();
            userParam.put("appPackage", pushArticle.getAppPackage());
            userParam.put("distinctIds",distinctIds);
            log.info("########Android全部/兴趣用户推送########");
            // 开始条数
            int index = 0;
            while (true) {
                userParam.put("index", index);
                //全员推送改为从设备表取得数据
                //获取推送用户
                List<ClDeviceVo> clDeviceVos = getClDeviceVosByUserType(pushArticle, distinctIds, userParam);
                if(CollectionUtils.isEmpty(clDeviceVos)){
                    return null;
                }
                tokensMap = PushUtil.getTokensMapByDevice(clDeviceVos);
                log.info("【自动推送】全部/兴趣用户推送，tokensMap：{}", JSON.toJSONString(tokensMap));
                // 推送所有平台
                puthAllPlatform(pushParams, param, tokensMap);
                if (index >= clDeviceVos.size())break;
                index += 5000;
            }
        }
        return null;
    }

    @Override
    public ResultMap sendInfoToCleanPage(PushArticle pushArticle) {
        log.info("Android垃圾清理通知栏推送------->" + pushArticle.toString());
        Map<String, String> param = PushUtil.getParaMap(pushArticle,0);
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle.getExpireTime());
        pushParams.put("pushArticle", pushArticle);
        pushParams.put("packageName", "com.mg.xyvideo.module.main.MainActivity");
        Map<String, List<String>> tokensMap;
        // 每次查出5000条设备token数据，处理一次推送
        Map<String, Object> userParam = new HashMap<>();
        userParam.put("appPackage", pushArticle.getAppPackage());
        // 查出需要推送的设备总条数
        Integer count = garbageRepository.findDeviceIdCount();
        // 开始条数
        int index = 0;
        while (true) {
            List<UserBuryingPointGarbage> garbages = garbageRepository.findDeviceIdInfo(index);
            if (CollectionUtils.isEmpty(garbages)) {
                return null;
            }
            String ids = garbages.stream().map(d -> d.getDeviceId().toString()).collect(Collectors.joining(","));
            userParam.put("ids", ids);
            userParam.put("index", index);
            //全员推送改为从设备表取得数据
            tokensMap = PushUtil.getTokensMapByDevice(clDeviceService.findAllTokens(userParam));
            // 推送所有平台
            puthAllPlatform(pushParams, param, tokensMap);
            if (index >= count) {
                break;
            }
            index += 5000;
        }
        return null;
    }

    public void puthAllPlatform (Map<String, Object> pushParams, Map<String, String> param, Map<String, List<String>> tokensMap) {
        //华为广播推送
        this.huaweiPush(pushParams, param, tokensMap);
        //vivo广播推送
        this.vivoPush(pushParams, param, tokensMap);
        //oppo广播推送
        this.oppoPush(pushParams, param, tokensMap);
        //小米广播推送
        this.xiaomiPush(pushParams, param, tokensMap);
    }

    @Override
    public ResultMap realTimePushTest(Long id, String tokens, String pushChannel,List<String> distinctIds) {
        PushArticle pushArticle = pushArticleService.findOneToPush(id);
        if (pushArticle == null || StringUtils.isBlank(pushArticle.getTitle())
                || StringUtils.isBlank(pushArticle.getNoteContent())) {
            return ResultMap.error("当前没有符合条件的推送数据");
        }
        Map<String, String> param = PushUtil.getParaMap(pushArticle,0);
        //获取各个厂商的tokens,tokens有值时，取tokens值，否则取表里的值
        Map<String, List<String>> tokensMap = new HashMap<>();
        if (StringUtils.isNotEmpty(tokens)) {
            tokensMap.put(pushChannel, Arrays.asList(tokens.split(",")));
        } else {
            if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
                tokensMap = PushUtil.getTokensMap(pushArticle);
            } else {
                List<ClDeviceVo> clDeviceVos = Lists.newArrayList();
                Map<String, Object> userParam = new HashMap<>();
                userParam.put("appPackage", pushArticle.getAppPackage());
                if(PushArticleConstant.USER_TYPE_INTEREST_CAT.equals(pushArticle.getUserType())){
                    //兴趣人群推送
                    userParam.put("distinctIds",distinctIds);
                    clDeviceVos = clDeviceService.findAllTokensByDistinct(userParam);
                }else{
                    //全员推送
                    clDeviceVos = clDeviceService.findAllTokens(userParam);
                }
                tokensMap = PushUtil.getTokensMapByDevice(clDeviceVos);
            }
        }
        PushChannel channel = PushChannel.val(pushChannel);
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle.getExpireTime());
        pushParams.put("pushArticle", pushArticle);
        pushParams.put("packageName", "com.mg.xyvideo.module.main.MainActivity");
        //根据厂商调用相对应的推送接口
        switch (channel) {
            case YouMeng:
                return this.youMengPush(pushParams, param, distinctIds);//youMeng广播推送
            case OPPO:
                return this.oppoPush(pushParams, param, tokensMap);//oppo广播推送
            case XiaoMi:
                return this.xiaomiPush(pushParams, param, tokensMap);//小米广播推送
            case VIVO:
                return this.vivoPush(pushParams, param, tokensMap);//vivo指定用户推送
            case HuaWei:
                return this.huaweiPush(pushParams, param, tokensMap);//华为广播推送
            default:
                return ResultMap.error("channel=" + channel + "未配置推送。");
        }
    }

    //友盟推送
    private ResultMap youMengPush(Map<String, Object> pushParams, Map<String, String> param, List<String> distinctIds) {
        log.info("########youMeng友盟用户推送########");
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType()) && StringUtils.isBlank(pushArticle.getDeviceTokens())) {
            return null;
        }
        String mobileType = Constant.ANDROID;

        if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
            mobileType = Constant.IOS;
        }
        PushArticleConfig youMeng = pushArticleConfigService.findPushArticleConfig(PushChannel.YouMeng.name(), mobileType, pushArticle.getAppPackage());
        log.info("【用户推送】参数配置：{}",JSON.toJSONString(youMeng));
        if (youMeng == null) return ResultMap.error("友盟推送参数未配置！");

        pushParams.put(PushChannel.YouMeng.name(), youMeng);

        List<Map> resultList = Lists.newArrayList();
        try {
            //封装deviceTokens
            String deviceTokens = null;
            if(PushArticleConstant.USER_TYPE_INTEREST_CAT.equals(pushArticle.getUserType())){
                // 每次查出5000条设备token数据，处理一次推送
                Map<String, Object> userParam = new HashMap<>();
                userParam.put("appPackage", pushArticle.getAppPackage());
                userParam.put("distinctIds",distinctIds);
                log.info("【用户推送】兴趣人群推送，userParam：{}",JSON.toJSONString(userParam));
                // 开始条数
                int index = 0;
                while (true) {
                    userParam.put("index", index);
                    //获取推送用户
                    List<ClDeviceVo> clDeviceVos = getClDeviceVosByUserType(pushArticle,distinctIds,userParam);
                    log.info("【用户推送】友盟推送clDeviceVos：{}",JSON.toJSONString(clDeviceVos));
                    if(CollectionUtils.isEmpty(clDeviceVos)){
                        break;
                    }
                    deviceTokens = clDeviceVos.stream().filter((clDevice -> StringUtils.isNotEmpty(clDevice.getDeviceToken())))
                            .map(clDeviceVo -> clDeviceVo.getDeviceToken()).collect(Collectors.joining(","));
                    log.info("【用户推送】友盟推送deviceTokens：{}", deviceTokens);
                    if(StringUtils.isNotEmpty(deviceTokens)){
                        if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
                            resultList = pushArticleMobileService.youMengIosPushInfo(pushParams, param, deviceTokens);
                        } else {
                            resultList = pushArticleMobileService.youMengAndroidPushInfo(pushParams, param, deviceTokens);
                        }
                    }
                    if (index >= clDeviceVos.size())break;
                    index += 5000;
                }

            }else{
                if(PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
                    //指定用户推送
                    deviceTokens = pushArticle.getDeviceTokens();
                }
                log.info("【用户推送】定向/指定用户推送，deviceTokens：{}",deviceTokens);

                if (Constant.IOSPACKAGELIST.contains(pushArticle.getAppPackage())) {
                    resultList = pushArticleMobileService.youMengIosPushInfo(pushParams, param, deviceTokens);
                } else {
                    resultList = pushArticleMobileService.youMengAndroidPushInfo(pushParams, param, deviceTokens);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // todo  如果为空也代表没数据
        log.info("【用户推送】返回结果：{}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.YouMeng.name(),"0");
            return ResultMap.error("友盟推送失败！");
        }
        for (Map mapData : resultList) {
            String taskId = "";
            if (mapData.get("task_id") != null) {
                taskId = mapData.get("task_id").toString();
            }
            if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType())) {
                if (mapData.get("msg_id") != null) {
                    taskId = mapData.get("msg_id").toString();
                }
            }
            saveSendResultInfo(pushArticle, taskId, PushChannel.YouMeng.name(),"1");
        }
        return null;
    }

    //华为推送
    private ResultMap huaweiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap) {
        log.info("########HuaWei华为用户推送########");
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig huaWei = pushArticleConfigService.findPushArticleConfig(PushChannel.HuaWei.name(), Constant.ANDROID, pushArticle.getAppPackage());
        if (huaWei == null) {
            log.error("华为推送参数未配置！");
            return ResultMap.error("华为推送参数未配置！");
        }
        pushParams.put(PushChannel.HuaWei.name(), huaWei);
        List<String> list = tokensMap.get(PushChannel.HuaWei.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("huaWeiTokens为空，不调用推送接口");
            return null;
        }
        List<SendResponce> sendResponceList = pushArticleMobileService.huaweiPushInfo(pushParams, param, list);
        if (CollectionUtils.isEmpty(sendResponceList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.HuaWei.name(), "0");
            log.error("华为推送失败！");
            return ResultMap.error("华为推送失败！");
        }
        setPushResultCountInfo(pushArticle.getId(), PushChannel.HuaWei.getCode(), list.size(), pushArticle.getAppPackage());
        for (SendResponce sendResponce : sendResponceList) {
            if (sendResponce != null) {
                saveSendResultInfo(pushArticle, sendResponce.getRequestId(), PushChannel.HuaWei.name(), "1");
            }
        }
        return null;
    }

    //vivo推送
    private ResultMap vivoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap) {
        log.info("########vivo用户推送########");
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType()) && StringUtils.isBlank(pushArticle.getDeviceTokens())) {
            return null;
        }
        PushArticleConfig vivo = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.ANDROID, pushArticle.getAppPackage());
        if (vivo == null) {
            log.error("vivo推送参数未配置！");
            return ResultMap.error("vivo推送参数未配置！");
        }
        String limitVal = redisService.get(RedisKeyConstant.VIVO_LIMIT_TOKEN + vivo.getAppId(),String.class);
        if(limitVal != null){
            log.error("vivo推送已被限制,不能推送！");
            return null;
        }

        pushParams.put(PushChannel.VIVO.name(), vivo);
        List<Result> resultList = new ArrayList<>();

        List<String> list = tokensMap.get(PushChannel.VIVO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("vivoTokens为空，不调用推送接口");
            return null;
        }
        setPushResultCountInfo(pushArticle.getId(), PushChannel.VIVO.getCode(), list.size(), pushArticle.getAppPackage());
        if (PushArticleConstant.USER_TYPE_ASSIGN_USER.equals(pushArticle.getUserType()) || list.size() < 2) {
            resultList = pushArticleMobileService.vivoPushByRegIds(pushParams, param, list);
        } else {
            resultList = pushArticleMobileService.vivoPushAll(pushParams, param, list);
        }
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.VIVO.name(),"0");
            log.error("vivo推送失败");
            return ResultMap.error("vivo推送失败");
        }
        for (Result result : resultList) {
            if (result != null && result.getResult() == 0) {
                saveSendResultInfo(pushArticle, result.getTaskId(), PushChannel.VIVO.name(),"1");
                List<InvalidUser> invalidUsers = result.getInvalidUsers();
                if (CollectionUtils.isNotEmpty(invalidUsers)) {
                    String dataStr = JSON.toJSONString(invalidUsers);
                    rabbitTemplate.convertAndSend(RabbitMQConstant.PUSH_VIVO_ERROR_EXCHANGE, RabbitMQConstant.PUSH_VIVO_ERROR_KEY, dataStr);
                }
            }
        }
        return null;
    }

    @Override
    public void vivoInvalidUserSave(List<InvalidUser> invalidUsers) {
        for (InvalidUser invalidUser : invalidUsers) {
            if (invalidUser.getStatus() == 1 || invalidUser.getStatus() == 2) {
                String key = "pushVivoToken:ballvideo";
                redisDB6Service.lpush(key, invalidUser.getUserid());
            }
        }
    }

    //组装推送结果总数信息
    private void setPushResultCountInfo(Long id, String pushChannel, Integer num, String appPackage) {
        PushResultCountVo countVo = new PushResultCountVo();
        countVo.setPushArticleId(id);
        countVo.setPushChannel(pushChannel);
        countVo.setSendNum(num);
        countVo.setAppPackage(appPackage);
        String dataStr = JSON.toJSONString(countVo);
        rabbitTemplate.convertAndSend(RabbitMQConstant.PUSH_COUNT_INFO_EXCHANGE, RabbitMQConstant.PUSH_COUNT_INFO_KEY, dataStr);
    }

    @Override
    public void savePushCountInfo(PushResultCountVo countVo) {
        PushResultCountVo queryVo = pushResultCountMapper.getPushResultCountInfo(countVo);
        if (queryVo == null) {
            pushResultCountMapper.savePushResultCountInfo(countVo);
        } else {
            pushResultCountMapper.updatePushResultCountInfo(countVo);
        }
    }

    //oppo推送
    private ResultMap oppoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap) {
        log.info("########oppo用户推送########");
        List<String> list = tokensMap.get(PushChannel.OPPO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.error("oppoTokens为空，不调用推送接口");
            return null;
        }
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig oppo = pushArticleConfigService.findPushArticleConfig(PushChannel.OPPO.name(), Constant.ANDROID, pushArticle.getAppPackage());
        if (oppo == null) {
            log.error("oppo推送参数未配置！");
            return ResultMap.error("oppo推送参数未配置！");
        }
        pushParams.put(PushChannel.OPPO.name(), oppo);
        List<com.oppo.push.server.Result> resultList = pushArticleMobileService.oppoPushByRegIds(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.OPPO.name(),"0");
            log.error("OPPO推送失败");
            return ResultMap.error("OPPO推送失败");
        }
        setPushResultCountInfo(pushArticle.getId(), PushChannel.OPPO.getCode(), list.size(), pushArticle.getAppPackage());
        for (com.oppo.push.server.Result result : resultList) {
            if (result != null) {
                String taskId = result.getTaskId() == null ? result.getMessageId() : result.getTaskId();
                saveSendResultInfo(pushArticle, taskId, PushChannel.OPPO.name(),"1");
            }
        }
        return null;
    }

    //小米推送
    private ResultMap xiaomiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap) {
        log.info("########小米用户推送########");
        List<String> list = tokensMap.get(PushChannel.XiaoMi.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("xiaomiTokens为空，不调用推送接口");
            return null;
        }
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig xiaomi = pushArticleConfigService.findPushArticleConfig(PushChannel.XiaoMi.name(), Constant.ANDROID, pushArticle.getAppPackage());
        if (xiaomi == null) {
            log.error("xiaomi推送参数未配置！");
            return ResultMap.error("xiaomi推送参数未配置！");
        }
        pushParams.put(PushChannel.XiaoMi.name(), xiaomi);
        List<com.xiaomi.xmpush.server.Result> resultList = pushArticleMobileService.xiaomiPushInfo(pushParams, param, list);
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendResultInfo(pushArticle, null, PushChannel.XiaoMi.name(),"0");
            log.error("小米推送失败！");
            return ResultMap.error("小米推送失败");
        }
        setPushResultCountInfo(pushArticle.getId(), PushChannel.XiaoMi.getCode(), list.size(), pushArticle.getAppPackage());
        for (com.xiaomi.xmpush.server.Result result : resultList) {
            if (result != null) {
                saveSendResultInfo(pushArticle, result.getMessageId(), PushChannel.XiaoMi.name(),"1");
            }
        }
        return null;
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
        List<AutoPushLog> autoPushLogs = Lists.newArrayList();
        try {
            AutoPushLog autoPushLog = new AutoPushLog();
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
                pushArticleSendResultService.saveSendResult(pushArticle.getId(), pushChannel, businessId, pushArticle.getAppPackage());
            } catch (Exception e) {
                log.error("保存推送参数:" + JSON.toJSONString(pushArticle));
                log.error("保存推送参数:" + "businessId=" + businessId + ",pushChannel=" + pushChannel);
                log.error("保存推送息异常:" + e.getMessage(), e);
            }
        }

    }
    private void saveAutoPushInfoToMongo(List<AutoPushLog> autoPushLogs) {
        mongoTemplate.insert(autoPushLogs,"auto_push_log");
    }

    public void executeInterestCat(String params){
        try {
            List<String> distinictIds = Lists.newArrayList();
            //params = "{\"pushId\":\"4725\",\"distinictIds\":[\"5472f98a5e701f87cf47a7c2b178b97b\",\"02e6e14adf564e524d0c18d4dd4bbac5\",\"69a82289d2e65447d3fdea258081b4aa\",\"54b459b4ed78518bf4dc969e97437142\"]}";
            JSONObject jsonObject = JSON.parseObject(params);
            String pushId = (String) jsonObject.get("pushId");
            JSONArray distinictIdsArr = (JSONArray) jsonObject.get("distinictIds");
            if(CollectionUtils.isNotEmpty(distinictIdsArr)){
                distinictIds = distinictIdsArr.stream().map(distinictId -> ""+distinictId).collect(Collectors.toList());
            }
            realTimeSendInfo(Long.parseLong(pushId),distinictIds);
            log.info("=====执行定向兴趣人群推送服务成功=====");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.error("执行定向兴趣人群推送服务异常:" + e.getMessage(), e);
        }
    }

    /**
     * 从device获取推送用户
     * @param pushArticle
     * @param distinctIds
     * @param userParam
     * @return
     */
    public List<ClDeviceVo> getClDeviceVosByUserType(PushArticle pushArticle, List<String> distinctIds, Map<String, Object> userParam) {
        List<ClDeviceVo> clDeviceVos = Lists.newArrayList();
        if(PushArticleConstant.USER_TYPE_INTEREST_CAT.equals(pushArticle.getUserType())){

            userParam.put("distinctIds", distinctIds);
            int count = clDeviceService.getAllTokensCountByDistinct(userParam);
            if(count > 0){
                clDeviceVos = clDeviceService.findAllTokensByDistinct(userParam);
            }
            log.info("########Android兴趣用户推送MQ-Consumer########，userParam：{},count:{}",JSON.toJSONString(userParam),count);
        }else{
            //全员推送
            int count = clDeviceService.getAllTokensCount(pushArticle.getAppPackage());
            if(count > 0){
                clDeviceVos = clDeviceService.findAllTokens(userParam);
            }
            log.info("########Android所有用户推送########，userParam：{},count:{}",JSON.toJSONString(userParam),count);
        }
        return clDeviceVos;
    }


    /**
     * 兴趣用户推送MQ
     * @param pushArticle
     * @param distinctIds
     * @return
     */
    private boolean consumeInterestCat(PushArticle pushArticle, List<String> distinctIds) {
        if(PushArticleConstant.USER_TYPE_INTEREST_CAT.equals(pushArticle.getUserType())){
            //如果distinctIds为空，代表还没推送MQ
            if(distinctIds == null){
                log.error("【MQ-Producer】Android全部/兴趣用户推送 consumeInterestCat into");
                if (StringUtils.isEmpty(pushArticle.getCatId())){
                    log.error("【MQ-Producer】Android兴趣用户推送,catIds为空，pushId={}", pushArticle.getId());
                    return true;
                }
                Map<String,String> pushMap = Maps.newHashMap();
                pushMap.put("pushId","" + pushArticle.getId());
                pushMap.put("catIds", pushArticle.getCatId());
                if(pushArticle.getType() == 3 || pushArticle.getType() == 4){
                    pushMap.put("videoId", pushArticle.getTypeValue());
                }
                pushMap.put("appPackage",pushArticle.getAppPackage());
                producerMqCallers.producerMqCallers(RabbitMQConstant.INTEREST_CAT,JSON.toJSONString(pushMap));
                log.error("【MQ-Producer】兴趣用户推送,{}",JSON.toJSONString(pushMap));
                return true;
            }
        }
        return false;
    }
}
