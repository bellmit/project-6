package com.miguan.xuanyuan.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.constant.StrategyGroupConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.common.util.AbResultMap;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.dto.ab.*;
import com.miguan.xuanyuan.dto.request.AbTestRequest;
import com.miguan.xuanyuan.dto.ab.ChannelInfoVo;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.task.AbExpTask;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ab实验处理类
 *
 */
@Slf4j
@Service
public class AbExpService {

    @Resource
    AbPlatFormService abPlatFormService;

    @Resource
    private RedisService redisService;

    @Resource
    private MofangService mofangService;

    @Resource
    private StrategyGroupService strategyGroupService;

    @Resource
    private AbExpTask abExpTask;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 登录
     *
     * @return
     * @throws ServiceException
     */
    private String login() throws ServiceException{
        String token = redisService.get(RedisKeyConstant.AB_TEST_LOGIN_TOKEN);
        if(StringUtils.isEmpty(token)){
            AbResultMap resultMap = abPlatFormService.login();
            if(resultMap.getCode() == -1){
                throw new ServiceException("登录失败,请检查账号密码是否正确！");
            } else if (resultMap.getCode() != 0) {
                throw new ServiceException("登录时,AB实验平台产生了未知问题。！");
            }
            Map<String,Object> acr = (Map<String,Object>)resultMap.getData();
            token = MapUtils.getString(acr, "token");
            if(StringUtils.isNotEmpty(token)){
                redisService.set(RedisKeyConstant.AB_TEST_LOGIN_TOKEN, token,RedisKeyConstant.AB_TEST_LOGIN_TOKEN_SECONDS);
            }
        }
        return token;
    }


