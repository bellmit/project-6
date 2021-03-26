package com.miguan.xuanyuan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.util.StringUtil;
import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.exception.ValidateException;
import com.miguan.xuanyuan.common.util.XyUtil;
import com.miguan.xuanyuan.common.util.adv.AdvUtils;
import com.miguan.xuanyuan.dto.*;
import com.miguan.xuanyuan.dto.common.AbTestAdvParamsDto;
import com.miguan.xuanyuan.entity.XyApp;
import com.miguan.xuanyuan.mapper.AdCodeMapper;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.sdk.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 广告代码位serviceImpl
 **/
@Service
public class AdCodeServiceImpl implements AdCodeService {

    private static final Logger logger = LoggerFactory.getLogger(AdCodeServiceImpl.class);

    @Resource
    private MofangService mofangService;
    @Resource
    private AbTestRuleService abTestRuleService;
    @Resource
    private AdCodeMapper adCodeMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private XySourceAppService xySourceAppService;

    @Resource
    private XyAppService xyAppService;

    @Override
    public ConfigureInfoVo configureInfo(String appKey, String secretKey) throws Exception {
        XyApp xyApp = xyAppService.findByAppKeyAndSecret(appKey,secretKey);
        if(xyApp == null){
            throw new ValidateException("应用不存在,或者已从广告平台下架。");
        }
        ConfigureInfoVo vo = new ConfigureInfoVo();
        //获取第三方应用信息
        List<SourceAppInfoVo> info = xySourceAppService.findAppInfo(xyApp.getId());
        vo.setAppKey(appKey);
        vo.setSourceAppInfos(info);
        vo.setCacheTime(redisService.getInt(RedisKeyConstant.CONFIG_CODE + RedisConstant.CONFIGURE_CACHE_TIME)); //阶梯广告延迟);
        return vo;
    }

    /**
     * 获取广告代码位列表
     * @param queueVo AB测试vo
     * @param paramDto 接口参数
     * @return
     */
    public AdDataDto adCodeInfoList(AbTestAdvParamsDto queueVo, AdvertCodeParamDto paramDto) {
        Map<String, Object> param = XyUtil.convertObjToMap(paramDto);
        //魔方后台-广告总开关:true禁用，false非禁用
        if (mofangService.stoppedByMofang(param)) {
            return null;
        }
        if(queueVo != null) {
            param.putAll(XyUtil.convertObjToMap(queueVo));
        }
        AdDataDto adDataDto = adCodeMapper.findPositionInfo(param);
        //是否AB测试，并且是否命中AB测试
        isAbTestAndHit(param);
        //查询广告代码位列表
        String abTestId = (param.get("abTestId") == null ? null : (String.valueOf(param.get("abTestId"))));
        List<AdCodeDto> advertCodeList = getAdCodeListWithCache(paramDto, (int)param.get("isAbTest"), (int)param.get("isHit"), abTestId);
        List<AdCodeDto> advertCodes = filterVersionAndChannel(advertCodeList,param.get("appVersion"),param.get("channelId"));

        if(adDataDto == null){
            adDataDto = new AdDataDto();
        }

        if(CollectionUtils.isEmpty(advertCodes)) {
            return adDataDto;
        }
        //填充自定义字段

        if((int)param.get("isHit") == 1 && (int)param.get("isAbTest") == 1){
            fillCustomRuleList(advertCodes);
        } else {
            fillCustomRule(advertCodes);
        }
        //根据类型和算法排序
        if (advertCodes.get(0).getSortType() == 1) {
            //手动配比
            List<AdCodeDto> advertCode = AdvUtils.computerAndSort(advertCodes);
            adDataDto.setAdCodes(advertCode);
            return adDataDto;
        } else if (advertCodes.get(0).getSortType() == 2) {
            //自动排序(多维度)
            Map<String, Object> sortParam = new HashMap<>();
            sortParam.put("isNew", paramDto.getIsNewApp() != null && paramDto.getIsNewApp() == 1 ? 1 : 0);  //是否新用户，1：新用户，0：老用户
            sortParam.put("city", StringUtils.isBlank(paramDto.getCity()) ? "-1" : paramDto.getCity());  //城市
            sortParam.put("channel", StringUtils.isBlank(XyUtil.removeEndNum(paramDto.getChannelId())) ? "-1" : XyUtil.removeEndNum(paramDto.getChannelId())); //渠道id
            sortParam.put("appKey", paramDto.getAppKey()); //包名
            sortAutoMulti(advertCodes, sortParam);
            this.setAdvertCodeDelayMillis(advertCodes);
        }
        adDataDto.setAdCodes(advertCodes);
        return adDataDto;
    }

