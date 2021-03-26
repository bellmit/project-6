package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.adv.AdvFieldType;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.dynamicquery.Dynamic2Query;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.AdvertService;
import com.miguan.ballvideo.service.RedisService;
import com.miguan.ballvideo.service.ToolMofangService;
import com.miguan.ballvideo.vo.AbTestAdvParamExpVo;
import com.miguan.ballvideo.vo.AbTestAdvParamVo;
import com.miguan.ballvideo.vo.AbTestRuleVo;
import com.miguan.ballvideo.vo.AdvertCodeVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service(value="advertService")
public class AdvertServiceImpl implements AdvertService {

    @Resource
    private Dynamic2Query dynamic2Query;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private RedisService redisService;

    @Override
    public List<AdvertCodeVo> commonSearch(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        List<AdvertCodeVo> advertCodeVos = getAdvertsByParams(queueVo, param,AdvFieldType.All);
        if(CollectionUtils.isEmpty(advertCodeVos)) {
            return null;
        }
        if (advertCodeVos.get(0).getComputer() == 1) {
            return AdvUtils.computerAndSort(advertCodeVos);
        } else if (advertCodeVos.get(0).getComputer() == 2) {
            return AdvUtils.sort(advertCodeVos);
        } else if (advertCodeVos.get(0).getComputer() == 3) {
            //自动排序
            return AdvUtils.sort(advertCodeVos);
        } else {
            return AdvUtils.sort(advertCodeVos);
        }
    }

    @Override
    public List<AdvertCodeVo> getAdertsByGame(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        param.put("game", "game");
        List<AdvertCodeVo> list = getAdvertsByParams(queueVo, param,AdvFieldType.All);;
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<AdvertCodeVo> result = new ArrayList<>();
        Map<String, List<AdvertCodeVo>> mapList = list.stream().collect(Collectors.groupingBy(AdvertCodeVo::getPositionType));
        for (Map.Entry<String, List<AdvertCodeVo>> map : mapList.entrySet()) {
            List<AdvertCodeVo> advertCodeVos = map.getValue();
            advertCodeVos = AdvUtils.sortByComputer(advertCodeVos);
            if(CollectionUtils.isEmpty(advertCodeVos))continue;//空指针问题修复 addshixh0809
            //每个广告位置只需返回一个广告给第三方调用
            result.add(advertCodeVos.get(0));
        }
        return result;
    }

  /**
   * 如果开关开启，返回多个广告位，并随机返回一个广告位
   * @param param
   * @return
   */
  @Override
  public Map<String, Object> getLockScreenInfo(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        String androidLockScreenToken = Global.getValue("android_lock_screen_token");
        result.put("androidLockScreenToken", androidLockScreenToken);
        //锁屏是开的时候才去查广告信息
        if ("10".equals(androidLockScreenToken)) {
            param.put("lockScreen", "1");
            List<AdvertCodeVo> advertVoList = getAdvertsByParams(queueVo, param,AdvFieldType.PositionType);
            if (CollectionUtils.isNotEmpty(advertVoList)) {
                List<String> list = advertVoList.stream().map(AdvertCodeVo::getPositionType).collect(toList());
                int i = new Random().nextInt(list.size());
                result.put("positionType", list.get(i));
            }
        }
        return result;
    }

    @Override
    public Map<String, List<AdvertCodeVo>> getAdversByPositionTypes(AbTestAdvParamsVo queueVo, Map<String, Object> param) {
        List<String> positionTypes = (List<String>) param.get("positionTypes");
        positionTypes = new ArrayList<>(positionTypes);
        List<String> positionTypeList = new ArrayList<>(positionTypes);
        List<AdvertCodeVo> list = Lists.newArrayList();
        param.remove("positionTypes", positionTypes);
        for (String positionType : positionTypeList) {
            //魔方后台-广告总开关:true禁用，false非禁用
            param.put("positionType", positionType);
            if (toolMofangService.stoppedByMofang(param)) {
                positionTypes.remove(positionType);
            }
            List<AdvertCodeVo> advList = getAdvertInfoByParams(queueVo, param,AdvFieldType.All);
            if (CollectionUtils.isNotEmpty(advList)) {
                list.addAll(advList);
            }
        }
        /*if (positionTypes.size() > 1) {
            param.put("positionTypes", "'"+String.join("','",positionTypes)+"'");
        } else if (positionTypes.size() == 1) {
            param.put("positionTypes", "'"+positionTypes.get(0)+"'");
        } else {
            return null;
        }
        List<AdvertCodeVo> list = getAdvertInfoByParams(queueVo, param,AdvFieldType.All);*/
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<String, List<AdvertCodeVo>> mapList = list.stream().collect(Collectors.groupingBy(AdvertCodeVo::getPositionType));
        for (Map.Entry<String, List<AdvertCodeVo>> map : mapList.entrySet()) {
            List<AdvertCodeVo> advertCodeVos = map.getValue();
            map.setValue(AdvUtils.sortByComputer(advertCodeVos));
        }
        return mapList;
    }

  /**
     * 查询一个或多个广告位的广告信息
     * @param param
     * @param fieldType
     * @return
     */
    @Override
    public List<AdvertCodeVo> getAdvertInfoByParams(AbTestAdvParamsVo queueVo, Map<String, Object> param,int fieldType) {
        fieldType = getData(queueVo, param,fieldType);
        String key = AdvUtils.filter(param);
        String json = redisService.get(key);
        if(RedisKeyConstant.EMPTY_VALUE.equals(json)){
            return null;
        }
        List<AdvertCodeVo> sysVersionVos = dynamic2Query.getAdversWithCache(param,fieldType);
        if(CollectionUtils.isEmpty(sysVersionVos)){
            redisService.set(key,RedisKeyConstant.EMPTY_VALUE,RedisKeyConstant.EMPTY_VALUE_SECONDS);
            return null;
        }else{
            return sysVersionVos;
        }
    }

