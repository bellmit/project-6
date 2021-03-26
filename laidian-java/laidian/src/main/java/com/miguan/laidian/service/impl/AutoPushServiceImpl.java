package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.miguan.laidian.common.constants.AutoPushConstant;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.enums.PushChannel;
import com.miguan.laidian.common.exception.ServiceException;
import com.miguan.laidian.common.util.*;
import com.miguan.laidian.common.util.push.PushUtil;
import com.miguan.laidian.entity.*;
import com.miguan.laidian.mapper.ActActivityMapper;
import com.miguan.laidian.mapper.PushContentMapper;
import com.miguan.laidian.mapper.SmallVideoMapper;
import com.miguan.laidian.mapper.VideoMapper;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.*;
import com.miguan.laidian.vo.AutoPushInfo;
import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.SmallVideoVo;
import com.miguan.laidian.vo.mongodb.XldAutoPushLog;
import com.miguan.message.push.utils.huawei.messaging.SendResponce;
import com.mongodb.client.result.DeleteResult;
import com.vivo.push.sdk.notofication.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动推送
 *
 * @author xy.chen
 * @date 2019-08-09
 **/
@Slf4j
@Service("autoPushService")
public class AutoPushServiceImpl implements AutoPushService {

    @Resource(name="ldpushMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Resource
    private PushArticleConfigService pushArticleConfigService;

    @Resource
    private PushArticleMobileService pushArticleMobileService;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoMapper videoMapper;

    @Resource
    private SmallVideoMapper smallVideosMapper;


    @Resource
    private ActActivityMapper actActivityMapper;

    @Resource
    private PushContentMapper pushContentMapper;

    @Resource
    private ClDeviceService clDeviceService;


    @Value("${push.auto-push.url}")
    private String autoPushUrl;

    @Override
    public void batchPush(List<AutoPushInfo> autoPushInfos, AutoPushConfig autoPushConfig){
        log.info("【自动推送】===batchPush.autoPushConfig : {}",JSON.toJSONString(autoPushConfig));
        for (AutoPushInfo autoPushInfo:autoPushInfos) {
            try {
                PushArticle pushArticle = fillArtice(autoPushInfo, autoPushConfig);
                autoPushInfo(pushArticle, autoPushInfo, autoPushConfig.getEventType());
            } catch (ServiceException e){
                log.error("【自动推送】推送业务失败："+e.getMessage());
            } catch (Exception e){
                log.error("【自动推送】推送失败："+e.getMessage());
            }
        }
    }

    private List<ActActivity> findActActivity(Map<String, Object> params){
        return actActivityMapper.findActActivity(params);
    }

    //随机文案处理
    @Override
    public Map<String, List<String>> autoPushContentInfo(List<String> distinctIds, Integer titleType) {
        Map<String, List<String>> contentMap = new HashMap<>();
        List<PushContent>  pushContentList = pushContentMapper.findPushContentList(titleType);
        if(CollectionUtils.isEmpty(pushContentList)){
            return contentMap;
        }
        //循环次数限制20次
        int num = 1;
        setContent(contentMap, distinctIds, pushContentList, num);
        return contentMap;
    }

    /**
     * 查询现有设备
     */
    public List<ClDeviceVo> findAllTokensByDistinct(String appPackage, List<String> distinctIds) {
        if(CollectionUtils.isEmpty(distinctIds)){
            return null;
        }
        Map param = new HashMap<>();
        param.put("distinctIds",distinctIds);
        param.put("appPackage",appPackage);
        List<ClDeviceVo> clDeviceVos = clDeviceService.findAllTokensByDistinct(param);
        return clDeviceVos;
    }


    //递归配置随机文案
    private void setContent(Map<String, List<String>> contentMap, List<String> distinctIds, List<PushContent>  pushContentList, int num) {
        if(CollectionUtils.isEmpty(pushContentList)){
            return ;
        }
        String acticleContent = getRandomContent(pushContentList);
        //该文案未推送过的用户列表
        List<String> abledistinctIds = Lists.newArrayList();
        //该文案已推送过的用户列表
        List<String> unabledistinctIds = Lists.newArrayList();
        for (String distinctId : distinctIds) {
            //从mongodb获取distinctId 7日所有的已推送文案
            Query query = new Query(Criteria.where("distinct_id").is(distinctId));
            List<XldAutoPushLog> pushLogList = mongoTemplate.find(query, XldAutoPushLog.class, Constant.XLD_AUTO_PUSH_TITLE);
            if (CollectionUtils.isNotEmpty(pushLogList)) {
                //该文案是否已推送过
                boolean exitFlag = false;
                for (XldAutoPushLog pushLog : pushLogList) {
                    if (acticleContent.equals(pushLog.getTitle())){
                        exitFlag = true;
                        break;
                    }
                }
                if (exitFlag) {
                    unabledistinctIds.add(distinctId);
                } else {
                    abledistinctIds.add(distinctId);
                }
            } else {
                abledistinctIds.add(distinctId);
            }
        }
        if (CollectionUtils.isNotEmpty(abledistinctIds)) {
            contentMap.put(acticleContent, abledistinctIds);
        }
        if (CollectionUtils.isNotEmpty(unabledistinctIds) && num < 21) {
            //递归传入新文案列表和未分配文案的用户
            num +=1;
            List<PushContent>  newPushContentList = pushContentList.stream().filter(p -> !p.getContent().equals(acticleContent)).collect(Collectors.toList());
            setContent(contentMap, unabledistinctIds, newPushContentList, num);
        }
    }

    private String getRandomContent(List<PushContent> pushContentList) {
        Random random = new Random();
        int a = random.nextInt(pushContentList.size());
        String acticleContent = pushContentList.get(a).getContent();
        return acticleContent;
    }

    /**
     * 判断用户是否完成活动任务
     * @return
     */
    @Override
    public boolean isFinishActivity(String deviceId, Integer eventType,Integer activityType){
        boolean finishFlag = false;
        Map<String, Object> params = new HashMap<>();
        params.put("curDate", new Date());
        List<ActActivity> actActivityList = findActActivity(params);
        log.info("【自动推送】===isFinishActivity.actActivityList:{}", JSON.toJSONString(actActivityList));
        if(CollectionUtils.isEmpty(actActivityList)){
            finishFlag = true;
        }
        //活跃用户:查询未完成任务的用户
        if(activityType == 2 && eventType == 6004){
            for (int i = 0; i < actActivityList.size(); i++) {
                ActActivity activity = actActivityList.get(i);

                //如果都禁用，代表没有任务需要完成
                if(activity.getLdxTaskFlag() == 0 && activity.getLsTaskFlag() == 0
                        && activity.getVideoTaskFlag() == 0  && activity.getShareTaskFlag() == 0 ){
                    log.info("【自动推送】===activity.AllFlag0==");
                    finishFlag = true;
                    continue;
                }

                if(activity.getLdxTaskFlag() == 1
                    && !redisService.exits(RedisKeyConstant.ACTIVITY_TASK_SETTING_LDX + activity.getId() + ":" + deviceId)){
                    log.info("【自动推送】===activity.getLdxTaskFlag()==");
                    continue;
                }
                if(activity.getLsTaskFlag() == 1
                        && !redisService.exits(RedisKeyConstant.ACTIVITY_TASK_SETTING_LS + activity.getId() + ":" + deviceId)){
                    log.info("【自动推送】===activity.getLsTaskFlag()==");
                    continue;
                }
                if(activity.getShareTaskFlag() == 1
                        && !redisService.exits(RedisKeyConstant.ACTIVITY_TASK_SHARE + activity.getId() + ":" + deviceId)){
                    log.info("【自动推送】===activity.getShareTaskFlag()==");
                    continue;
                }

                if(activity.getVideoTaskFlag() == 1){
                    String videoCntStr = redisService.get(RedisKeyConstant.ACTIVITY_TASK_VIDEO + activity.getId() + ":" + deviceId);
                    Integer videoCnt = 0;
                    if(StringUtils.isNotEmpty(videoCntStr)){
                        videoCnt = Integer.parseInt(videoCntStr);
                    }
                    //50次以上就代表任务完成
                    if(videoCnt < 50){
                        log.info("【自动推送】===activity.getVideoTaskFlag()==");
                        continue;
                    }
                }
                finishFlag = true;
            }
        }
        log.info("【自动推送】===isFinishActivity.finishFlag:{}",finishFlag);
        return finishFlag;
    }

    /**
     * 是否推送过签到数据
     * @param deviceId
     * @return
     */
    public boolean isPushSignIn(String deviceId){
        boolean flag = false;
        String key = RedisKeyConstant.AUTO_PUSH_SIGN + deviceId;
        String pushFlag = redisService.get(key);
        log.info("【自动推送】======isPushSignIn:{}",pushFlag);
        if (StringUtils.isNotEmpty(pushFlag)) {
            flag = true;
        }
        return flag;
    }


    /**
     * 填充推送素材
     * @param autoPushInfo
     * @param config
     * @return
     */
    private PushArticle fillArtice(AutoPushInfo autoPushInfo, AutoPushConfig config) throws ServiceException {
        PushArticle pushArticle = new PushArticle();
        //用于埋点
        pushArticle.setId(config.getId());

        //标题类型：1.文案库-活跃用户推送文案 2.文案库-不活跃用户推送文案 3.文案库-签到推送文案 4.自定义标签
        pushArticle.setTitle(config.getTitle());

        Video videos = getVideo(autoPushInfo);
        if(AutoPushConstant.ARTICLE_TYPE_DEFAULT.equals(config.getContentType())){
            pushArticle.setNoteContent(videos.getTitle());
            pushArticle.setThumbnailUrl(videos.getBsyImgUrl());
        } else {
            pushArticle.setNoteContent(config.getContent());
        }


        pushArticle.setAppType(autoPushInfo.getAppPackage());
        pushArticle.setExpireTime("4");
        pushArticle.setType(config.getJumpKey());
        pushArticle.setAppPackage(autoPushInfo.getAppPackage());
        //内容：1 签到：2 活动：3 用户行为：4
        pushArticle.setActType(LaidianUtils.getStrategyByEvent(config.getEventType()));
        //链接时，才会有跳转链接
        if(config.getJumpKey() == 2 ){
            pushArticle.setTypeValue(config.getJumpValue());
        }else {
            pushArticle.setTypeValue(autoPushInfo.getVideoId() + "");
        }

        String time = config.getTriggerTime();
        if (StringUtils.isEmpty(time)) {
            time = DateUtil.parseDateToStr(new Date(), "HH:mm:ss");
        }
        String today = DateUtil.parseDateToStr(new Date(), "yyyy-MM-dd") + " " + time;
        pushArticle.setPushTime(today);
        return pushArticle;
    }

    private Video getVideo(AutoPushInfo autoPushInfo) {
        Video videos = new Video();
        if(autoPushInfo.getVideoId() != null){
            videos = videoMapper.findVideoById(autoPushInfo.getVideoId());
            //目前無小視頻
/*            if(videos == null){
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", autoPushInfo.getVideoId());
                params.put("state", Constant.open);
                SmallVideoVo smallVideoVo = smallVideosMapper.findVideosDetailByOne(params);
                if(smallVideoVo != null){
                    videos.setTitle(smallVideoVo.getTitle());
                    videos.setBsyAudioUrl(smallVideoVo.getBsyUrl());
                }
            }*/
        }
        return videos;
    }

    public void autoPushInfo(PushArticle pushArticle, AutoPushInfo autoPushInfo,Integer evenType){
        Map<String, String> param = getParaMap(pushArticle);
        //推送有效期，如果实体类有没有配置有效期，取配置表的有效期
        Map<String, Object> pushParams = PushUtil.getExpireTime(pushArticle);
        pushParams.put("pushArticle", pushArticle);
        pushParams.put("eventType", evenType);
        log.info("【自动推送】AutoPushServiceImpl.autoPushInfo param:{},pushParams:{}",JSON.toJSONString(param),JSON.toJSONString(pushParams));
        puthAllPlatform(param, pushParams,autoPushInfo);
    }


    private void puthAllPlatform(Map<String, String> param, Map<String, Object> pushParams, AutoPushInfo autoPushInfo) {
        //华为广播推送
        this.huaweiPush(pushParams, param, autoPushInfo.getTokens(),autoPushInfo.getConnTokenDistinct(),autoPushInfo.getDeviceIdMap());
        //vivo广播推送
        this.vivoPushAll(pushParams, param, autoPushInfo.getTokens(),autoPushInfo.getConnTokenDistinct(),autoPushInfo.getDeviceIdMap());
        //oppo广播推送
        this.oppoPush(pushParams, param, autoPushInfo.getTokens(),autoPushInfo.getConnTokenDistinct(),autoPushInfo.getDeviceIdMap());
        //小米广播推送
        this.xiaomiPush(pushParams, param, autoPushInfo.getTokens(),autoPushInfo.getConnTokenDistinct(),autoPushInfo.getDeviceIdMap());
    }

    //华为推送
    private ResultMap huaweiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap, Map<String, List<String>> deviceIdMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig huaWei = pushArticleConfigService.findPushArticleConfig(PushChannel.HuaWei.name(), Constant.Android, pushArticle.getAppType());
        if (huaWei == null) return ResultMap.error("推送参数未配置！");
        pushParams.put(PushChannel.HuaWei.name(), huaWei);
        List<String> list = tokensMap.get(PushChannel.HuaWei.name());
        log.info("【自动推送】华为推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(huaWei));
        if (CollectionUtils.isEmpty(list)) {
            log.error("huaWeiTokens为空，不调用推送接口");
            return ResultMap.error("huaWeiTokens为空，不调用推送接口");
        }
        List<SendResponce> sendResponceList = pushArticleMobileService.huaweiPushInfo(pushParams, param, list);
        log.info("【自动推送】华为推送 结果 ===huapush===: {}",JSON.toJSONString(sendResponceList));
        if (CollectionUtils.isEmpty(sendResponceList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, PushChannel.HuaWei.name(),"0");
            }
            return ResultMap.error("【自动推送】推送失败！");
        }
        Integer eventType = (Integer) pushParams.get("eventType");
        saveSendNumToRedis(deviceIdMap.get(PushChannel.HuaWei.name()),eventType);
        if(CollectionUtils.isNotEmpty(list)){
            saveSendInfo(dstinctsMap, pushArticle, PushChannel.HuaWei.name(),"1");
        }
        return ResultMap.success();
    }

