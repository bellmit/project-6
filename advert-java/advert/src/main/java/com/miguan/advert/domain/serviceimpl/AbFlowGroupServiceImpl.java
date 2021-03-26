package com.miguan.advert.domain.serviceimpl;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.miguan.advert.common.abplat.ABPlatFormService;
import com.miguan.advert.common.constants.FlowGroupConstant;
import com.miguan.advert.common.constants.TableInfoConstant;
import com.miguan.advert.common.constants.VersionConstant;
import com.miguan.advert.common.construct.LogOperationConstruct;
import com.miguan.advert.common.exception.ServiceException;
import com.miguan.advert.common.task.AbExpTask;
import com.miguan.advert.common.util.*;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import com.miguan.advert.domain.mapper.AdOperationLogMapper;
import com.miguan.advert.domain.mapper.AdPlatMapper;
import com.miguan.advert.domain.mapper.FlowTestMapper;
import com.miguan.advert.domain.pojo.AdAdvertCode;
import com.miguan.advert.domain.pojo.AdAdvertFlowConfig;
import com.miguan.advert.domain.pojo.AdAdvertTestConfig;
import com.miguan.advert.domain.pojo.AdTestCodeRelation;
import com.miguan.advert.domain.service.AbFlowGroupService;
import com.miguan.advert.domain.service.TableInfoService;
import com.miguan.advert.domain.service.ToolMofangService;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import com.miguan.advert.domain.vo.interactive.*;
import com.miguan.advert.domain.vo.request.AbFlowGroupParam;
import com.miguan.advert.domain.vo.result.ABFlowGroupVo;
import com.miguan.advert.domain.vo.result.ABTestGroupVo;
import com.miguan.advert.domain.vo.result.AdvCodeInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: advert-java
 * @description: ab交互
 * @author: kangkh
 * @create: 2020-10-27 10:36
 **/
@Slf4j
@Service
public class AbFlowGroupServiceImpl implements AbFlowGroupService {


    @Resource
    private RedisService redisService;

    @Resource
    FlowTestMapper flowTestMapper;

    @Resource
    AdPlatMapper adPlatMapper;

    @Resource
    ABPlatFormService abPlatFormService;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private TableInfoService tableInfoService;

    @Resource
    private AdOperationLogMapper adOperationLogMapper;