    //AB实验平台实验Id
    public int getData(AbTestAdvParamsVo queueVo, Map<String, Object> param,int fieldType) {
        if(queueVo == null){
            return fieldType;
        }
        if (StringUtils.isNotEmpty(queueVo.getAbExp())){
            //获取实验参数
            List<AbTestAdvParamExpVo> advParamExpVos = new ArrayList<>();
            Arrays.stream(queueVo.getAbExp().split(",")).forEach(abexp -> {
                if(StringUtil.isNotEmpty(abexp)){
                    AbTestAdvParamExpVo advParamExpVo = new AbTestAdvParamExpVo();
                    String[] absplits = abexp.split("\\-");
                    if(!StringUtils.isEmpty(absplits[0]) && !StringUtils.isEmpty(absplits[1])){
                        advParamExpVo.setExp_id(absplits[0]);
                        advParamExpVo.setGroup_id(Long.valueOf(absplits[1]));
                        advParamExpVos.add(advParamExpVo);
                    }
                }
            });
            if(CollectionUtils.isEmpty(advParamExpVos)){
                return fieldType;
            }
            //命中实验：命中实验规则（一个广告位对应一个实验。如果一个广告位配置多个实验，那就是配置问题）
            Map<String, Object> paramPosition = new HashMap<>();
            paramPosition.put("positionType", param.get("positionType"));
            paramPosition.put("mobileType", param.get("mobileType"));
            paramPosition.put("appPackage", param.get("appPackage"));
            List<AbTestRuleVo> abTestRuleVos = dynamic2Query.getABTextAdversByRule(paramPosition,0);
            if (CollectionUtils.isNotEmpty(abTestRuleVos)) {
                abTestRuleVos.forEach(rule -> {
                    if(rule != null && rule.getAbFlowId() != null){
                        String abFlowId = rule.getAbFlowId();
                        for (AbTestAdvParamExpVo vo:advParamExpVos) {
                            if(StringUtils.isNotEmpty(vo.getExp_id()) && vo.getExp_id().equals(abFlowId)){
                                param.put("queryType", "abTestAdv");
                                param.put("abTestId", vo.getGroup_id());
                                param.put("openStatus",rule.getOpenStatus() == null ? 0 : rule.getOpenStatus()); //防止缓存失效
                                return;
                            }
                        }
                    }
                });
            }
        } else if (StringUtils.isNotEmpty(queueVo.getAbTestId())){
            List<AbTestAdvParamVo> advParamVos = JSON.parseArray(queueVo.getAbTestId(), AbTestAdvParamVo.class);
            Map<String, Object> paramPosition = new HashMap<>();
            paramPosition.put("positionType", param.get("positionType"));
            paramPosition.put("mobileType", param.get("mobileType"));
            paramPosition.put("appPackage", param.get("appPackage"));
            paramPosition.put("queryType", "position");
            List<AdvertCodeVo> advert = dynamic2Query.getAdversWithCache(paramPosition,AdvFieldType.PositionType);
            if (CollectionUtils.isNotEmpty(advert)) {
                Long positionId = advert.get(0).getPositionId();
                if (positionId > 0) {
                    String keyStr = "ad_exp_" + positionId + "_";
                    for (AbTestAdvParamVo advParamVo : advParamVos) {
                        if (StringUtils.isNotEmpty(advParamVo.getExp_key()) && advParamVo.getExp_key().contains(keyStr)) {
                            param.put("queryType", "abTestAdv");
                            param.put("abTestId", advParamVo.getGroup_id());
                            break;
                        }
                    }
                }
            }
            //命中广告位  尝试或许实验下的开关状态
            if("abTestAdv".equals(param.get("queryType"))){
                Map<String,Object> params = Maps.newHashMap();
                params.put("abTestId",param.get("abTestId"));
                Integer openStatus = dynamic2Query.queryOpenStatusByAbTestId(params);
                param.put("openStatus",openStatus == null ? 0 : openStatus); //防止缓存失效
            }
        }
        //命中实验，且开启状态为1
        if("abTestAdv".equals(param.get("queryType"))){
            if(param.get("openStatus") != null && StringUtils.isNotEmpty(param.get("openStatus").toString()) && Integer.valueOf(param.get("openStatus").toString()) == 1){
                fieldType = 4;
            }
        }

        return fieldType;
    }

    /**
     * 查询广告信息
     * @param param
     * @param fieldType
     * @return
     */
    public List<AdvertCodeVo> getAdvertsByParams(AbTestAdvParamsVo queueVo, Map<String, Object> param,int fieldType) {
        //魔方后台-广告总开关:true禁用，false非禁用
        if (toolMofangService.stoppedByMofang(param)) {
            return null;
        }
        return getAdvertInfoByParams(queueVo, param,fieldType);
    }

    @Override
    public Map<String,String> getAppIdByAppPackage(String appPackage) {
        try{
            Map<String,String> resultMap = new HashMap<>();
            List<Map<String,String>> resultList = dynamic2Query.nativeQueryListMap("select app_id,plat_key from ad_appId_ios where app_package=? ",appPackage);
            if (CollectionUtils.isNotEmpty(resultList)) {
                for (Map map : resultList) {
                    resultMap.put(map.get("plat_key").toString(),map.get("app_id").toString());
                }
            }
            return resultMap;
        }catch (Exception e){
            return null;
        }
    }
}
