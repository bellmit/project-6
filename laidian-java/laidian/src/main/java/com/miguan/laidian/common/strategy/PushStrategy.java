package com.miguan.laidian.common.strategy;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.laidian.common.util.HttpUtil;
import com.miguan.laidian.common.util.LaidianUtils;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.AutoPushConfig;
import com.miguan.laidian.service.AutoPushService;
import com.miguan.laidian.service.ClDeviceService;
import com.miguan.laidian.vo.AutoPushInfo;
import com.miguan.laidian.vo.ClDeviceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.LinkedMultiValueMap;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class PushStrategy {
    protected AutoPushConfig autoPushConfig;
    protected ClDeviceService clDeviceService;
    protected MongoTemplate mongoTemplate;
    protected AutoPushService autoPushService;
    protected String autoPushUrl;

    public abstract void doexecute();

    /**
     * 封装推送信息   video ==> list<distinctId>
     */
    protected Map<Long, Set<String>> fillInfoMap(List<Map<String,Object>> infoData, Map<Long,Set<String>> videoInfos) {
        for (int i = 0 ; i < infoData.size() ; i ++ ) {
            Map<String,Object> info = infoData.get(i);
            if(info == null){
                continue;
            }


            String distinctId = MapUtils.getString(info,"distinctId");
            if(StringUtils.isEmpty(MapUtils.getString(info,"videoId"))){
                if(StringUtils.isEmpty(distinctId)){
                    continue;
                }
                //存在只有distinctId的情況，設爲特殊值-1
                info.put("videoId", "-1");
            }
            String videoId = MapUtils.getString(info,"videoId");

            Set<String> disLst = videoInfos.get(Long.valueOf(videoId));
            if(videoInfos.get(Long.valueOf(videoId)) == null){
                Set<String> distinctList = new HashSet<>();
                distinctList.add(distinctId);
                videoInfos.put(Long.valueOf(videoId),distinctList);
            } else {
                Set<String> distinctList = disLst;
                distinctList.add(distinctId);
            }
        }
        return videoInfos;
    }



    /**
     * 访问发数据，获得推送用户的信息。
     */
    protected List<Map<String,Object>> postDbInfo(AutoPushConfig autoPushConfig, SimpleDateFormat sdf, String appPackage, Map<Long, Set<String>> videoInfos, int pageNum, int pageSize, Calendar calendar) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
        String type = String.valueOf(autoPushConfig.getActivityType()) + String.valueOf(autoPushConfig.getEventType());
        body.add("pageNum",pageNum + "");
        body.add("pageSize",pageSize + "");
        body.add("packageName",appPackage);
        body.add("type", type);
        body.add("dd",sdf.format(calendar.getTime()));
        log.info("【自动推送】访问大数据findAutoPushList begin====,url={},pageNum={},pageSize={},packageName={},type={},dd={}",
                autoPushUrl,pageNum,pageSize,appPackage,type,sdf.format(calendar.getTime()));
        ResultMap result = HttpUtil.httpSend(autoPushUrl + "/api/push/ld/findLdAutoPushList",body);
        log.info("【自动推送】访问大数据findAutoPushList end====,jsonResult:{}",JSON.toJSONString(result));
        if(result.getCode() == 200){
            List<Map<String,Object>> infoDataList = (List<Map<String,Object>>) result.getData();
            if(infoDataList != null && infoDataList.size() > 0){
                return infoDataList;
            }
        }
        return null;
    }

    //==================================推送封装 start==================================
    public void singleOrManyBatchPush(AutoPushConfig autoPushConfig, String appPackage, Map<Long, Set<String>> videoInfos) {
        Map<String, List<AutoPushInfo>> contentMap = Maps.newHashMap();

        if(autoPushConfig.getTitleType() == 4) {
            //自定义标签时，不走随机文案获取
            contentMap = fillAutoPushInfo(autoPushConfig.getTitle(),appPackage, videoInfos);
        }else{
            contentMap = fillAutoPushInfoBatch(appPackage, videoInfos, autoPushConfig.getTitleType(), autoPushConfig.getEventType());
        }

        for (Map.Entry<String, List<AutoPushInfo>> mapResult : contentMap.entrySet()) {
            String content = mapResult.getKey();
            autoPushConfig.setTitle(content);
            List<AutoPushInfo> pushInfoList = mapResult.getValue();
            autoPushService.batchPush(pushInfoList, autoPushConfig);
            log.info("【自动推送】推送量autoPushInfos.size："+pushInfoList.size());
        }
    }

    /**
     * 根据单独填充推送数据
     */
    private Map<String, List<AutoPushInfo>> fillAutoPushInfo(String title, String appPackage, Map<Long, Set<String>> videoInfos) {
        Map<String, List<AutoPushInfo>> contentMap = new HashMap<>();

        List<AutoPushInfo> autoPushInfos = new ArrayList<>();
        Set<Long> videoInfoSet = videoInfos.keySet();
        for (Long videoId:videoInfoSet) {
            Map param = new HashMap<>();
            List<String> distinctIds =  new ArrayList<>(videoInfos.get(videoId));
            if(CollectionUtils.isEmpty(distinctIds)){
                continue;
            }
            param.put("distinctIds",distinctIds);
            param.put("appPackage",appPackage);
            List<ClDeviceVo> clDeviceVos = clDeviceService.findAllTokensByDistinct(param);
            if(CollectionUtils.isEmpty(clDeviceVos)){
                continue;
            }
            log.info("【自动推送】filterSpecialCond in,{},{}",JSON.toJSONString(distinctIds),JSON.toJSONString(clDeviceVos));
            //特殊事件类型过滤
            Map<String,Object> fltMap = filterSpecialCond(distinctIds,clDeviceVos);
            if (MapUtils.isEmpty(fltMap)) {
                continue;
            }


            List<String> filterDistinctIds = (List<String>)fltMap.get("filterDistinctIds");
            if(CollectionUtils.isEmpty(filterDistinctIds)){
                continue;
            }
            List<ClDeviceVo> filterDeviceVoList = (List<ClDeviceVo>)fltMap.get("filterDeviceVoList");

            log.info("【自动推送】filterSpecialCond out,{},{}",JSON.toJSONString(filterDistinctIds),JSON.toJSONString(filterDeviceVoList));
            autoPushService.pushTokenInfo(appPackage, autoPushInfos, videoId, filterDistinctIds, filterDeviceVoList);
        }

        if(CollectionUtils.isNotEmpty(autoPushInfos)){
            contentMap.put(title,autoPushInfos);
        }
        log.info("【自动推送】填充推送数据===fillAutoPushInfo.contentMap：{}",JSON.toJSONString(contentMap));
        return contentMap;
    }


    /**
     * 根据随机文案，批量填充推送数据
     */
    private Map<String, List<AutoPushInfo>> fillAutoPushInfoBatch(String appPackage,Map<Long,Set<String>> videoInfos, Integer  titleType, Integer evenType) {
        Map<String, List<AutoPushInfo>> contentMap = new HashMap<>();

        for (Long videoId : videoInfos.keySet()){
            List<String> distinctIds = new ArrayList<>(videoInfos.get(videoId));
            //查詢相应设备
            List<ClDeviceVo> clDeviceVoList = autoPushService.findAllTokensByDistinct(appPackage,distinctIds);
            //过滤今日推送总数已达6次用户
            List<String> unDistinctIds = autoPushService.filterPushDevice(clDeviceVoList);
            if(CollectionUtils.isEmpty(unDistinctIds)) {
                continue;
            }

            //特殊事件类型过滤
            Map<String,Object> fltMap = filterSpecialCond(unDistinctIds,clDeviceVoList);
            if (MapUtils.isEmpty(fltMap)) {
                continue;
            }

            List<String> filterDistinctIds = (List<String>)fltMap.get("filterDistinctIds");
            if(CollectionUtils.isEmpty(filterDistinctIds)){
                continue;
            }
            List<ClDeviceVo> filterDeviceVoList = (List<ClDeviceVo>)fltMap.get("filterDeviceVoList");

            Map<String, List<String>> autoPushInfoList = autoPushService.autoPushContentInfo(filterDistinctIds, titleType);
            if (autoPushInfoList == null) {
                continue;
            }
            for (Map.Entry<String, List<String>> mapResult : autoPushInfoList.entrySet()) {
                String content = mapResult.getKey();
                List<String> distinctIdList = mapResult.getValue();
                List<ClDeviceVo> clDeviceVoNew = Lists.newArrayList();
                for (ClDeviceVo clDeviceVo : filterDeviceVoList) {
                    for (String distinctId : distinctIdList) {
                        if (clDeviceVo.getDistinctId().equals(distinctId)) {
                            clDeviceVoNew.add(clDeviceVo);
                        }
                    }
                }
                List<AutoPushInfo> autoPushInfos = new ArrayList<>();
                autoPushService.pushTokenInfo(appPackage, autoPushInfos, videoId, filterDistinctIds, clDeviceVoNew);
                contentMap.put(content, autoPushInfos);
            }
        }
        log.info("【自动推送】填充推送数据===fillAutoPushInfo.fillAutoPushInfoBatch：{}",JSON.toJSONString(contentMap));
        return contentMap;
    }

    private Map<String,Object> filterSpecialCond(List<String> distinctIds, List<ClDeviceVo> clDeviceVoList){
        log.info("【自动推送】==========autoPushConfig activityType:{},eventType:{}",autoPushConfig.getActivityType(), autoPushConfig.getEventType());
        Map<String,Object> retMap = Maps.newHashMap();
        //策略类型
        Integer strategyType = LaidianUtils.getStrategyByEvent(autoPushConfig.getEventType());

        if(autoPushConfig.getEventType() == null || autoPushConfig.getEventType() == 0){
            return null;
        }

        List<String> filterDistinctIds = distinctIds;

        List<ClDeviceVo> filterDeviceVoList = clDeviceVoList;

        //签到推送：新增用户 今日未推送用户
        if(autoPushConfig.getActivityType() == 1 && autoPushConfig.getEventType() == 5003){
            filterDeviceVoList = clDeviceVoList.stream().filter(clDeviceVo ->
                    !autoPushService.isPushSignIn(clDeviceVo.getDeviceId())).collect(Collectors.toList());
        }

        //活动推送
        if(strategyType == 3){
            filterDeviceVoList = clDeviceVoList.stream().filter(clDeviceVo ->
                    !autoPushService.isFinishActivity(clDeviceVo.getDeviceId(),
                            autoPushConfig.getEventType(),autoPushConfig.getActivityType())).collect(Collectors.toList());
        }

        filterDistinctIds = filterDeviceVoList.stream().map(clDeviceVo
                -> clDeviceVo.getDistinctId()).collect(Collectors.toList());

        retMap.put("filterDeviceVoList",filterDeviceVoList);
        retMap.put("filterDistinctIds",filterDistinctIds);
        return retMap;
    }

    //==================================推送封装 end  ==================================
    public AutoPushConfig getAutoPushConfig() {
        return autoPushConfig;
    }

    public void setAutoPushConfig(AutoPushConfig autoPushConfig) {
        this.autoPushConfig = autoPushConfig;
    }

    public ClDeviceService getClDeviceService() {
        return clDeviceService;
    }

    public void setClDeviceService(ClDeviceService clDeviceService) {
        this.clDeviceService = clDeviceService;
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public AutoPushService getAutoPushService() {
        return autoPushService;
    }

    public void setAutoPushService(AutoPushService autoPushService) {
        this.autoPushService = autoPushService;
    }

    public String getAutoPushUrl() {
        return autoPushUrl;
    }

    public void setAutoPushUrl(String autoPushUrl) {
        this.autoPushUrl = autoPushUrl;
    }
}