    private List<AdCodeDto> filterVersionAndChannel(List<AdCodeDto> advertCodeList, Object versionObj, Object channelIdObj) {
        if(versionObj == null && channelIdObj == null){
            return advertCodeList;
        }
        if(CollectionUtils.isEmpty(advertCodeList)){
            return advertCodeList;
        }
        String version = versionObj == null ? null : versionObj.toString();
        String channel = channelIdObj == null ? null : channelIdObj.toString();
        return advertCodeList.stream().filter(adCode -> {
            boolean vb = XyUtil.validateVersion(adCode,version);
            boolean cb = XyUtil.validateChannel(adCode,channel);
            if(vb && cb){
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public AdPositionVo findPositionCustomRule(AbTestAdvParamsDto queueVo, AdvertPositionParamDto paramDto) {
        Map<String, Object> param = XyUtil.convertObjToMap(paramDto);
        if(queueVo != null) {
            param.putAll(XyUtil.convertObjToMap(queueVo));
        }
        //是否AB测试，并且是否命中AB测试
        isAbTestAndHit(param);
        //查询广告代码位列表
        String abTestId = (param.get("abTestId") == null ? null : (String.valueOf(param.get("abTestId"))));
        String customField = getCustomFieldCache(paramDto, (int)param.get("isAbTest"), (int)param.get("isHit"), abTestId);
        AdPositionVo adPositionVo = new AdPositionVo(paramDto.getPositionKey());
        if((int)param.get("isHit") == 1 && (int)param.get("isAbTest") == 1){
            fillCustomRuleList(adPositionVo,customField);
        } else {
            fillCustomRule(adPositionVo,customField);
        }
        return adPositionVo;
    }

    private void fillCustomRule(Object object, String customField) {
        if(StringUtils.isEmpty(customField)){
            return ;
        }
        List<CustomField> customFields = JSON.parseArray(customField, CustomField.class);
        if(CollectionUtils.isNotEmpty(customFields)){
            for ( int i = 0 ; i < customFields.size() ; i ++) {
                Class clazz = object.getClass();
                try {
                    Field field = clazz.getDeclaredField("customRule" + (i + 1));
                    field.setAccessible(true);
                    field.set(object,customFields.get(i).getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("【广告策略】自定义字段转换异常customField：{}",customField);
                }
            }
        }
    }

    private void fillCustomRuleList(Object object, String customField) {
        if(StringUtils.isEmpty(customField)){
            return ;
        }
        List<String> customFields = JSONArray.parseArray(customField,String.class);
        if(CollectionUtils.isNotEmpty(customFields)){
            for ( int i = 0 ; i < customFields.size() ; i ++) {
                Class clazz = object.getClass();
                try {
                    Field field = clazz.getDeclaredField("customRule" + (i + 1));
                    field.setAccessible(true);
                    field.set(object,customFields.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("【广告策略】自定义字段转换异常customField：{}",customField);
                }
            }
        }
    }

    private void fillCustomRule(List<AdCodeDto> advertCodes) {
        advertCodes.stream().forEach(advertCode -> {
            fillCustomRule(advertCode, advertCode.getCustomField());
        });
    }

    private void fillCustomRuleList(List<AdCodeDto> advertCodes) {
        advertCodes.stream().forEach(advertCode -> {
            fillCustomRuleList(advertCode, advertCode.getCustomField());
        });
    }

    /**
     * 设置延迟时间
     *
     * @param list
     */
    public void setAdvertCodeDelayMillis(List<AdCodeDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int ladderDelayMillis =  redisService.getInt(RedisKeyConstant.CONFIG_CODE + RedisConstant.XY_LADDER_DELAY_MILLIS); //阶梯广告延迟
        int commonDelayMillis =  redisService.getInt(RedisKeyConstant.CONFIG_CODE + RedisConstant.XY_COMMON_DELAY_MILLIS); //通跑广告延迟

        int delayMillis = 0;
        int preLadderPrice = 0; //前一个阶梯广告的阶梯价
        int commonDelayDiff = 0; //通跑广告第一次延迟
        for (AdCodeDto advertCodeDto : list) {
            int ladderPrice = advertCodeDto.getLadderPrice();
            if (ladderPrice > 0) {
                if (preLadderPrice == 0) {
                    preLadderPrice = ladderPrice;
                }
                if (ladderPrice != preLadderPrice) {
                    delayMillis += ladderDelayMillis;
                }
                preLadderPrice = ladderPrice;
                advertCodeDto.setDelayMillis(delayMillis);
            }  else {
                if (delayMillis == 0 && preLadderPrice == 0) {
                    commonDelayDiff = commonDelayMillis;
                }
                delayMillis += commonDelayMillis;
                advertCodeDto.setDelayMillis(delayMillis - commonDelayDiff);
            }

        }
    }

    /**
     * 统计多维度代码位ecpm
     * @param adIds  代码位列表
     * @param params 参数
     * @return
     */
    public Map<String, Double> countMultiEcpm(List<String> adIds, Map<String, Object> params) {
        String date = this.queryNearDate();  //查询最近一天的第三方代码位统计数据的日期
        params.put("adIds", adIds);
        params.put("date", date);
        List<AdProfitVo> adMultiList = queryAdMultiData(params); //查询多维度最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
        List<AdProfitVo> adTotalList = queryAdTotalList(params);  //查询代码位最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
        Map<String, AdProfitVo> adMultiMap = adMultiList.stream().collect(Collectors.toMap(AdProfitVo::getAdId, r->r));
        Map<String, AdProfitVo> adTotalMap = adTotalList.stream().collect(Collectors.toMap(AdProfitVo::getAdId, r->r));
        Map<String, AdProfitVo> adMap = null; //查询最近一天代码位的曝光数，点击数，有效曝光数，有效点击数

        //计算代码位多维度的ecpm
        //各维度组合下的ecpm=1000*(自监测点击数*点击单价)/自监测展现数;  点击单价=代码位总收益/有效点击数（有效点击数是这个代码位一天的总点击数）
        Map<String, Double> adEcpm = new HashMap<>();
        Map<String, Double> adProfitMap = getAdProfit(date);  //获取98和第三方代码位最新一天的代码位收益
        for(String adId : adIds) {
            double adProfit = adProfitMap.get(adId) == null ? 0.0 : Double.parseDouble(String.valueOf(adProfitMap.get(adId)));  //代码位收益
            AdProfitVo adMultiData = adMultiMap.get(adId);
            adMultiData = (adMultiData == null ? new AdProfitVo() : adMultiData);
            AdProfitVo adTotalData = adTotalMap.get(adId);
            adTotalData = (adTotalData == null ? new AdProfitVo() : adTotalData);
            if(adMultiData == null) {
                //如果多维度下的数据为空，则取代码位下的曝光数，点击数，有效曝光数，有效点击数
                adMap = this.getAdMap(adMap, date, String.valueOf(params.get("appKey")));
                adMultiData = (adMap.get(adId) == null ? new AdProfitVo() : adMap.get(adId));
            }
            int validClick = (adTotalData == null || adTotalData.getValidClick() == null ? 0 : adTotalData.getValidClick());  //代码位有效点击数
            int show = (adMultiData.getShow() == null ? 0 : adMultiData.getShow());  //展示数(自监测)
            int click = (adMultiData.getClick() == null ? 0 : adMultiData.getClick());  //点击数（自监测）
            if(adProfit == 0 || validClick == 0 || show == 0 || click == 0) {
                adEcpm.put(adId, 0D);
                continue;
            }
            double price = adProfit / validClick; //点击单价
            double ecpm = 1000 * (click * price) / show;
            adEcpm.put(adId, ecpm);
        }
        return adEcpm;
    }

    /**
     * 代码位多维度排序方法
     * @param advertVos
     * @param params
     */
    public void sortAutoMulti(List<AdCodeDto> advertVos, Map<String, Object> params) {
        List<AdCodeDto> ladderAds = new ArrayList<>();  //阶梯价代码位列表(阶梯价格为0的，就表示是阶梯价代码位)
        List<AdCodeDto> notLadderAds = new ArrayList<>();  //非阶梯价代码位列表（通跑层代码位）
        for(AdCodeDto advertCodeDto : advertVos) {
            if(advertCodeDto.getLadderPrice() > 0) {
                ladderAds.add(advertCodeDto);  //阶梯价代码位
            } else {
                notLadderAds.add(advertCodeDto);  //非阶梯价代码位
            }
        }
        //阶梯价代码位根据阶梯价格降序排
        ladderAds = ladderAds.stream().sorted(Comparator.comparing(AdCodeDto::getLadderPrice).reversed()).collect(Collectors.toList());

        //非阶梯价代码位排序(通跑层)
        if(!notLadderAds.isEmpty()) {
            List<String> adIds = notLadderAds.stream().map(AdCodeDto::getSourceCodeId).collect(Collectors.toList());
            Map<String, Double> adEcpm = this.countMultiEcpm(adIds, params);  //统计出通跑层代码位ecpm
            notLadderAds.forEach(r->{
                r.setEcpm(adEcpm.get(r.getSourceCodeId()));
            });
            //ecpm从高到低排序，相同ecpm之间按照配置顺序排序
            notLadderAds = notLadderAds.stream().sorted(Comparator.comparing(AdCodeDto::getEcpm, Comparator.reverseOrder()).thenComparing(AdCodeDto::getOptionValue, Comparator.reverseOrder())).collect(Collectors.toList());
        }

        advertVos.clear();
        //最终排序：先根据代码位阶梯价降序排，在根据非阶梯价代码位多维度ecpm降序排，最后ecpm为0的在根据排序字段排
        advertVos.addAll(ladderAds);
        advertVos.addAll(notLadderAds);
    }

    /**
     * 查询最近一天的第三方代码位统计数据的日期
     * @return
     */
    private String queryNearDate() {
        String value = redisService.get(RedisConstant.XY_SORT_MULTI_DATE);
        if(StringUtils.isNotBlank(value)) {
            return value;
        } else {
            Integer platNum = adCodeMapper.countUsedPlat();  //查询当前在使用的第三方广告商数
            String date = adCodeMapper.queryNearDate(platNum);
            if(StringUtils.isNotEmpty(date)){
                date = date.replaceAll("-","");
            }
            redisService.set(RedisConstant.XY_SORT_MULTI_DATE, date, RedisConstant.DEFALUT_SECONDS);
            return date;
        }
    }

    /**
     * 获取98和第三方代码位最新一天的代码位收益
     * @return
     */
    private Map<String, Double> getAdProfit(String date) {
        String value = redisService.get(RedisConstant.XY_AD_PROFIT); //从缓存获取代码位收益
        if(StringUtils.isNotBlank(value)) {
            return JSONObject.parseObject(value, Map.class);
        } else {
            Map<String, Double> profit98Map = new HashMap<>();
            //查询最近一天第三方代码位收益
            List<AdProfitVo> profitThirdList = adCodeMapper.queryAdProfit(date);
//            //查询最近一天98代码位收益
//            List<AdProfitVo> profit98List = adCodeMapper.queryAd98Profit(date);
            //合并第三方和98代码位收益
            if(profitThirdList != null) {
                profit98Map.putAll(profitThirdList.stream().collect(Collectors.toMap(AdProfitVo::getAdId, AdProfitVo::getProfit)));
            }
//            if(profit98List != null) {
//                profit98Map.putAll(profit98List.stream().collect(Collectors.toMap(AdProfitVo::getAdId, AdProfitVo::getProfit)));
//            }
            redisService.set(RedisConstant.XY_AD_PROFIT, JSON.toJSONString(profit98Map), RedisConstant.DEFALUT_SECONDS);
            return profit98Map;
        }
    }

    /**
     * 查询多维度最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
     * @param params
     * @return
     */
    private List<AdProfitVo> queryAdMultiData(Map<String, Object> params) {
        String key = RedisConstant.AD_MULTI_DATA + params.toString();
        String value =redisService.get(key);

        if(StringUtils.isNotBlank(value)) {
            //从缓存获取
            return JSONArray.parseArray(value, AdProfitVo.class);
        } else {
            List<AdProfitVo> list = adCodeMapper.queryAdMultiData(params);
            redisService.set(key, JSON.toJSONString(list), RedisConstant.DEFALUT_SECONDS); //把数据存入缓存
            return list;
        }
    }

    /**
     * 查询对应代码位的数据
     * @param params
     * @return
     */
    private List<AdProfitVo> queryAdTotalList(Map<String, Object> params) {
        params.put("isNew", -1);  //是否新用户，1：新用户，0：老用户
        params.put("city", "-1");  //城市
        params.put("channel", "-1"); //渠道id
        String key = RedisConstant.AD_TOTAL_DATA + params.toString();
        String value =redisService.get(key);

        if(StringUtils.isNotBlank(value)) {
            //从缓存获取
            return JSONArray.parseArray(value, AdProfitVo.class);
        } else {
            List<AdProfitVo> list = adCodeMapper.queryAdMultiData(params);
            redisService.set(key, JSON.toJSONString(list), RedisConstant.DEFALUT_SECONDS); //把数据存入缓存
            return list;
        }
    }

    private Map<String, AdProfitVo> getAdMap(Map<String, AdProfitVo> adMap, String date, String appKey) {
        if(adMap == null) {
            List<AdProfitVo> list = queryAdData(date, appKey);
            adMap = list.stream().collect(Collectors.toMap(AdProfitVo::getAdId, r->r));
            return adMap;
        }
        return adMap;
    }

    /**
     * 查询最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
     * @param date 日期
     * @param appKey appKey
     * @return
     */
    private List<AdProfitVo> queryAdData(String date, String appKey) {
        String key = RedisConstant.AD_DATA + date + appKey;
        String value =redisService.get(key);
        if(StringUtils.isNotBlank(value)) {
            //从缓存获取
            return JSONArray.parseArray(value, AdProfitVo.class);
        } else {
            List<AdProfitVo> list = adCodeMapper.queryAdData(date, appKey);
            redisService.set(key, JSON.toJSONString(list), RedisConstant.DEFALUT_SECONDS); //把数据存入缓存
            return list;
        }
    }

    /**
     * 根据参数查询广告代码位列表（缓存每5分钟过期）
     * @param paramDto
     * @param isAbTest 是否AB测试；0：否，1：是
     * @param isHit 如果是AB测试，是否命中AB测试；0：否，1：是
     * @param abTestId AB测试id
     * @return
     */
    private List<AdCodeDto> getAdCodeListWithCache(AdvertCodeParamDto paramDto, int isAbTest, int isHit, String abTestId) {
        Map<String, Object> param = XyUtil.convertObjToMap(paramDto);
        param.put("isAbTest", isAbTest);
        param.put("isHit", isHit);
        param.put("abTestId", abTestId);
        String key = RedisConstant.ADV_CODES + param.toString();
        String value = redisService.get(key);

        if(RedisConstant.EMPTY_VALUE.equals(value)) {
            //防止缓存击穿
            return null;
        }

        if(StringUtils.isNotBlank(value)) {
            return JSONArray.parseArray(value, AdCodeDto.class);
        } else {
            List<AdCodeDto> advertCodes = adCodeMapper.adCodeInfoList(param);
            String cache = RedisConstant.EMPTY_VALUE;
            if(advertCodes != null && !advertCodes.isEmpty()) {
                cache = JSON.toJSONString(advertCodes);
            }
            redisService.set(key, cache, RedisConstant.DEFALUT_SECONDS);  //把查询结果存入缓存
            return advertCodes;
        }
    }



    private String getCustomFieldCache(AdvertPositionParamDto paramDto, int isAbTest, int isHit, String abTestId) {
        Map<String, Object> param = XyUtil.convertObjToMap(paramDto);
        param.put("isAbTest", isAbTest);
        param.put("isHit", isHit);
        param.put("abTestId", abTestId);
        String key = RedisConstant.ADV_POSITION_INFO + param.toString();
        String value = redisService.get(key);

        if(RedisConstant.EMPTY_VALUE.equals(value)) {
            //防止缓存击穿
            return null;
        }

        if(StringUtils.isNotBlank(value)) {
            return value;
        } else {
            String customField = adCodeMapper.findPositionRule(param);
            String cache = RedisConstant.EMPTY_VALUE;
            if(StringUtils.isNotEmpty(customField)) {
                cache = customField;
            }
            redisService.set(key, cache, RedisConstant.DEFALUT_SECONDS);  //把查询结果存入缓存
            return customField;
        }
    }

    /**
     * 是否AB测试，并且是否命中AB测试
     * @param param isAbTest(1:走AB测试，0：走默认)，isHit(1:命中AB测试，0:没命中AB测试)
     */
    private void isAbTestAndHit(Map<String, Object> param) {
        String abExps = (param.get("abExp") == null ? "" : param.get("abExp").toString()); //实验id
        param.put("isAbTest", 0);
        param.put("isHit", 0);

        if (StringUtils.isNotEmpty(abExps)){
            //获取实验参数
            List<AbTestAdvParamExpVo> advParamExpVos = new ArrayList<>();
            Arrays.stream(abExps.split(",")).forEach(abexp -> {
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
                return;
            }
            //命中实验：命中实验规则（一个广告位对应一个实验。如果一个广告位配置多个实验，那就是配置问题）
            List<AbTestRuleVo> abTestRuleVos = abTestRuleService.getABTextAdversByRule(param.get("positionKey").toString(), param.get("appKey").toString(), param.get("mobileType").toString());
            if (CollectionUtils.isNotEmpty(abTestRuleVos)) {
                abTestRuleVos.forEach(rule -> {
                    if(rule != null && rule.getAbId() != null){
                        String abFlowId = rule.getAbId();
                        for (AbTestAdvParamExpVo vo:advParamExpVos) {
                            if(StringUtils.isNotEmpty(vo.getExp_id()) && vo.getExp_id().equals(abFlowId)){
                                param.put("isAbTest", 1);  //走Abtest
                                param.put("abTestId", vo.getGroup_id());
                                param.put("openStatus",rule.getCustomSwitch() == null ? 0 : rule.getCustomSwitch()); //防止缓存失效
                                if(rule.getCustomSwitch() != null && rule.getCustomSwitch() == 1){
                                    param.put("isHit", 1);
                                }
                                return;
                            }
                        }
                    }
                });
            }
        }
    }
}