    public List<AppInfo> appList() throws ServiceException{
        String appList = redisService.get(RedisKeyConstant.AB_APP_LIST_TOKEN);
        if (StringUtils.isEmpty(appList)) {
            //1：登录
            String token = login();
            if(StringUtils.isEmpty(token)){
                throw new ServiceException("登录失败,获取的token为空");
            }
            AbResultMap resultMap = abPlatFormService.appList(token);
            if(resultMap.getCode() != 0){
                if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                    throw new ServiceException(resultMap.getMsg());
                }
                throw new ServiceException("修改运行状态时。Ab返回未知的code"+resultMap.getCode());
            }
            List<AppInfo> result = (List<AppInfo>)resultMap.getData();
            if(CollectionUtils.isNotEmpty(result)){
                String jsonStr = JSONObject.toJSONString(result);
                redisService.set(RedisKeyConstant.AB_APP_LIST_TOKEN,jsonStr,RedisKeyConstant.AB_APP_LIST_TOKEN_SECONDS);
            }
            return result;
        } else {
            List<AppInfo> result = JSONObject.parseObject(appList,List.class);
            return result;
        }
    }

    public Integer getAppIdByAppPackage(String appPackage) throws ServiceException{
        String cacheKey = RedisKeyConstant.AB_TEST_APP_ID + appPackage;
        String appIdCache = redisService.get(cacheKey);
        if (StringUtils.isEmpty(appIdCache)) {
            String token = login();
            if(StringUtils.isEmpty(token)){
                throw new ServiceException("登录失败,获取的token为空");
            }
            AbResultMap resultMap = abPlatFormService.getAppInfoByPackage(appPackage, token);
            if(resultMap.getCode() != 0){
                if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                    throw new ServiceException(resultMap.getMsg());
                }
                throw new ServiceException("获取app id失败：" + resultMap.getCode());
            }

            Map<String, Object> result = (Map<String,Object>)resultMap.getData();
            if (MapUtils.isEmpty(result) || !result.containsKey("app_id") || result.get("app_id") == null) {
                throw new ServiceException("找不到包对应的app_id:" + appPackage);
            }
            Integer appId = (Integer) result.get("app_id");
            redisService.set(cacheKey, appId,RedisKeyConstant.AB_TEST_APP_ID_SECONDS);
            return appId;
        } else {
            return Integer.parseInt(appIdCache);
        }
    }

    public List<AbLayer> getLayerInfo(String appPackage, Integer expId) throws ServiceException {
//        Integer appId = mofangService.searchAppId(appPackage);
        Integer appId = this.getAppIdByAppPackage(appPackage);
        //1：登录
        String token = login();
        if(StringUtils.isEmpty(token)){
            throw new ServiceException("登录失败,获取的token为空");
        }
        AbResultMap resultMap = abPlatFormService.getCondition(appId,expId,token);
        if(resultMap.getCode() != 0){
            if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                throw new ServiceException(resultMap.getMsg());
            }
            throw new ServiceException("修改运行状态时。Ab返回未知的code"+resultMap.getCode());
        }
        Map<String,Object> result = (Map<String,Object>)resultMap.getData();
        List<Map<String,Object>> abLayers = (List<Map<String,Object>>) result.get("layer_list");
        if(CollectionUtils.isEmpty(abLayers)){
            List<AbLayer> abLayerList = Lists.newArrayList();
            abLayerList.add(new AbLayer(0,"新建分层","100.0"));
            return abLayerList;
        }
        List<AbLayer> newAbLayerList = Lists.newArrayList();
        abLayers.forEach(abLayerMap -> {
            Integer id = MapUtils.getInteger(abLayerMap,"id");
            String name = MapUtils.getString(abLayerMap,"name");
            String traffic = name.substring(name.lastIndexOf("剩余") + 2,name.lastIndexOf("%流量"));
            newAbLayerList.add(new AbLayer(id,name,traffic));
        });
        newAbLayerList.add(new AbLayer(0,"新建分层","100.0"));
        return newAbLayerList;
    }


    private List<Integer> getAdTag(Integer app_id, String token) throws ServiceException{
        String adTag = redisService.get(RedisKeyConstant.AB_TEST_AD_TAG);
        List<Integer> tagIds = Lists.newArrayList();
        log.error("token:"+token);
        if(StringUtils.isEmpty(adTag)){
            AbResultMap resultMap = abPlatFormService.getTags(app_id,token);
            if(resultMap.getCode() != 0){
                if(resultMap.getCode() == 4001){
                    throw new ServiceException(resultMap.getMsg());
                }
                throw new ServiceException("获取标签时,AB实验平台产生了未知问题："+ resultMap.getMsg());
            }
            List<Map<String,Object>> tags = (List<Map<String,Object>>)resultMap.getData();
            if(CollectionUtils.isEmpty(tags)){
                throw new ServiceException("获取标签时，发现AB平台没有标签数据");
            }
            tags.forEach(tag -> {
                String name = MapUtils.getString(tag,"name");
                if(name.contains("广告")){
                    Integer id = MapUtils.getInteger(tag, "id");
                    tagIds.add(id);
                }
            });
            if(CollectionUtils.isNotEmpty(tagIds)){
                adTag = StringUtil.toString(tagIds,",");
                redisService.set(RedisKeyConstant.AB_TEST_AD_TAG,adTag,RedisKeyConstant.AB_TEST_AD_TAG_SECONDS);
            }
            return tagIds;
        } else {
            List<Integer> tags = StringUtil.strToIntegerList(adTag);
            return tags;
        }
    }


    /**
     * 添加实验层 （返回实验id）
     * @param abLayer
     * @param token
     * @return
     * @throws ServiceException
     */
    private Integer saveLayer(AbLayer abLayer, String token) throws ServiceException {
        //获取广告所对应的类别。
        log.error(abLayer.getName());
        if(CollectionUtils.isEmpty(abLayer.getTags())){
            List<Integer> tags = getAdTag(abLayer.getApp_id(),token);
            if(CollectionUtils.isEmpty(tags)){
                throw new ServiceException("获取标签时，发现AB平台没有标签数据");
            }
            abLayer.setTags(tags);
        }
        AbResultMap resultMap = abPlatFormService.saveLayer(abLayer,token);
        if (resultMap.getCode() != 0) {
            if(resultMap.getCode() != 0){
                if(resultMap.getCode() == 4001){
                    throw new ServiceException("新建分层错误：" + resultMap.getMsg());
                }
                throw new ServiceException("新建分层时,AB实验平台产生了未知问题："+ resultMap.getMsg());
            }
        }
        Map<String,Integer> result = (Map<String,Integer>)resultMap.getData();
        Integer id = MapUtils.getInteger(result,"id");
        return id;
    }

    private AbExperiment getExperimentInfo(String exp_id, Integer appId, String token) throws ServiceException{
        AbResultMap resultMap = abPlatFormService.getExperimentInfo(exp_id,appId,token);
        if (resultMap.getCode() != 0) {
            throw new ServiceException(resultMap.getMsg());
        }
        try{
            AbExperiment abExperiment = new AbExperiment();
            Map<String,Object> source = (Map<String,Object>)resultMap.getData();
            BeanUtils.copyProperties(abExperiment, source);
            fillExisAbInfo(abExperiment,source);
            return abExperiment;
        } catch (Exception e){
            throw new ServiceException("实验转换异常:"+e.getMessage());
        }
    }

    private void fillExisAbInfo(AbExperiment abExperiment, Map<String, Object> source) throws ParseException {
        abExperiment.setVersion_list(abExperiment.getGroup_list()); //实验组
        if(abExperiment.getPeriod() == null){
            abExperiment.setPeriod(Lists.newArrayList());
        }
        SimpleDateFormat sdfn = new SimpleDateFormat("yyyy-MM-dd");
        abExperiment.getPeriod().add(sdfn.parse(source.get("start_date").toString()));
        abExperiment.getPeriod().add(sdfn.parse(source.get("end_date").toString()));
    }

    /**
     * 检测发布时间
     *
     * @param abFlowGroupParam
     * @throws ServiceException
     */
    private void checkStateInfo(AbFlowGroupParam abFlowGroupParam) throws ServiceException {
        String pubTime = abFlowGroupParam.getPub_time();
        if (StringUtils.isNotEmpty(pubTime)) {
            Date pubTimeDate = null;
            try {
                pubTimeDate = sdf.parse(pubTime);
            } catch (ParseException e) {
                throw new ServiceException("发布时间格式错误");
            }

            if(StringUtils.isNotEmpty(pubTime) && pubTimeDate.compareTo(new Date()) <= 0){
                throw new ServiceException("推送时间必须大于当前时间");
            }
        }

    }

    /**
     * 添加实验信息
     * @param abExperiment
     * @param token
     * @return
     * @throws ServiceException
     */
    private Integer saveExperiment(AbExperiment abExperiment, String token) throws ServiceException{
        AbResultMap resultMap = abPlatFormService.saveAbExperiment(abExperiment,token);
        if (resultMap.getCode() != 0) {
            if(resultMap.getCode() != 0){
                if(resultMap.getCode() == 4001){
                    throw new ServiceException(resultMap.getMsg());
                }
                if(resultMap.getCode() == 1004){
                    if(resultMap.getMsg().indexOf("实验别名") >= 0){
                        abExperiment.incrCode();
                        return saveExperiment(abExperiment, token);
                    }
                    throw new ServiceException(resultMap.getMsg());
                }
                if(resultMap.getCode() == 1024){
                    throw new ServiceException("该实验，目前在AB平台已非预发版本！请前去AB平台确认，或者确认是否已运行！");
                }
                if(resultMap.getCode() == 1005){
                    throw new ServiceException(resultMap.getMsg());
                }
                if(resultMap.getCode() == 1008){
                    throw new ServiceException(resultMap.getMsg());
                }
                throw new ServiceException("保存实验时,AB实验平台产生了未知问题："+resultMap.getMsg());
            }
        }
        if(abExperiment.getId() == null){
            Map<String,Integer> result = (Map<String,Integer>)resultMap.getData();
            Integer id = MapUtils.getInteger(result,"exp_id");
            return id;
        } else {
            return abExperiment.getId();
        }
    }

    //添加流量分组
    private List<AbItem> saveFlowDistribution(AbFlowDistribution abFlowDistribution, String token) throws ServiceException {

        List<AbItem> abItemList = new ArrayList<>();

        Map<Integer,Integer> groups = Maps.newHashMap();
        List<Integer> groupIds = Lists.newArrayList();
        if(MapUtils.isEmpty(abFlowDistribution.getGroup_maps())){
            List<Map<String, Object>> groupInfos = getGroupInfo(abFlowDistribution, token);
            if(CollectionUtils.isEmpty(groupInfos)){
                throw new ServiceException("添加流量分组时,获取分组信息失败！");
            }
            List<AbTraffic> ratio = abFlowDistribution.getRatio();
            for (int i = 0 ; i < groupInfos.size() ; i ++) {
                Map<String, Object> groupInfo = groupInfos.get(i);
                Integer id = MapUtils.getInteger(groupInfo,"id");
                AbTraffic ratioInfo = ratio.get(i+1);
                if(ratioInfo.getId() == null || ratioInfo.getId() == -1){
                    groups.put(id,ratioInfo.getTraffic());
                } else {
                    groups.put(id,ratioInfo.getTraffic());
                }
                groupIds.add(id);

                AbItem abItem = new AbItem();
                abItem.setAbItemId(id.longValue());
                abItem.setVal((ratioInfo.getTraffic()/10));
                if (i == 0) {
                    abItem.setType(StrategyGroupConstant.STRATEGY_TYPE_CONTRAST);
                } else {
                    abItem.setType(StrategyGroupConstant.STRATEGY_TYPE_TEST);
                }
                abItemList.add(abItem);

            }
        }

        abFlowDistribution.setGroup_maps(groups);
        AbResultMap resultMap = abPlatFormService.saveAbFlowDistribution(abFlowDistribution,token);
        if (resultMap.getCode() != 0) {
            if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                throw new ServiceException(resultMap.getMsg());
            }
            throw new ServiceException("添加流量分组出错："+resultMap.getMsg());
        }
        return abItemList;
    }

    private List<Map<String,Object>> getGroupInfo(AbFlowDistribution abFlowDistribution, String token) throws ServiceException {
        AbResultMap groupInfo = abPlatFormService.searchGroupByExpId(abFlowDistribution.getExp_id(),token);
        if(groupInfo.getCode() != 0){
            if(groupInfo.getCode() == 4001 || groupInfo.getCode() == 4005){
                throw new ServiceException(groupInfo.getMsg());
            }
            throw new ServiceException("添加流量分组时,AB实验平台产生了未知问题");
        }
        return (List<Map<String,Object>>)groupInfo.getData();
    }

    /**
     * 更新状态
     *
     * @param param
     * @param token
     * @throws ServiceException
     */
    private void sendEditState(Map<String, Object> param, String token) throws ServiceException{
        AbResultMap resultMap = abPlatFormService.sendEditState(param,token);
        if(resultMap.getCode() != 0){
            if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                throw new ServiceException(resultMap.getMsg());
            }
            throw new ServiceException("修改运行状态时。Ab返回未知的code"+resultMap.getCode());
        }
        Long abId = Long.parseLong(param.get("experiment_id").toString());
        strategyGroupService.updateStrategyGroupByAbId(abId, Integer.parseInt(param.get("state").toString()));
    }

    public void sendEditState(Map<String, Object> param) throws ServiceException{
        String token = login();
        sendEditState(param, token);
    }


    public Map<String, Object> saveFlowInfo(AbFlowGroupParam abFlowGroupParam, AbTestRequest abTestRequest, String ip) throws ServiceException {
        //获取app信息
//        Integer appId = mofangService.searchAppId(abFlowGroupParam.getApp_type());

        Integer appId = this.getAppIdByAppPackage(abTestRequest.getPackageName());
        abFlowGroupParam.fillAppId(appId);

        Long strategyGroupId = abTestRequest.getStrategyGroupId();
        String flowId = strategyGroupId != null ? String.valueOf(strategyGroupId) : "";

        Integer experimentId = abFlowGroupParam.getExperiment_id();

        //1：登录
        String token = login();
        if (StringUtils.isEmpty(token)) {
            throw new ServiceException("登录时,获得token为空！");
        }

        XyStrategyGroup strategyGroupInfo = null;
        if (strategyGroupId != null && strategyGroupId > 0) {
            strategyGroupInfo = strategyGroupService.getDataById(strategyGroupId);
            if (strategyGroupInfo != null && strategyGroupInfo.getAbId() != null) {
                experimentId = strategyGroupInfo.getAbId().intValue();
            }
        }

        boolean flag = false;  //true为新增逻辑 false为修改逻辑
        int state = 0;
        //以 flowId 为主  experiment_id为辅
        if (StringUtils.isEmpty(flowId) && abFlowGroupParam.getExperiment_id() == null) {
            flag = true;
        } else if (abFlowGroupParam.getExperiment_id() == null){
            if (strategyGroupInfo != null && strategyGroupInfo.getAbId() != null) {
                experimentId = strategyGroupInfo.getAbId().intValue();
            }
        }

        if(strategyGroupInfo != null){
            AbExperiment abExperiment = getExperimentInfo(String.valueOf(strategyGroupInfo.getAbId()),appId,token);
            state = abExperiment.getState();
            abFlowGroupParam.getAbExperiment().setCode(abExperiment.getCode());
            //其它数据填充吧
            if(state != 0){ //当状态不等于0 时,只修改 需求文档 ,实验名称, 切量比例。
//                if(abFlowGroupParam.getState_bak() != null  && abFlowGroupParam.getState_bak() == 0){
//                    throw new ServiceException("实验已发布，保存失败，请重新编辑。");
//                }
                abExperiment.setDoc_url(abFlowGroupParam.getDoc_url());
                abExperiment.setName(abFlowGroupParam.getName());
                abFlowGroupParam.setAbExperiment(abExperiment);
            }
        }

        if(state == 0){
            checkStateInfo(abFlowGroupParam);
        }

        //1：保存实验分层
        Integer olayerId = abFlowGroupParam.getAbLayer().getId();

        Integer layerId = null;
        //double traffic = 0;
        if (olayerId == null || olayerId == -1 || olayerId == 0) {
            //添加实验分层
            layerId = saveLayer(abFlowGroupParam.getAbLayer(), token);
        } else {
            //存在该分层,不对该分层进行修改。
            layerId = abFlowGroupParam.getAbLayer().getId();

        }
        if (layerId == null) {
            throw new ServiceException("添加分层,ab未返回添加的主键。");
        }

        //2：保存实验信息
        abFlowGroupParam.getAbExperiment().setLayer_id(layerId);
        Integer expId = saveExperiment(abFlowGroupParam.getAbExperiment(), token);

        //3：保存流量分组
        abFlowGroupParam.getAbFlowDistribution().setExp_id(expId);


        List<AbItem> flowDistributions = saveFlowDistribution(abFlowGroupParam.getAbFlowDistribution(), token);

        String abExpCode = abFlowGroupParam.getAbExperiment().getCode();
        Map<String, Object> result = new HashMap<>();
        result.put("abId", expId.longValue());
        result.put("abExpCode", abExpCode);
        result.put("abItemList", flowDistributions);


        //如果状态不为0.则只修改 切量比例需求地址,实验名称。 //不做后续修改
        if(state != 0){
            return result;
        }

        //添加分组信息
        Integer positionId = abFlowGroupParam.getPosition_id();


        //数据保存过程无误
        log.info("===数据保存过程无误===");
        //4：是否开始运行
        //定时发布数据
        Map<String, Object> map = Maps.newHashMap();
        map.put("app_id", abFlowGroupParam.getApp_id());
        map.put("experiment_id", expId);
        map.put("state", 1); //目前：我们只有abFlowGroupParam.state == 1 为 发布状态. 还有一个是发布时间>当前时间。会定时推送
        try {
            if (abFlowGroupParam.getState() != null && abFlowGroupParam.getState() == 1) {
                //立即运行
                sendEditState(map, token);
                //可能有其它操作。删除定时任务
                abExpTask.deleteTask(expId);
            } else if (StringUtils.isNotEmpty(abFlowGroupParam.getPub_time()) && sdf.parse(abFlowGroupParam.getPub_time()).compareTo(new Date()) > 0 ) {
                //定时运行
                abExpTask.pubAbExp(map, expId, abFlowGroupParam.getPub_time());
            } else {
                //可能有其它操作。删除定时任务
                abExpTask.deleteTask(expId);
            }
        } catch (ParseException e){
            log.error("定时发布,解析日期格式时，发生异常。");
            throw new ServiceException("你输入的日期格式有误！");
        }

        return result;
    }

    /**
     * 执行ab
     *
     * @param appType
     * @param expId
     * @param state
     * @throws ServiceException
     */
    public void sendEditState(String appType, Integer expId, Integer state) throws ServiceException {
//        Integer appId = mofangService.searchAppId(appType);
        Integer appId = this.getAppIdByAppPackage(appType);
        String token = login();
        Map<String, Object> map = Maps.newHashMap();
        map.put("app_id", appId);
        map.put("experiment_id", expId);
        map.put("state", state);

        sendEditState(map, token);
    }

    /**
     * 定时运行
     *
     * @param appType
     * @param expId
     * @param state
     * @param beginTs
     * @throws ServiceException
     */
    public void sendDelayTaskState(String appType, Integer expId, Integer state, String beginTs) throws ServiceException {
//        Integer appId = mofangService.searchAppId(appType);
        Integer appId = this.getAppIdByAppPackage(appType);
        String token = login();
        Map<String, Object> map = Maps.newHashMap();
        map.put("app_id", appId);
        map.put("experiment_id", expId);
        map.put("state", state);

        abExpTask.deleteTask(expId);
        abExpTask.pubAbExp(map, expId, beginTs);
    }




    public Map<String, String> getFilterMap(Map<String, Object> abFlowMap){

        String channelIds = "",
                channel2Ids = "",
                version1 = "",
                version2 = "",
                version3 = "",
                version4 = "",
                versionFlag = "";
        List<Map<String,Object>> testArr = Lists.newArrayList();
        if(MapUtils.isNotEmpty(abFlowMap)){
            channelIds = MapUtils.getString(abFlowMap,"channel");
            channel2Ids = MapUtils.getString(abFlowMap,"channel2");
            version1 = MapUtils.getString(abFlowMap,"version1");
            version2 = MapUtils.getString(abFlowMap,"version2");
            version3 = MapUtils.getString(abFlowMap,"version3");
            version4 = MapUtils.getString(abFlowMap,"version4");
            versionFlag = MapUtils.getString(abFlowMap,"versionFlag");
            testArr = (List<Map<String, Object>>) abFlowMap.get("testArr");
        }
//        String channelNames = getChannelNames(channelIds);
//        String channelNames2 = getChannelNames(channel2Ids);


        String channelNames = channelIds;
        String channelNames2 = channel2Ids;

        String appVersion = "全部";
        if(StringUtils.isNotEmpty(version1) && StringUtils.isNotEmpty(version2) ){
            appVersion = version1 + "-" + version2;
        }else if(StringUtils.isNotEmpty(version1) && StringUtils.isEmpty(version2)){
            appVersion = versionFlag+version1;
        }else if(StringUtils.isEmpty(version1) && StringUtils.isNotEmpty(version2)){
            appVersion = versionFlag+version2;
        }else if (StringUtils.isNotEmpty(version3)){
            appVersion = versionFlag+version3;
        }else if (StringUtils.isNotEmpty(version4)){
            appVersion = versionFlag+version4;
        }

        if(StringUtils.isNotEmpty(channelNames2) && !"全部".equals(channelNames2)){
            channelNames = "排除:"+channelNames2;
        } else if( StringUtils.isNotEmpty(channelNames) && !"全部".equals(channelNames)){
            channelNames = "仅限:"+channelNames;
        }

        Map<String, String> data = new HashMap<>();
        data.put("channel", channelNames);
        data.put("appVersion", appVersion);


        return data;
    }

//    /**
//     * 获取渠道名称
//     * @param channelIds
//     * @return
//     */
//    private String getChannelNames(String channelIds) {
//        String channelNames = "";
//        if(StringUtils.isEmpty(channelIds)){
//            channelNames = "全部";
//            return channelNames;
//        }
//        List<String> ChannelList = new ArrayList<>();
//        ChannelList.add(channelIds);
//        List<ChannelInfoVo> channelInfoVoList = mofangService.findChannelInfoByKeys(ChannelList);
//        if (CollectionUtils.isNotEmpty(channelInfoVoList)) {
//            channelNames = channelInfoVoList.stream().map(channelInfoVo ->
//                    channelInfoVo.getChannelName()).collect(Collectors.joining(","));
//        }
//        return channelNames;
//    }



}