    //vivo广播推送
    private ResultMap vivoPushAll(Map<String, Object> pushParams, Map<String, String> param,
                                  Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap, Map<String, List<String>> deviceIdMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig vivo = pushArticleConfigService.findPushArticleConfig(PushChannel.VIVO.name(), Constant.Android, pushArticle.getAppType());
        log.info("【自动推送】vivo推送 配置 ===PushArticleConfig====: {}",JSON.toJSONString(vivo));
        pushParams.put(PushChannel.VIVO.name(), vivo);
        List<String> list = tokensMap.get(PushChannel.VIVO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("【自动推送】vivoTokens为空，不调用推送接口");
            return ResultMap.error("vivoTokens为空，不调用推送接口");
        }
        List<Result> resultList = pushArticleMobileService.vivoPushAll(pushParams, param, list);
        log.info("【自动推送】vivo推送 结果 ===vivoPush===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, PushChannel.VIVO.name(),"0");
            }
            return ResultMap.error("vivo广播推送失败");
        }
        Integer eventType = (Integer) pushParams.get("eventType");
        saveSendNumToRedis(deviceIdMap.get(PushChannel.VIVO.name()),eventType);
        if(CollectionUtils.isNotEmpty(list)){
            saveSendInfo(dstinctsMap, pushArticle, PushChannel.VIVO.name(),"1");
        }
        return ResultMap.success();
    }

    //oppo推送
    private ResultMap oppoPush(Map<String, Object> pushParams, Map<String, String> param,
                               Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap, Map<String, List<String>> deviceIdMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig oppo = pushArticleConfigService.findPushArticleConfig(PushChannel.OPPO.name(), Constant.Android, pushArticle.getAppType());
        log.info("【自动推送】oppo推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(oppo));
        pushParams.put(PushChannel.OPPO.name(), oppo);
        List<String> list = tokensMap.get(PushChannel.OPPO.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("【自动推送】oppoTokens为空，不调用推送接口");
            return ResultMap.error("oppoTokens为空，不调用推送接口");
        }
        List<com.oppo.push.server.Result> resultList = pushArticleMobileService.oppoPushByRegIds(pushParams, param, list);
        log.info("【自动推送】oppo推送 结果 ===oppopush===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            if(CollectionUtils.isNotEmpty(list)){
                saveSendInfo(dstinctsMap, pushArticle, PushChannel.OPPO.name(),"0");
            }
            return ResultMap.error("OPPO推送失败");
        }
        Integer eventType = (Integer) pushParams.get("eventType");
        saveSendNumToRedis(deviceIdMap.get(PushChannel.OPPO.name()),eventType);
        if(CollectionUtils.isNotEmpty(list)){
            saveSendInfo(dstinctsMap, pushArticle, PushChannel.OPPO.name(),"1");
        }
        return ResultMap.success();
    }

    //小米推送
    private ResultMap xiaomiPush(Map<String, Object> pushParams, Map<String, String> param,
                                 Map<String, List<String>> tokensMap, Map<String, List<String>> dstinctsMap, Map<String, List<String>> deviceIdMap) {
        PushArticle pushArticle = (PushArticle) pushParams.get("pushArticle");
        PushArticleConfig xiaomi = pushArticleConfigService.findPushArticleConfig(PushChannel.XiaoMi.name(), Constant.Android, pushArticle.getAppType());
        log.info("【自动推送】小米推送 配置 ===PushArticleConfig===: {}",JSON.toJSONString(xiaomi));
        pushParams.put(PushChannel.XiaoMi.name(), xiaomi);
        List<String> list = tokensMap.get(PushChannel.XiaoMi.name());
        if (CollectionUtils.isEmpty(list)) {
            log.info("【自动推送】xiaomiTokens为空，不调用推送接口");
            return ResultMap.error("xiaomiTokens为空，不调用推送接口");
        }
        List<com.xiaomi.xmpush.server.Result> resultList = pushArticleMobileService.xiaomiPushInfo(pushParams, param, list);
        log.info("【自动推送】小米推送 结果 ===小米push===: {}",JSON.toJSONString(resultList));
        if (CollectionUtils.isEmpty(resultList)) {
            saveSendInfo(dstinctsMap, pushArticle, PushChannel.XiaoMi.name(),"0");
            return ResultMap.error("小米推送失败");
        }
        Integer eventType = (Integer) pushParams.get("eventType");
        saveSendNumToRedis(deviceIdMap.get(PushChannel.XiaoMi.name()),eventType);
        if(CollectionUtils.isNotEmpty(list)){
            saveSendInfo(dstinctsMap, pushArticle, PushChannel.XiaoMi.name(),"1");
        }
        return ResultMap.success();
    }

    private void saveSendInfo(Map<String, List<String>> dstinctsMap, PushArticle pushArticle, String key, String isSucc) {

        List<XldAutoPushLog> autoPushLogs = listLog(pushArticle.getTitle(), pushArticle.getAppType(), pushArticle.getTypeValue(), dstinctsMap.get(key));

        //成功，才推送大数据
        if(Integer.parseInt(isSucc) == 1){
            sycToBdrep(autoPushLogs);
        }
        //日志存储参数
        autoPushLogs.stream().forEach(log -> {
            log.setPush_id(pushArticle.getId());
            log.setContent(JSON.toJSONString(pushArticle));
            log.setType("1");
            log.setPush_channel(key);
            log.setIs_succ(isSucc);
        });

        saveAutoPushInfoToMongo(autoPushLogs);
        //更新发送数
//        updateSendNum(pushArticle, count);
    }

    /**
     * 保存推送日志到大数据
     * @param autoPushLogs
     */
    private void sycToBdrep(List<XldAutoPushLog> autoPushLogs) {
        if(CollectionUtils.isEmpty(autoPushLogs)){
            return ;
        }
        Map<String,String> param = new HashMap<>();
        param.put("arrayList",parseAutoPushLogs(autoPushLogs));
        HttpUtil.send(autoPushUrl + "/api/push/video/syncAutoLdPushLog",param,true,"UTF-8");
        log.info("【自动推送】保存推送日志到大数据，syncAutoLdPushLog：{}",JSON.toJSONString(param));
    }

    private String parseAutoPushLogs(List<XldAutoPushLog> autoPushLogs) {
        StringBuilder sb = new StringBuilder();
        int flag = 0 ;
        for (XldAutoPushLog autoPushLog:autoPushLogs) {
            if(flag == 0){
                flag ++ ;
                sb.append(autoPushLog.getDistinct_id() + ":" +autoPushLog.getVideo_id() + ":" + autoPushLog.getApp_package());
            } else {
                sb.append(";" + autoPushLog.getDistinct_id() + ":" +autoPushLog.getVideo_id() + ":" + autoPushLog.getApp_package());
            }
        }
        return sb.toString();
    }

