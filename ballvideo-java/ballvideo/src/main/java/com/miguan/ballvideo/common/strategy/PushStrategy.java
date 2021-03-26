package com.miguan.ballvideo.common.strategy;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.enums.PushChannel;
import com.miguan.ballvideo.common.util.HttpUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.AutoPushConfig;
import com.miguan.ballvideo.service.AutoPushService;
import com.miguan.ballvideo.service.ClDeviceService;
import com.miguan.ballvideo.vo.AutoPushInfo;
import com.miguan.ballvideo.vo.ClDeviceVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.LinkedMultiValueMap;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public abstract class PushStrategy {
    protected AutoPushConfig autoPushConfig;
    protected ClDeviceService clDeviceService;
    protected MongoTemplate mongoTemplate;
    protected AutoPushService autoPushService;
    protected String autoPushUrl;

    public abstract void doexecute();

    /**
     * 访问大数据，获得推送用户的信息。
     */
    protected boolean postDbInfo(AutoPushConfig autoPushConfig, SimpleDateFormat sdf, String appPackage, Map<Long, Set<String>> videoInfos, int pageNum, int pageSize, Calendar calendar) {

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
        body.add("pageNum",pageNum + "");
        body.add("pageSize",pageSize + "");
        body.add("packageName",appPackage);
        body.add("type",autoPushConfig.getTriggerEvent() + "");
        body.add("dd",sdf.format(calendar.getTime()));
        log.info("【自动推送】访问大数据findAutoPushList begin====,url={},pageNum={},pageSize={},packageName={},type={},dd={}",
                autoPushUrl,pageNum,pageSize,appPackage,autoPushConfig.getTriggerEvent(),sdf.format(calendar.getTime()));
        ResultMap result = HttpUtil.httpSend(autoPushUrl + "/api/push/video/findAutoPushList",body);
        log.info("【自动推送】访问大数据findAutoPushList end====,result:{}", JSON.toJSONString(result));
        if(result.getCode() == 200){
            List<Map<String,Object>> infoDataList = (List<Map<String,Object>>) result.getData();
            if(infoDataList != null && infoDataList.size() > 0){
                log.info(appPackage + "访问大数据，获得推送用户的信息数：{}", infoDataList.size());
                fillInfoMap(infoDataList,videoInfos);
                return true;
            }
            log.info("访问大数据，未获得推送用户的信息!");
        } else {
            log.error("访问大数据，获得推送用户的信息失败：{}", result.getMessage());
        }
        return false;
    }

    /**
     * 封装推送信息   video ==> list<distinctId>
     */
    protected Map<Long, Set<String>> fillInfoMap(List<Map<String,Object>> infoData, Map<Long,Set<String>> videoInfos) {
        for (int i = 0 ; i < infoData.size() ; i ++ ) {
            Map<String,Object> info = infoData.get(i);
            if(info == null){
                continue;
            }
            if(videoInfos.get(Long.valueOf(MapUtils.getString(info,"videoId"))) == null){
                Set<String> distinctList = new HashSet<>();
                distinctList.add(MapUtils.getString(info,"distinctId"));
                videoInfos.put(Long.valueOf(MapUtils.getString(info,"videoId")),distinctList);
            } else {
                Set<String> distinctList = videoInfos.get(Long.valueOf(MapUtils.getString(info,"videoId")));
                distinctList.add(MapUtils.getString(info,"distinctId"));
            }
        }
        return videoInfos;
    }

    /**
     * 填充推送数据
     */
    protected List<AutoPushInfo> fillAutoPushInfo(String appPackage, Map<Long, Set<String>> videoInfos) {
        List<AutoPushInfo> autoPushInfos = new ArrayList<>();
        Set<Long> videoInfoSet = videoInfos.keySet();
        for (Long videoId:videoInfoSet) {
            Map param = new HashMap<>();
            Set<String> videoInfo = videoInfos.get(videoId);
            if(CollectionUtils.isEmpty(videoInfo)){
                continue;
            }
            param.put("distinctIds",videoInfo);
            param.put("appPackage",appPackage);
            List<ClDeviceVo> clDeviceVos = clDeviceService.findAllTokensByDistinct(param);
            pushTokenInfo(appPackage, autoPushInfos, videoId, videoInfo, clDeviceVos);
        }
        log.info("【自动推送】填充推送数据===fillAutoPushInfo.autoPushInfos：{}",JSON.toJSONString(autoPushInfos));
        return autoPushInfos;
    }

    private void pushTokenInfo(String appPackage, List<AutoPushInfo> autoPushInfos, Long videoId, Set<String> videoInfo, List<ClDeviceVo> clDeviceVos) {
        AutoPushInfo pushInfo = new AutoPushInfo();
        pushInfo.setVideoId(videoId);
        pushInfo.setAppPackage(appPackage);
        pushInfo.setDistinctIds(videoInfo);
        Map<String, List<String>> result = new HashMap<>();
        Map<String, List<String>> connTokenDistinct = new HashMap<>();
        List<String> huaweiTokens = new ArrayList<>();
        List<String> oppoTokens = new ArrayList<>();
        List<String> vivoTokens = new ArrayList<>();
        List<String> xiaomiTokens = new ArrayList<>();
        List<String> youmenTokens = new ArrayList<>();
        List<String> huaweiDistincts = new ArrayList<>();
        List<String> oppoDistincts = new ArrayList<>();
        List<String> vivoDistincts = new ArrayList<>();
        List<String> xiaomiDistincts = new ArrayList<>();
        List<String> youmenDistincts = new ArrayList<>();
        // 排重处理
        Set<ClDeviceVo> clDeviceVoSet = new HashSet<>(clDeviceVos);
        clDeviceVos.clear();
        clDeviceVos.addAll(clDeviceVoSet);
        // 根据
        for (ClDeviceVo vo : clDeviceVos) {
            if (StringUtils.isNotEmpty(vo.getHuaweiToken())) {
                huaweiTokens.add(vo.getHuaweiToken());
                huaweiDistincts.add(vo.getDistinctId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getOppoToken())) {
                oppoTokens.add(vo.getOppoToken());
                oppoDistincts.add(vo.getDistinctId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getVivoToken())) {
                vivoTokens.add(vo.getVivoToken());
                vivoDistincts.add(vo.getDistinctId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getXiaomiToken())) {
                xiaomiTokens.add(vo.getXiaomiToken());
                xiaomiDistincts.add(vo.getDistinctId());
                continue;
            }
            if (StringUtils.isNotEmpty(vo.getDeviceToken())) {
                youmenTokens.add(vo.getDeviceToken());
                youmenDistincts.add(vo.getDistinctId());
            }
        }
        result.put(PushChannel.HuaWei.name(), huaweiTokens);
        result.put(PushChannel.OPPO.name(), oppoTokens);
        result.put(PushChannel.VIVO.name(), vivoTokens);
        result.put(PushChannel.XiaoMi.name(), xiaomiTokens);
        result.put(PushChannel.YouMeng.name(), youmenTokens);
        connTokenDistinct.put(PushChannel.HuaWei.name(), huaweiDistincts);
        connTokenDistinct.put(PushChannel.OPPO.name(), oppoDistincts);
        connTokenDistinct.put(PushChannel.VIVO.name(), vivoDistincts);
        connTokenDistinct.put(PushChannel.XiaoMi.name(), xiaomiDistincts);
        connTokenDistinct.put(PushChannel.YouMeng.name(), youmenDistincts);
        pushInfo.setTokens(result);
        pushInfo.setConnTokenDistinct(connTokenDistinct);
        autoPushInfos.add(pushInfo);
    }

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


/*    public static void main(String[] args) {
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap(16);
        body.add("pageNum","1");
        body.add("pageSize","1000");
        body.add("packageName","com.mg.xyvideo");
        body.add("type","1");
        body.add("dd","2020-10-20");
        ResultMap result = HttpUtil.httpSend("http://dev.bdrep.uheixia.com/api/push/video/findAutoPushList",body);
        if(result.getCode() == 200){
            List<Map<String,Object>> infoDataList = (List<Map<String,Object>>) result.getData();
            if(infoDataList != null && infoDataList.size() > 0){
                for (int i = 0 ; i < infoDataList.size() ; i ++ ) {
                    Map<String,Object> info = infoDataList.get(i);
                    if(info == null){
                        continue;
                    }
                    System.out.println(info.get("videoId").toString());
                    System.out.println(info.get("distinctId").toString());
                    System.out.println(Long.valueOf(info.get("videoId").toString()));
                }
            }
        }

    }*/
}