    @Resource
    private AbExpTask abExpTask;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Integer searchAppId(String app_type) throws ServiceException {
        String appid = redisService.get(RedisKeyConstant.CHANNAL_SEARCH_APPID + app_type);
        if(StringUtils.isEmpty(appid)){
            Integer appValue = null;
            try {
                appValue = toolMofangService.searchAppId(app_type);
            } catch ( EmptyResultDataAccessException e){
                throw new ServiceException("请检查app_type在channel_group表有没有对应上！并且有app_id");
            }
            if(appValue != null){
                redisService.set(RedisKeyConstant.CHANNAL_SEARCH_APPID + app_type,appValue,RedisKeyConstant.CHANNAL_SEARCH_APPID_SECONDS);
                appid = appValue + "";
            }
        }
        if(appid == null){
            throw new ServiceException("请检查app_type在channel_group表有没有对应上！并且有app_id");
        }
        return Integer.parseInt(appid);
    }

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
                redisService.set(RedisKeyConstant.AB_TEST_LOGIN_TOKEN,token,RedisKeyConstant.AB_TEST_LOGIN_TOKEN_SECONDS);
            }
        }
        return token;
    }

    @Override
    public List<AppInfo> appList() throws ServiceException{
        String appList = redisService.get(RedisKeyConstant.AB_APP_LIST_TOKEN);
        if(StringUtils.isEmpty(appList)){
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
                throw new ServiceException("获取标签时,AB实验平台产生了未知问题。！"+ resultMap.getMsg());
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

    @Override
    public ResultMap saveFlowInfo(AbFlowGroupParam abFlowGroupParam, String flowId, String ip) throws ServiceException {
        //获取app信息
        Integer appId = searchAppId(abFlowGroupParam.getApp_type());
        abFlowGroupParam.fillAppId(appId);
        //1：登录
        String token = login();
        if (StringUtils.isEmpty(token)) {
            throw new ServiceException("登录时,获得token为空！");
        }
        //填充code
        if(StringUtils.isEmpty(abFlowGroupParam.getCode())){
            List<String> expCode = flowTestMapper.findAllCodeByPositionId(abFlowGroupParam.getPosition_id());
            String code = "ad_exp_"+abFlowGroupParam.getPosition_id()+"_0";
            if(CollectionUtils.isNotEmpty(expCode)){
                code = getMaxCode(expCode,abFlowGroupParam.getPosition_id());
            }
            abFlowGroupParam.setCode(code);
            abFlowGroupParam.getAbExperiment().setCode(code);
        }

        //获取实验信息。填充实验标识
        AdAdvertFlowConfig flowConfig = null;

        boolean flag = false;  //true为新增逻辑 false为修改逻辑
        int state = 0;
        //以 flowId 为主  experiment_id为辅
        if (StringUtils.isEmpty(flowId) && abFlowGroupParam.getExperiment_id() == null) {
            flag = true;
        } else if (abFlowGroupParam.getExperiment_id() == null){
            List<AdAdvertFlowConfig> flowConfigs = flowTestMapper.getAdvFlowConfLst(null, null, flowId, null);
            flowConfig = flowConfigs.get(0);
            if(StringUtils.isNotEmpty(flowConfig.getAb_flow_id()) && abFlowGroupParam.getExperiment_id() == null){
                abFlowGroupParam.getAbExperiment().setId(Integer.parseInt(flowConfig.getAb_flow_id()));
                abFlowGroupParam.setExperiment_id(Integer.parseInt(flowConfig.getAb_flow_id()));
            }
        } else {
            //走这里。表示  experiment_id 不为空,flowId 为空.该方法不提倡。
            List<AdAdvertFlowConfig> flowConfigs = flowTestMapper.getAdvFlowConfLst(null, null, null, abFlowGroupParam.getExperiment_id().toString());
            flowConfig = flowConfigs.get(0);
            flowId = flowConfig.getId().toString();
        }
        if(flowConfig != null){
            AbExperiment abExperiment = getExperimentInfo(flowConfig.getAb_flow_id(),appId,token);
            state = abExperiment.getState();
            abFlowGroupParam.getAbExperiment().setCode(abExperiment.getCode());
            //其它数据填充吧
            if(state != 0){ //当状态不等于0 时,只修改 需求文档 ,实验名称, 切量比例。
                if(abFlowGroupParam.getState_bak() != null  && abFlowGroupParam.getState_bak() == 0){
                    return ResultMap.success("实验已发布,无法保存","实验已发布，保存失败，请重新编辑。");  //该场景是 用户打开页面
                }
                abExperiment.setDoc_url(abFlowGroupParam.getDoc_url());
                abExperiment.setName(abFlowGroupParam.getName());
                abFlowGroupParam.setAbExperiment(abExperiment);
            }
        }
        String vmsg = "";
        if(state == 0){
            try {
                vmsg = checkStateInfo(abFlowGroupParam);
            } catch (ParseException e) {
                throw new ServiceException("时间格式错误！");
            }
        }
        if(StringUtils.isNotEmpty(vmsg)){
            throw new ServiceException(vmsg);
        }

        //1：保存实验分层
        Integer olayerId = abFlowGroupParam.getAbLayer().getId();

        Integer layerId = null;
        //double traffic = 0;
        if (olayerId == null || olayerId == -1) {
            //添加实验分层
            layerId = saveLayer(abFlowGroupParam.getAbLayer(), token);
        } else {
            //存在该分层,不对该分层进行修改。
            layerId = abFlowGroupParam.getAbLayer().getId();

            //获得实验层 (改变前端传过来的比量);  这个目前前端控制
            //traffic = getLayerTrafficById(olayerId,token);
        }
        if (layerId == null) {
            throw new ServiceException("添加分层,ab未返回添加的主键。");
        }

        //2：保存实验信息
        abFlowGroupParam.getAbExperiment().setLayer_id(layerId);
        Integer expId = saveExperiment(abFlowGroupParam.getAbExperiment(), token);

        //3：保存流量分组
        abFlowGroupParam.getAbFlowDistribution().setExp_id(expId);
        List<Integer> flowDistributions = saveFlowDistribution(abFlowGroupParam.getAbFlowDistribution(), token);


        //如果状态不为0.则只修改 切量比例需求地址,实验名称。 //不做后续修改
        if(state != 0){
            //更新配置时间
            flowTestMapper.updateName(abFlowGroupParam.getName(),flowId,state);
            return ResultMap.success();
        }

        //添加分组信息
        Integer positionId = abFlowGroupParam.getPosition_id();

        if(flag){
            flowConfig = addFlowInfo(abFlowGroupParam, expId, flowDistributions, positionId);
        } else {
            updateFlowInfo(flowId, flowDistributions,abFlowGroupParam);
        }

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

        //数据填充
        //获取过滤版本和渠道
//        ResultMap<Map<String,Object>> resultMap = abPlatFormService.getAbFlowMapByInt(expId + "");
//        if(resultMap.getCode() != 200){
//            return ResultMap.error(resultMap.getMessage());
//        }
//        Map<String, Object> abFlowMap =resultMap.getData();
//
//        //查询代码位列表
//        List<AdvCodeInfoVo> defaultPosCodeLstVos = getAdvCodeInfoVos(String.valueOf(positionId));
//
//        //返回当前编辑组的数据
//        ABFlowGroupVo abFlowGroupVo = returnFlowTest(flowConfig, defaultPosCodeLstVos, abFlowMap);
        return ResultMap.success();
    }

    private String getMaxCode(List<String> expCode,Integer positionId) {
        int codeId = 0;
        for (String code:expCode) {
            String codeStr = code.replace("ad_exp_" + positionId + "_","");
            int codeTmp = StringUtils.isEmpty(codeStr)? 0 : Integer.parseInt(codeStr);
            if(codeTmp > codeId){
                codeId = codeTmp;
            }
        }
        return "ad_exp_"+positionId+"_"+(codeId+1);
    }

    private AbExperiment getExperimentInfo(String exp_id, Integer appId, String token) throws ServiceException{
        AbResultMap resultMap = abPlatFormService.getExperimentInfo(exp_id,appId,token);
        if (resultMap.getCode() != 0) {
            throw new ServiceException(resultMap.getMsg());
        }
        try{
            AbExperiment abExperiment = new AbExperiment();
            Map<String,Object> source = (Map<String,Object>)resultMap.getData();
            BeanUtils.copyProperties(abExperiment,source);
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

    private String checkStateInfo(AbFlowGroupParam abFlowGroupParam) throws ParseException {
        String pubTime = abFlowGroupParam.getPub_time();
        if(StringUtils.isNotEmpty(pubTime) && sdf.parse(pubTime).compareTo(new Date()) <= 0){
            return "推送时间必须大于当前时间。";
        }
        return null;
    }

    private double getLayerTrafficById(Integer olayerId, String token) throws  ServiceException{
        AbResultMap resultMap = abPlatFormService.getLayerTrafficById(olayerId,token);
        if (resultMap.getCode() != 0) {
            throw new ServiceException(resultMap.getMsg());
        }
        JSONObject layerInfo = (JSONObject)JSONObject.toJSON(resultMap.getData());
        Object trafficObj = layerInfo.get("traffic");
        if(trafficObj instanceof Integer ){
            return Double.valueOf((Integer) layerInfo.get("traffic"));
        } else {
            return (double)layerInfo.get("traffic");
        }
    }

    private void updateFlowInfo(String flowId, List<Integer> flowDistributions, AbFlowGroupParam abFlowGroupParam) {
        //查询原本数据库配置信息
        List<AdAdvertTestConfig> advTestConfigs = flowTestMapper.getAdvTestConfLst(flowId,null);

        //更新配置时间
        flowTestMapper.updateInfo(abFlowGroupParam.getName(),abFlowGroupParam.getPub_time(),flowId,abFlowGroupParam.getState());

        List<AdAdvertTestConfig> testConfigs = advTestConfigs.stream().filter(conf -> {
            //如果是默认组
            if(conf.getType() == 0){
                return false;
            }
            String abTestId = String.valueOf(conf.getAb_test_id());
            String testId = String.valueOf(conf.getId());
            //现在不存在，需要删除原本的配置信息
            if(!flowDistributions.stream().anyMatch(id -> (id+"").equals(abTestId))){
                flowTestMapper.deleteTestById(testId);
                flowTestMapper.deleteConfigByConfId(testId);
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        for (int i = 0; i < flowDistributions.size(); i++) {
            String testId = flowDistributions.get(i)+"";
            //原本库里不存在，需要增加配置信息
            int type = 0;
            if(i == 0){
                type = 1;
                //如果有数据，并且为第一个，匹配到测试组。则更新为对照组
                if(CollectionUtils.isNotEmpty(testConfigs) &&
                        testConfigs.stream().anyMatch(conf ->
                                conf.getAb_test_id().equals(testId) && conf.getType() == FlowGroupConstant.TEST_GROUP_TYPE_TEST)){
                    flowTestMapper.updateTestType(String.valueOf(type),testId);
                }
            }else{
                type = 2;
                //如果有数据，并且为不是第一个,并且是对照组。则更新为实验组
                if(CollectionUtils.isNotEmpty(testConfigs) &&
                        testConfigs.stream().anyMatch(conf ->
                                conf.getAb_test_id().equals(testId) && conf.getType() == FlowGroupConstant.TEST_GROUP_TYPE_DUIZHAO)){
                    flowTestMapper.updateTestType(String.valueOf(type),testId);
                }
            }
            if(CollectionUtils.isEmpty(testConfigs) ||
                    testConfigs.stream().noneMatch(conf -> conf.getAb_test_id().equals(testId))){
                AdAdvertTestConfig config = new AdAdvertTestConfig(
                        null,flowId,testId,2,type,null,null,1);
                flowTestMapper.insertTestGroup(config);
            }
        }
        flowTestMapper.updateFlowTestState(flowId,"1");
    }

    private AdAdvertFlowConfig addFlowInfo(AbFlowGroupParam abFlowGroupParam, Integer expId, List<Integer> flowDistributions, Integer positionId) {
        AdAdvertFlowConfig flowConfig;//新增逻辑
        //新增实验分组
        String name = FlowGroupConstant.AB_FLOW_GROUP_NAME;
        if(StringUtils.isNotEmpty(abFlowGroupParam.getName())){
            name = abFlowGroupParam.getName();
        }
        flowConfig =
                new AdAdvertFlowConfig(null, name,
                        positionId,expId + "",2,1,null,null,1,abFlowGroupParam.getAbExperiment().getCode(),abFlowGroupParam.getPub_time(),abFlowGroupParam.getState());
        flowTestMapper.insertFlowGroup(flowConfig);
        AdAdvertTestConfig adAdvertTestConfig = flowTestMapper.getDefaultAdvTestConf(positionId);
        List<AdAdvertTestConfig> testConfigs = Lists.newArrayList();
        //新增流量分组
        for (int i = 0 ; i < flowDistributions.size() ; i ++) {
            int flowDist = flowDistributions.get(i);
            int type = 2;
            if(i == 0){
                //添加对照组
                type = 1;
            }  else {
                type = 2;
            }
            AdAdvertTestConfig testConfig = new AdAdvertTestConfig(
                    null,String.valueOf(flowConfig.getId()),flowDist+"",adAdvertTestConfig.getComputer() == null ? 2:adAdvertTestConfig.getComputer(),type,null,null,1);
            flowTestMapper.insertTestGroup(testConfig);
            testConfigs.add(testConfig);
        }
        if(abFlowGroupParam.getUse_default() != null && abFlowGroupParam.getUse_default() && CollectionUtils.isNotEmpty(testConfigs)){
            //使用默认组的代码位
            userDefalutCode(positionId,flowConfig.getId(),testConfigs.get(0).getId());
        }
        return flowConfig;
    }

    private void userDefalutCode(Integer positionId, Integer configId, Integer testConfigId) {
        if(testConfigId == null || configId == null){
            return ;
        }
        //添加关联关系
        List<AdTestCodeRelation> relas = flowTestMapper.queryDefalutRelation(positionId);
        relas.forEach(rela -> {
            AdTestCodeRelation newRela = new AdTestCodeRelation(null,testConfigId + "", rela.getCode_id(),
                    rela.getNumber(), rela.getMatching(),rela.getOrder_num(), null,null,1);
            flowTestMapper.insertRelaGroup(newRela);
        });

    }

    @Override
    public AbFlowGroupParam findAbflow(Integer positionId, String flowId, String appType, String ip) throws ServiceException {
        //Integer appId = searchAppId(appType);
        //根据positionId和flowId.查询对应的实验组
        List<AdAdvertFlowConfig> flowConfigs = flowTestMapper.getAdvFlowConfLst(null,null,flowId, null);
        AdAdvertFlowConfig adAdvertFlow = flowConfigs.get(0);
        //如果不存在，代表是默认流量分组。可能存在AB实验ID未配置的情况
        if(StringUtil.isEmpty(adAdvertFlow.getAb_flow_id())){
            throw new ServiceException("所选的实验，并不存在AB实验ID");
        }
        String expId = adAdvertFlow.getAb_flow_id();
        //获取过滤版本和渠道
        ResultMap<Map<String,Object>> resultMap = abPlatFormService.getAbFlowMapByInt(expId);
        if(resultMap.getCode() != 200){
            throw new ServiceException(resultMap.getMessage());
        }
        Map<String, Object> abFlowMap = resultMap.getData();
        return createAbFlowGroupParam(abFlowMap, positionId, expId,adAdvertFlow) ;
    }

    @Override
    public void deleteAbflow(String flowId, String appType, String ip) throws ServiceException {
        //删除本地的
        //日志删除config
        LogOperationConstruct construct = new LogOperationConstruct(tableInfoService, adOperationLogMapper);
        AdAdvertFlowConfig config = flowTestMapper.findAdFlowConfigById(flowId);
        //修改状态state
        flowTestMapper.updateStatusById(0,2,flowId);
        construct.logUpdate(TableInfoConstant.AD_ADVERT_FLOW_CONFIG,"state",config.getState()+"","0");
        construct.logUpdate(TableInfoConstant.AD_ADVERT_FLOW_CONFIG,"status",config.getStatus()+"","2");
        List<AdAdvertTestConfig> testConfigs = flowTestMapper.getAdvTestConfLst(flowId,null);
        Set<String> allCodes = Sets.newHashSet();
        if(CollectionUtils.isNotEmpty(testConfigs)){
            testConfigs.forEach(conf->{
                //日志删除test和relation
                List<AdTestCodeRelation>  adTestCodeRelationList = flowTestMapper.getTestCodeRelaLst(String.valueOf(conf.getId()),null);
                allCodes.addAll(adTestCodeRelationList.stream().map(adTestCodeRelation ->
                        String.valueOf(adTestCodeRelation.getCode_id())).collect(Collectors.toList()));

                flowTestMapper.updateTestStatusById(0,String.valueOf(conf.getId()));
                flowTestMapper.updateRelationStatusById(0,String.valueOf(conf.getId()));
                construct.logUpdate(TableInfoConstant.AD_ADVERT_TEST_CONFIG,"state",conf.getState()+"","0");
                construct.logUpdateList(TableInfoConstant.AD_TEST_CODE_RELATION,"state",adTestCodeRelationList,"0");
            });
        }
        if(config == null){
            return ;
        }
        //如果查出代码位在任意广告位上配置了策略，则为投放。否则为未投放
        List<Map> codeRelations = flowTestMapper.findTestCodeRelaByCodeId(new ArrayList<>(allCodes));
        Map<String,String> dataMap = Maps.newHashMap();
        for (String code : allCodes) {
            //如果存在
            if(codeRelations.stream().anyMatch(codeRela -> String.valueOf(codeRela.get("code_id")).equals(code))){
                dataMap.put(code,"1");
            }else{
                dataMap.put(code,"2");
            }
        }
        flowTestMapper.updateCodePutInByDataMap(dataMap);

        construct.saveLog(LogOperationConstruct.PATH_DELETE_FLOW_GROUP,ip,3);
        //再删除ab测试的数据  (删不删成都无所谓，别有太大压力)
        Integer appId = searchAppId(appType);
        //1：登录
        String token = login();
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("app_id", appId);
        map.put("experiment_id",config.getAbFlowId());
        map.put("state", 2);
        sendEditState(map,token);
        abExpTask.deleteTask(Integer.parseInt(config.getAbFlowId()));
    }


    @Override
    public void sendEditState(String appType,Integer state, String abFlowId, String ip) throws ServiceException {
        Integer appId = searchAppId(appType);
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("app_id", appId);
        map.put("experiment_id",abFlowId);
        map.put("state", state);
        sendEditState(map);
    }
    
    public void sendEditState(Map<String, Object> param) throws ServiceException{
        String token = login();
        sendEditState(param, token);
    }

    private AbFlowGroupParam createAbFlowGroupParam(Map<String, Object> abFlowMap, Integer positionId, String expId,AdAdvertFlowConfig adAdvertFlow) throws ServiceException{
        //实验信息和测试组信息
        Map<String,Object> expInfo = (Map<String,Object>) abFlowMap.get("exp_info");
        List<Map<String,Object>> testArr = (List<Map<String,Object>>) abFlowMap.get("testArr");
        AbFlowGroupParam param = new AbFlowGroupParam();
        param.setApp_id(MapUtils.getInteger(expInfo,"app_id"));
        param.setName(MapUtils.getString(expInfo,"name"));
        param.setDoc_url(MapUtils.getString(expInfo,"doc_url")); //doc_url目前没有
        param.setPosition_id(positionId);
        param.setLayer_id(MapUtils.getInteger(expInfo,"layer_id"));
        param.setExperiment_id(Integer.parseInt(expId));
        param.setCode(adAdvertFlow.getExp_code());
        param.setPub_time(adAdvertFlow.getPub_time());
        fillRatios(param,testArr);
        fillCondition(param,expInfo);
        param.setState_bak(MapUtils.getInteger(expInfo,"state"));
        param.setState(MapUtils.getInteger(expInfo,"state"));
        //todo use_default  pub_time  doc_url
        return param;
    }



    private void fillCondition(AbFlowGroupParam param, Map<String,Object> expInfo) throws ServiceException{
        String condition = MapUtils.getString(expInfo, "condition");
        if(StringUtils.isNotEmpty(condition)){
            List<String> filterParamLst = Arrays.asList(condition.split("&&"));
            for (int i = 0; i < filterParamLst.size(); i++) {
                String filterObj = filterParamLst.get(i);
                JSONObject jsonObject = null;
                try {
                    jsonObject = JSONObject.parseObject(filterObj);
                } catch (Exception e) {
                    throw new ServiceException("AB实验实验平台条件解析失败，报文：" + condition);
                }
                String key = (String) jsonObject.get("key");
                String operation = (String) jsonObject.get("operation");
                String value = (String) jsonObject.get("value");
                if("app_version".equals(key)){
                    if(operation.equals("in")){
                        param.setVersion_type(FlowGroupConstant.VERSION_TYPE_ONLY);
                        param.setVersion_ids(value);
                    }
                    if(operation.equals("notin")){
                        param.setVersion_type(FlowGroupConstant.VERSION_TYPE_EX);
                        param.setVersion_ids(value);
                    }
                }
                if("father_channel".equals(key)){
                    if(operation.equals("in")){
                        param.setChannel_type(FlowGroupConstant.CHANNEL_TYPE_ONLY);
                        param.setChannel_ids(value);
                    }
                    if(operation.equals("notin")){
                        param.setChannel_type(FlowGroupConstant.CHANNEL_TYPE_EX);
                        param.setChannel_ids(value);
                    }
                }
                if("is_new".equals(key)){
                    if(operation.equals("in")){
                        //我们项目  is_new = 0 对应全部  1:是  2:否 ,ab平台  0： 否,1 是
                        if(value == null){
                            param.setIs_new(0);
                        } else if ("0".equals(value)){
                            param.setIs_new(2);
                        } else {
                            param.setIs_new(Integer.parseInt(value));
                        }
                    }
                }
            }
        }
        if(param.getIs_new() == null){
            param.setIs_new(0);
        }
        if(param.getVersion_type() == null){
            param.setVersion_type(1);
        }
        if(param.getChannel_type() == null){
            param.setChannel_type(1);
        }
    }

    private void fillRatios(AbFlowGroupParam param, List<Map<String, Object>> testArr) {
        List<AbTraffic> maps = Lists.newArrayList();
        Integer total = 0;
        for (Map<String, Object> test:testArr) {
            Integer traffic = MapUtils.getInteger(test,"traffic");
            maps.add(new AbTraffic(MapUtils.getInteger(test,"id"),MapUtils.getString(test,"name"),traffic));
            total += traffic;
        }
        maps.add(0,new AbTraffic(-1,"总流量",total));
        param.setRatio(maps);
    }


    public List<AbLayer> getLayerInfo(String appType, Integer expId) throws ServiceException{
        Integer appId = searchAppId(appType);
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
            abLayerList.add(new AbLayer(-1,"新建分层","100.0"));
            return abLayerList;
        }
        List<AbLayer> newAbLayerList = Lists.newArrayList();
        abLayers.forEach(abLayerMap -> {
            Integer id = MapUtils.getInteger(abLayerMap,"id");
            String name = MapUtils.getString(abLayerMap,"name");
            String traffic = name.substring(name.lastIndexOf("剩余") + 2,name.lastIndexOf("%流量"));
            newAbLayerList.add(new AbLayer(id,name,traffic));
        });
        newAbLayerList.add(new AbLayer(-1,"新建分层","100.0"));
        return newAbLayerList;
    }

    public ResultMap getVersionInfo(String appType)  {
        List<String> appVersions = toolMofangService.findVersionInfo(appType);
        if(CollectionUtils.isEmpty(appVersions)){
            return ResultMap.success();
        }
        List<String> appVersionList = appVersions.stream().filter(appVersion -> VersionConstant.APP_XLD.contains(appType) ?
                VersionUtil.compare(appVersion, VersionConstant.XLD_DEFALUT_VERSION) : VersionUtil.compare(appVersion, VersionConstant.XY_DEFALUT_VERSION)).collect(Collectors.toList());
        return ResultMap.success(appVersionList);
    }

    private void sendEditState(Map<String, Object> param, String token) throws ServiceException{
        AbResultMap resultMap = abPlatFormService.sendEditState(param,token);
        if(resultMap.getCode() != 0){
            if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                throw new ServiceException(resultMap.getMsg());
            }
            throw new ServiceException("修改运行状态时。Ab返回未知的code"+resultMap.getCode());
        }
        flowTestMapper.updateStatus(Integer.parseInt(param.get("state").toString()),param.get("experiment_id").toString());
    }

    //添加流量分组
    private List<Integer> saveFlowDistribution(AbFlowDistribution abFlowDistribution, String token) throws ServiceException {
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
            }
        }
        abFlowDistribution.setGroup_maps(groups);
        AbResultMap resultMap = abPlatFormService.saveAbFlowDistribution(abFlowDistribution,token);
        if (resultMap.getCode() != 0) {
            if(resultMap.getCode() == 4001 || resultMap.getCode() == 4005){
                throw new ServiceException(resultMap.getMsg());
            }
            throw new ServiceException("添加流量分组时,AB实验平台产生了未知问题。！"+resultMap.getMsg());
        }
        return groupIds;
    }

    private Integer subTraffic(Integer traffic, double oldtraffic) {
        if(oldtraffic == 0.0 ||  oldtraffic == 0 || traffic == 0){
            return traffic;
        }
        if(oldtraffic == 100 || oldtraffic == 100.0){
            return 0;
        }
        BigDecimal divi = new BigDecimal(10);
        //剩余流量
        BigDecimal tra = new BigDecimal(traffic);
        BigDecimal oldTra = new BigDecimal(oldtraffic);
        BigDecimal total = new BigDecimal(100);
        BigDecimal sub = total.subtract(oldTra); //剩余的份额
        return tra.multiply(sub.divide(total)).setScale(1, RoundingMode.HALF_UP).intValue();
    }

    private List<Map<String,Object>> getGroupInfo(AbFlowDistribution abFlowDistribution, String token) throws ServiceException {
        AbResultMap groupInfo = abPlatFormService.searchGroupByExpId(abFlowDistribution.getExp_id(),token);
        if(groupInfo.getCode() != 0){
            if(groupInfo.getCode() == 4001 || groupInfo.getCode() == 4005){
                throw new ServiceException(groupInfo.getMsg());
            }
            throw new ServiceException("添加流量分组时,AB实验平台产生了未知问题。！");
        }
        return (List<Map<String,Object>>)groupInfo.getData();
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
                throw new ServiceException("保存实验时,AB实验平台产生了未知问题。！"+resultMap.getMsg());
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
                    throw new ServiceException(resultMap.getMsg());
                }
                throw new ServiceException("新建分层时,AB实验平台产生了未知问题。！"+ resultMap.getMsg());
            }
        }
        Map<String,Integer> result = (Map<String,Integer>)resultMap.getData();
        Integer id = MapUtils.getInteger(result,"id");
        return id;
    }



    /**
     * 获取渠道名称
     * @param channelIds
     * @return
     */
    private String getChannelNames(String channelIds) {
        String channelNames = "";
        if(StringUtil.isEmpty(channelIds)){
            channelNames = "全部";
        }
        List<ChannelInfoVo> channelInfoVoList = toolMofangService.findChannelInfoByKeys(channelIds);
        if (CollectionUtils.isNotEmpty(channelInfoVoList)) {
            channelNames = channelInfoVoList.stream().map(channelInfoVo ->
                    channelInfoVo.getChannelName()).collect(Collectors.joining(","));
        }
        return channelNames;
    }

    /**
     * 手动分组，对渠道、版本号进行过滤
     * @param defaultPosCodeLstVos
     * @param channelIds
     * @param version1
     * @param version2
     * @return
     */
    private List<AdvCodeInfoVo> getAdvCodesByFilter(List<AdvCodeInfoVo> defaultPosCodeLstVos,
                                                    String channelIds,String channel2Ids, String version1,String version2,String version3,String version4) {
        List<AdvCodeInfoVo> newAdvCodes = Lists.newArrayList();
        for (int j = 0; j < defaultPosCodeLstVos.size(); j++) {
            AdvCodeInfoVo vo = defaultPosCodeLstVos.get(j);

            boolean chaneFlag = false;


            //如果AB不限制 或者 作用版本位全部
            if(StringUtil.isEmpty(channelIds) || StringUtil.isEmpty(channel2Ids) || FlowGroupConstant.CHANNEL_TYPE_ALL == vo.getChannel_type()){
                chaneFlag = true;
            } else if(FlowGroupConstant.CHANNEL_TYPE_EX == vo.getChannel_type() && "-1".equals(vo.getChannel_ids())){
                chaneFlag = false;
            } else if(FlowGroupConstant.CHANNEL_TYPE_ONLY == vo.getChannel_type() && "-1".equals(vo.getChannel_ids())){
                chaneFlag = true;
            } else {
                if(StringUtil.isNotEmpty(channelIds)){
                    //过滤渠道
                    List<String> channelLst = Arrays.asList(channelIds.split(","));
                    for (int z = 0; z < channelLst.size(); z++) {
                        if(vo.getChannel_ids().indexOf(channelLst.get(z)) > -1 && FlowGroupConstant.CHANNEL_TYPE_ONLY == vo.getChannel_type()
                                || vo.getChannel_ids().indexOf(channelLst.get(z)) == -1 && FlowGroupConstant.CHANNEL_TYPE_EX == vo.getChannel_type()){
                            chaneFlag = true;
                            break;
                        }
                    }
                }
                if(StringUtil.isNotEmpty(channel2Ids)){
                    //过滤渠道
                    String[] csp2 = channel2Ids.split(",");
                    List<String> codeIdslLst = Arrays.asList(vo.getChannel_ids().split(","));
                    if(codeIdslLst.size() > csp2.length){
                        chaneFlag = true;
                    }
                    for (int z = 0; z < codeIdslLst.size(); z++) {
                        if(channel2Ids.indexOf(codeIdslLst.get(z)) == -1 && FlowGroupConstant.CHANNEL_TYPE_ONLY == vo.getChannel_type()){
                            chaneFlag = true;
                            break;
                        }
                    }
                }
            }

            //过滤版本号
            boolean versionFlag1 = false;
            boolean versionFlag2 = false;
            boolean versionFlag3 = false;
            boolean versionFlag4 = false;
            //如果都是空 则代表全部
            if(StringUtils.isEmpty(version1) && StringUtils.isEmpty(version2)){
                versionFlag1 = true;
                versionFlag2 = true;
            }else{
                if(StringUtils.isNotEmpty(version1) &&
                        VersionUtil.compare(vo.getVersion2(),version1)){
                    if(StringUtils.isEmpty(version2) || VersionUtil.compare(version2, vo.getVersion1())){
                        versionFlag1 = true;
                    }
                }
                if(StringUtils.isNotEmpty(version2) &&
                        VersionUtil.compare(version2,vo.getVersion1())){
                    if(StringUtils.isEmpty(version1) || VersionUtil.compare(vo.getVersion2(), version1)){
                        versionFlag2 = true;
                    }
                }
            }
            if(StringUtils.isEmpty(version3) && StringUtils.isEmpty(version4)){
                versionFlag3 = true;
                versionFlag4 = true;
            }else{
                if(StringUtils.isNotEmpty(version3) ){
                    List<String> versionList = Arrays.asList(version3.split(","));
                    for (String version:versionList) {
                        //如果没有版本1。那么version要小于版本2
                        if(StringUtils.isEmpty(vo.getVersion1())){
                            if(VersionUtil.compare(vo.getVersion2(),version)){
                                versionFlag3 = true;
                                break;
                            }
                        } else if (StringUtils.isEmpty(vo.getVersion2()) ){
                            if(VersionUtil.compare(version,vo.getVersion1())){
                                versionFlag3 = true;
                                break;
                            }
                        } else if(VersionUtil.compare(version,vo.getVersion1()) && VersionUtil.compare(vo.getVersion2(),version)){
                            versionFlag3 = true;
                            break;
                        }
                    }
                }
                //version4 排除版本  暂时无法判断。排除版本是  1.4.6,1.5.7,2.0.0 的格式， 而version1 和version2选择的是 [version1,version2] 区间格式. （必须得知道全部版本才能实现）
                if(StringUtils.isNotEmpty(version4)){
                    versionFlag4 = true;
                }
            }

            if(chaneFlag && (versionFlag1 || versionFlag2) && (versionFlag3 || versionFlag4)){
                newAdvCodes.add(vo);
            }

        }
        return newAdvCodes;
    }

    /**
     * 查询代码位列表
     * @param positionId
     * @return
     */
    private List<AdvCodeInfoVo> getAdvCodeInfoVos(String positionId) {
        List<AdvCodeInfoVo> defaultPosCodeLstVos = Lists.newArrayList();
        List<AdAdvertCode> advCodeInfoVoLst = flowTestMapper.getAdvCodeInfoVoLst(positionId);
        if(CollectionUtils.isEmpty(advCodeInfoVoLst)){
            return defaultPosCodeLstVos;
        }
        //组装 默认广告配置代码位信息
        advCodeInfoVoLst.forEach(adv -> {

            String platName = adPlatMapper.findPlatNameByPlatKey(adv.getPlat_key());
            String channelNames = getChannelNames(adv.getChannel_ids());
            AdvCodeInfoVo advCodeInfoVo = new AdvCodeInfoVo(
                    adv.getId(),0,0,0,adv.getPlat_key(),platName, adv.getAd_id(),adv.getLadder(),adv.getLadder_price(),adv.getChannel_type(),
                    adv.getChannel_ids(), channelNames,adv.getType_key(),adv.getVersion1(),adv.getVersion2(),adv.getPermission(),adv.getPut_in(),
                    DateUtils.parseDateToStr(adv.getCreated_at(),DateUtils.DATEFORMAT_STR_010),DateUtils.parseDateToStr(adv.getUpdated_at(),DateUtils.DATEFORMAT_STR_010), 0d);
            defaultPosCodeLstVos.add(advCodeInfoVo);
        });
        return defaultPosCodeLstVos;
    }

    /**
     * 返回当前编辑组的数据
     * @param flowConfig
     * @param abFlowMap
     * @param defaultPosCodeLstVos
     * @return
     */
    private ABFlowGroupVo returnFlowTest(AdAdvertFlowConfig flowConfig, List<AdvCodeInfoVo> defaultPosCodeLstVos, Map<String, Object> abFlowMap){

        String channelIds = "",channel2Ids = "",version1 = "",version2 = "",version3 = "",version4 = "";
        List<Map<String,Object>> testArr = Lists.newArrayList();
        if(MapUtils.isNotEmpty(abFlowMap)){
            channelIds = MapUtils.getString(abFlowMap,"channel");
            channel2Ids = MapUtils.getString(abFlowMap,"channel2");
            version1 = MapUtils.getString(abFlowMap,"version1");
            version2 = MapUtils.getString(abFlowMap,"version2");
            version3 = MapUtils.getString(abFlowMap,"version3");
            version4 = MapUtils.getString(abFlowMap,"version4");
            testArr = (List<Map<String, Object>>) abFlowMap.get("testArr");
        }
        String channelNames = getChannelNames(channelIds);
        //默认分组
        int flowType = 1;
        List<AdvCodeInfoVo> newAdvCodes = Lists.newArrayList();
        if(FlowGroupConstant.DEFAULT_GROUP_TYPE == flowConfig.getType()){
            newAdvCodes.addAll(defaultPosCodeLstVos);
        }else if(FlowGroupConstant.HANDLER_GROUP_TYPE == flowConfig.getType()) {//手动分组，对渠道、版本号进行过滤
            newAdvCodes = getAdvCodesByFilter(defaultPosCodeLstVos, channelIds,channel2Ids, version1, version2,version3,version4);
            flowType = 2;
        }
        //获取所有实验分组
        List<AdAdvertTestConfig> allTestConfigs = flowTestMapper.getAdvTestConfLst(String.valueOf(flowConfig.getId()),null);

        //获取排序过代码位列表的实验组
        List<ABTestGroupVo> newTestConfs = getAdvTestConfs(allTestConfigs, newAdvCodes, testArr);

        //获取默认测试组
        //List<AdAdvertTestConfig> defaultTestConfigs = allTestConfigs.stream().filter(
        //        testConf -> "0".equals(String.valueOf(testConf.getType()))).collect(Collectors.toList());
        //组装默认实验分组的返回参数
        //List<ABTestGroupVo> defaultTestGroupVoList = getAbTestGroupVos(defaultTestConfigs, defaultPosCodeLstVos);

        String appVersion = "全部";
        if(StringUtil.isNotEmpty(version1) && StringUtil.isNotEmpty(version2) ){
            appVersion = version1 + "-" + version2;
        }else if(StringUtil.isNotEmpty(version1) && StringUtil.isEmpty(version2)){
            appVersion = "大于等于"+version1;
        }else if(StringUtil.isEmpty(version1) && StringUtil.isNotEmpty(version2)){
            appVersion = "小于等于"+version2;
        }else if (StringUtil.isNotEmpty(version3)){
            appVersion = "仅限版本:"+version3;
        }else if (StringUtil.isNotEmpty(version4)){
            appVersion = "排除版本:"+version4;
        }


        ABFlowGroupVo abFlowGroupVo = new ABFlowGroupVo(flowConfig.getId(),flowConfig.getName(),
                channelNames,flowType, flowConfig.getTest_state(),appVersion, flowConfig.getAb_flow_id(),newAdvCodes,newTestConfs,flowConfig.getPub_time(),flowConfig.getStatus(),null,null);

        return abFlowGroupVo;
    }

    /**
     * 获取排序过代码位列表的实验组
     * @param adAdvertTestConfigs
     * @param defaultPosCodeLstVos
     * @param testArr
     * @return
     */
    private List<ABTestGroupVo> getAdvTestConfs(List<AdAdvertTestConfig> adAdvertTestConfigs, List<AdvCodeInfoVo> defaultPosCodeLstVos, List<Map<String,Object>> testArr) {

        List<ABTestGroupVo> abTestGroupVoList = Lists.newArrayList();
        if(CollectionUtils.isEmpty(adAdvertTestConfigs)){
            return abTestGroupVoList;
        }

        for (int i = 0; i < adAdvertTestConfigs.size(); i++) {
            AdAdvertTestConfig conf = adAdvertTestConfigs.get(i);
            String configId = String.valueOf(conf.getId());
            //策略类型
            int computer = conf.getComputer();

            //获取实验组与代码位对应的策略配置信息
            List<AdTestCodeRelation> codeRelations = flowTestMapper.getTestCodeRelaLst(configId,computer);

            List<AdvCodeInfoVo> posCodeLstVos = Lists.newArrayList();
            if(CollectionUtils.isNotEmpty(codeRelations)) {
                codeRelations.stream().forEach(codeRela -> {
                    Optional<AdvCodeInfoVo> advCodeInfoVoOptional = defaultPosCodeLstVos.stream().filter(codeLstVo ->
                            codeLstVo.getId().equals(codeRela.getCode_id())).findFirst();
                    if (advCodeInfoVoOptional.isPresent()) {
                        AdvCodeInfoVo advCodeInfoVo = advCodeInfoVoOptional.get();
                        advCodeInfoVo.setNumber(codeRela.getNumber());
                        advCodeInfoVo.setMatching(codeRela.getMatching());
                        advCodeInfoVo.setOrderNum(codeRela.getOrder_num() != null? codeRela.getOrder_num() : 0);
                        posCodeLstVos.add(advCodeInfoVoOptional.get());
                    }
                });
            }
            //手动配比,正序。手动排序逆序
            if(conf.getComputer() == FlowGroupConstant.HANDLER_MATCHING){
                posCodeLstVos.sort(Comparator.comparing(AdvCodeInfoVo::getNumber));
            }else{
                posCodeLstVos.sort(Comparator.comparing(AdvCodeInfoVo::getOrderNum).reversed());
            }
            ABTestGroupVo testInfoVo = new ABTestGroupVo(
                    conf.getId(), conf.getAb_test_id(), null, conf.getComputer(), conf.getType(), posCodeLstVos,null);
            if(StringUtils.isNotEmpty(conf.getAb_test_id()) && CollectionUtils.isNotEmpty(testArr)){
                Optional<Map<String,Object>> mapOptional =
                        testArr.stream().filter(test -> conf.getAb_test_id().equals(String.valueOf(test.get("id")))).findFirst();
                if(mapOptional.isPresent()){
                    testInfoVo.setAb_test_id(conf.getAb_test_id());
                    testInfoVo.setPercentage(String.valueOf((double)(
                            (Integer)mapOptional.get().get("traffic"))/10));
                    testInfoVo.setName(mapOptional.get().get("name").toString());
                }
            }
            abTestGroupVoList.add(testInfoVo);
        }
        return abTestGroupVoList;
    }
}