//    private void updateSendNum(PushArticle pushArticle, int size) {
//        try{
//            int videoId = Integer.valueOf(pushArticle.getTypeValue());
//            Map<String,Object> param = new HashMap<>();
//            param.put("videoId",videoId);
//            param.put("playEndNum",size);
//            videoMapper.updateSenNumByVideoId(param);
//        } catch (Exception e ){
//            log.error("更新推送量失败！updateSendNum");
//        }
//    }

    private List<XldAutoPushLog> listLog(String title, String appPackage, String videoId, List<String> distinctIds){
        List<XldAutoPushLog> xldAutoPushLogs = new ArrayList<>();
        for (String distinctId:distinctIds) {
            XldAutoPushLog xldAutoPushLog = new XldAutoPushLog();
            xldAutoPushLog.setApp_package(appPackage);
            xldAutoPushLog.setDistinct_id(distinctId);
            xldAutoPushLog.setVideo_id(videoId);
            xldAutoPushLog.setTitle(title);
            xldAutoPushLogs.add(xldAutoPushLog);
        }
        return xldAutoPushLogs;
    }

    private void saveAutoPushInfoToMongo(List<XldAutoPushLog> xldAutoPushLogs) {
        try {
            mongoTemplate.insert(xldAutoPushLogs,Constant.XLD_AUTO_PUSH_LOG);
            mongoTemplate.insert(xldAutoPushLogs,Constant.XLD_AUTO_PUSH_TITLE);
        } catch (Exception e) {
            log.error("自动推送保存推送日志mongodb错误：{}", e.getMessage(), e);
        }
    }

    /**
     * 删除7天前的自动推送文案日志
     */
    //@Scheduled(cron = "0 0 0 * * ?")
    private void deleteAutoPushLog() {
        RedisLock redisLock = new RedisLock(RedisKeyConstant.DELETE_AUTO_PUSH_LOG, RedisKeyConstant.defalut_seconds);
        if (redisLock.lock()) {
            log.info("【自动推送】---删除7天前的自动推送日志开始---");
            Date sevenDay = DateUtil.getDateBefore(-7, new Date());
            Query query = new Query(Criteria.where("create_at").lt(sevenDay));
            DeleteResult result = mongoTemplate.remove(query,Constant.XLD_AUTO_PUSH_TITLE);
            log.info("【自动推送】删除日志总数：{}", result.getDeletedCount());
            log.info("【自动推送】---删除7天前的自动推送日志结束---");
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
        param.put("act_type","" + pushArticle.getActType());
        param.put("intent_url", "huaweipush://com.mg.phonecall.push/notify_detail");
        param.put("thumbnail_url", pushArticle.getThumbnailUrl() == null ? "" : pushArticle.getThumbnailUrl());
        param.put("push_method", "1");
        return param;
    }

    /**
     * 设备今日推送总数：保存到redis
     * 签到提醒推送：保存已推送标识到redis
     * @param sendList
     */
    private void saveSendNumToRedis(List<String> sendList, Integer eventType) {
        Integer strategyType = LaidianUtils.getStrategyByEvent(eventType);
        for (String DeviceId : sendList) {
            String keyTotalNum = RedisKeyConstant.AUTO_PUSH_TOTAL_NUM + DeviceId;
            String totalNum = redisService.get(keyTotalNum);
            if (StringUtils.isEmpty(totalNum)) {
                redisService.set(keyTotalNum, 1, DateUtil.caluRedisExpiredTime());
            } else {
                redisService.set(keyTotalNum, Integer.valueOf(totalNum) + 1, DateUtil.caluRedisExpiredTime());
            }
            if (strategyType == 2) {
                String key = RedisKeyConstant.AUTO_PUSH_SIGN + DeviceId;
                redisService.set(key, 1, DateUtil.caluRedisExpiredTime());
            }
        }
    }


    public void pushTokenInfo(String appPackage, List<AutoPushInfo> autoPushInfos, Long videoId, List<String> distinctIds, List<ClDeviceVo> clDeviceVos) {
        AutoPushInfo pushInfo = new AutoPushInfo();
        if(videoId != -1){
            pushInfo.setVideoId(videoId);
        }
        pushInfo.setAppPackage(appPackage);
        pushInfo.setDistinctIds(distinctIds);
        Map<String, List<String>> result = new HashMap<>();
        Map<String, List<String>> connTokenDistinct = new HashMap<>();
        List<String> huaweiTokens = new ArrayList<>();
        List<String> oppoTokens = new ArrayList<>();
        List<String> vivoTokens = new ArrayList<>();
        List<String> xiaomiTokens = new ArrayList<>();

        List<String> huaweiDistincts = new ArrayList<>();
        List<String> oppoDistincts = new ArrayList<>();
        List<String> vivoDistincts = new ArrayList<>();
        List<String> xiaomiDistincts = new ArrayList<>();

        Map<String, List<String>> connDeviceMap = new HashMap<>();
        List<String> huaweiDeviceIds = new ArrayList<>();
        List<String> oppoDeviceIds = new ArrayList<>();
        List<String> vivoDeviceIds = new ArrayList<>();
        List<String> xiaomiDeviceIds = new ArrayList<>();

        // 排重处理
        Set<ClDeviceVo> clDeviceVoSet = new HashSet<>(clDeviceVos);
        clDeviceVos.clear();
        clDeviceVos.addAll(clDeviceVoSet);
        // 根据
        for (ClDeviceVo vo : clDeviceVos) {
            if (StringUtils.isNotEmpty(vo.getHuaweiToken())) {
                huaweiTokens.add(vo.getHuaweiToken());
                huaweiDistincts.add(vo.getDistinctId());
                huaweiDeviceIds.add(vo.getDeviceId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getOppoToken())) {
                oppoTokens.add(vo.getOppoToken());
                oppoDistincts.add(vo.getDistinctId());
                oppoDeviceIds.add(vo.getDeviceId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getVivoToken())) {
                vivoTokens.add(vo.getVivoToken());
                vivoDistincts.add(vo.getDistinctId());
                vivoDeviceIds.add(vo.getDeviceId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getXiaomiToken())) {
                xiaomiTokens.add(vo.getXiaomiToken());
                xiaomiDistincts.add(vo.getDistinctId());
                xiaomiDeviceIds.add(vo.getDeviceId());
                continue;
            }
        }
        result.put(PushChannel.HuaWei.name(), huaweiTokens);
        result.put(PushChannel.OPPO.name(), oppoTokens);
        result.put(PushChannel.VIVO.name(), vivoTokens);
        result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        connTokenDistinct.put(PushChannel.HuaWei.name(), huaweiDistincts);
        connTokenDistinct.put(PushChannel.OPPO.name(), oppoDistincts);
        connTokenDistinct.put(PushChannel.VIVO.name(), vivoDistincts);
        connTokenDistinct.put(PushChannel.XiaoMi.name(), xiaomiDistincts);

        connDeviceMap.put(PushChannel.HuaWei.name(), huaweiDeviceIds);
        connDeviceMap.put(PushChannel.OPPO.name(), oppoDeviceIds);
        connDeviceMap.put(PushChannel.VIVO.name(), vivoDeviceIds);
        connDeviceMap.put(PushChannel.XiaoMi.name(), xiaomiDeviceIds);

        pushInfo.setTokens(result);
        pushInfo.setConnTokenDistinct(connTokenDistinct);
        pushInfo.setDeviceIdMap(connDeviceMap);

        autoPushInfos.add(pushInfo);
    }


    /**
     * 过滤今日推送总数已达6次用户
     */
    public List<String> filterPushDevice(List<ClDeviceVo> clDeviceVos) {
        List<String> unDistinctIds = Lists.newArrayList();
        //过滤今日推送总数已达6次用户
        Iterator<ClDeviceVo> it = clDeviceVos.iterator();
        while (it.hasNext()) {
            ClDeviceVo clDeviceVo = it.next();
            String keyTotalNum = RedisKeyConstant.AUTO_PUSH_TOTAL_NUM + clDeviceVo.getDeviceId();
            String totalNum = redisService.get(keyTotalNum);
            log.info("【自动推送】=== deviceId={} 今日推送总数为{}次",clDeviceVo.getDeviceId(),totalNum);
            //第6次要下发了。所以只能校验下发到5的
            if (totalNum == null || Integer.valueOf(totalNum) < 6) {
                unDistinctIds.add(clDeviceVo.getDistinctId());
            }else{
                it.remove();
            }

        }
        return unDistinctIds;
    }

    @Override
    public void saveAutoPushRedis(String configId) {
        //设置5分钟过期
        redisService.set(RedisKeyConstant.AUTO_PUSH_SCHEDULE_ONE + ":" + configId, "1", 300);
    }

    @Override
    public boolean hasAutoPushRedis(String configId) {
        boolean flag = false;
        if(StringUtil.isNotEmpty(redisService.get(RedisKeyConstant.AUTO_PUSH_SCHEDULE_ONE + ":" + configId))){
            flag = true;
        }
        return flag;
    }
}