package com.miguan.flow.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.api.R;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Maps;
import com.miguan.flow.common.constant.RedisConstant;
import com.miguan.flow.common.util.FlowUtil;
import com.miguan.flow.common.util.adv.AdvUtils;
import com.miguan.flow.dto.AdvertCodeDto;
import com.miguan.flow.dto.AdvertCodeParamDto;
import com.miguan.flow.dto.common.AbTestAdvParamsDto;
import com.miguan.flow.mapper.AbTestRuleMapper;
import com.miguan.flow.mapper.AdvCodeMapper;
import com.miguan.flow.service.AbTestRuleService;
import com.miguan.flow.service.AdvCodeService;
import com.miguan.flow.service.MofangService;
import com.miguan.flow.service.common.RedisService;
import com.miguan.flow.vo.AbTestAdvParamExpVo;
import com.miguan.flow.vo.AbTestAdvParamVo;
import com.miguan.flow.vo.AbTestRuleVo;
import com.miguan.flow.vo.AdProfitVo;
import com.miguan.flow.vo.common.PublicInfo;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.miguan.flow.service.common.RedisAdvertService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 广告代码位serviceImpl
 **/
@Service
public class AdvCodeServiceImpl implements AdvCodeService{

    @Resource
    private MofangService mofangService;
    @Resource
    private AbTestRuleService abTestRuleService;
    @Resource
    private AbTestRuleMapper abTestRuleMapper;
    @Resource
    private AdvCodeMapper advCodeMapper;
    @Resource
    private RedisService redisService;

    @Resource
    private RedisAdvertService redisAdvertService;


    /**
     * 获取广告代码位列表
     * @param queueVo AB测试vo
     * @param publicInfo 请求头参数
     * @param paramDto 接口参数
     * @return
     */
    public List<AdvertCodeDto> advCodeInfoList(AbTestAdvParamsDto queueVo, PublicInfo publicInfo, AdvertCodeParamDto paramDto) {
        Map<String, Object> param = FlowUtil.convertObjToMap(paramDto);
        //魔方后台-广告总开关:true禁用，false非禁用
        if (mofangService.stoppedByMofang(param)) {
            return null;
        }
        if(queueVo != null) {
            param.putAll(FlowUtil.convertObjToMap(queueVo));
        }

        //是否AB测试，并且是否命中AB测试
        isAbTestAndHit(param);

        int isAbTest = (int)param.get("isAbTest");
        int isHit = (int)param.get("isHit");

        //查询广告代码位列表
        String abTestId = (param.get("abTestId") == null ? null : (String.valueOf(param.get("abTestId"))));
        List<AdvertCodeDto> advertCodes = getAdvCodeListWithCache(paramDto, isAbTest, isHit, abTestId);
        if(CollectionUtils.isEmpty(advertCodes)) {
            return null;
        }

        //根据类型和算法排序
        if (advertCodes.get(0).getComputer() == 1) {
            //手动配比
            return AdvUtils.computerAndSort(advertCodes);
        } else if (advertCodes.get(0).getComputer() == 2) {
            //手动排序
            return AdvUtils.sort(advertCodes);
        } else if (advertCodes.get(0).getComputer() == 3) {
            //自动排序
            return AdvUtils.sort(advertCodes);
        } else if(advertCodes.get(0).getComputer() == 4) {

            int ladderDelayMillis =  redisAdvertService.getInt(RedisConstant.AD_LADDER_DELAY_MILLIS); //阶梯广告延迟
            int commonDelayMillis =  redisAdvertService.getInt(RedisConstant.AD_COMMON_DELAY_MILLIS); //通跑广告延迟

            int adLadderDelayMillis = advertCodes.get(0).getLadderDelayMillis(); //广告位或者ab配置的时间
            int adCommonDelayMillis = advertCodes.get(0).getCommonDelayMillis(); //广告位或者ab配置的时间
            if (isAbTest == 1 && isHit == 1) {
                ladderDelayMillis = adLadderDelayMillis;
                commonDelayMillis = adCommonDelayMillis;
            } else {
                if (adLadderDelayMillis > 0) {
                    ladderDelayMillis = adLadderDelayMillis;
                }
                if (adCommonDelayMillis > 0) {
                    commonDelayMillis = adCommonDelayMillis;
                }
            }

            //自动排序(多维度)
            Map<String, Object> sortParam = new HashMap<>();
            sortParam.put("isNew", publicInfo.isNewApp() ? 1 : 0);  //是否新用户，1：新用户，0：老用户
            sortParam.put("city", StringUtils.isBlank(publicInfo.getGpscity()) ? "-1" : publicInfo.getGpscity());  //城市
            sortParam.put("channel", StringUtils.isBlank(publicInfo.getChannel()) ? "-1" : publicInfo.getChannel()); //渠道id
            sortParam.put("appPackage", paramDto.getAppPackage()); //包名
            sortAutoMulti(advertCodes, sortParam);
            this.setAdvertCodeDelayMillis(advertCodes, ladderDelayMillis, commonDelayMillis);
        }
        return advertCodes;
    }


    /**
     * 设置延迟时间
     *
     * @param list
     */
    private void setAdvertCodeDelayMillis(List<AdvertCodeDto> list,  int ladderDelayMillis, int commonDelayMillis) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        int delayMillis = 0;
        int preLadderPrice = 0; //前一个阶梯广告的阶梯价
        int commonDelayDiff = 0; //通跑广告第一次延迟
        for (AdvertCodeDto advertCodeDto : list) {
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
                adMap = this.getAdMap(adMap, date, String.valueOf(params.get("appPackage")));
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
    private void sortAutoMulti(List<AdvertCodeDto> advertVos, Map<String, Object> params) {
        List<AdvertCodeDto> ladderAds = new ArrayList<>();  //阶梯价代码位列表(阶梯价格为0的，就表示是阶梯价代码位)
        List<AdvertCodeDto> notLadderAds = new ArrayList<>();  //非阶梯价代码位列表（通跑层代码位）
        for(AdvertCodeDto advertCodeDto : advertVos) {
            if(advertCodeDto.getLadderPrice() > 0) {
                ladderAds.add(advertCodeDto);  //阶梯价代码位
            } else {
                notLadderAds.add(advertCodeDto);  //非阶梯价代码位
            }
        }
        //阶梯价代码位根据阶梯价格降序排
        ladderAds = ladderAds.stream().sorted(Comparator.comparing(AdvertCodeDto::getLadderPrice).reversed()).collect(Collectors.toList());

        //非阶梯价代码位排序(通跑层)
        if(!notLadderAds.isEmpty()) {
            List<String> adIds = notLadderAds.stream().map(AdvertCodeDto::getAdId).collect(Collectors.toList());
            Map<String, Double> adEcpm = this.countMultiEcpm(adIds, params);  //统计出通跑层代码位ecpm
            notLadderAds.forEach(r->{
                r.setEcpm(adEcpm.get(r.getAdId()));
            });
            //ecpm从高到低排序，相同ecpm之间按照配置顺序排序
            notLadderAds = notLadderAds.stream().sorted(Comparator.comparing(AdvertCodeDto::getEcpm, Comparator.reverseOrder()).thenComparing(AdvertCodeDto::getOptionValue, Comparator.reverseOrder())).collect(Collectors.toList());
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
        String value = redisService.get(RedisConstant.SORT_MULTI_DATE);
        if(StringUtils.isNotBlank(value)) {
            return value;
        } else {
            Integer platNum = advCodeMapper.countUsedPlat();  //查询当前在使用的第三方广告商数
            String date = advCodeMapper.queryNearDate(platNum);
            redisService.set(RedisConstant.SORT_MULTI_DATE, date, RedisConstant.DEFALUT_SECONDS);
            return date;
        }
    }

    /**
     * 获取98和第三方代码位最新一天的代码位收益
     * @return
     */
    private Map<String, Double> getAdProfit(String date) {
        String value = redisService.get(RedisConstant.PROFIT_AD); //从缓存获取代码位收益
        if(StringUtils.isNotBlank(value)) {
            return JSONObject.parseObject(value, Map.class);
        } else {
            Map<String, Double> profit98Map = new HashMap<>();
            //查询最近一天第三方代码位收益
            List<AdProfitVo> profitThirdList = advCodeMapper.queryAdProfit(date);
            //查询最近一天98代码位收益
            List<AdProfitVo> profit98List = advCodeMapper.queryAd98Profit(date);
            //合并第三方和98代码位收益
            if(profitThirdList != null) {
                profit98Map.putAll(profitThirdList.stream().collect(Collectors.toMap(AdProfitVo::getAdId, AdProfitVo::getProfit)));
            }
            if(profit98List != null) {
                profit98Map.putAll(profit98List.stream().collect(Collectors.toMap(AdProfitVo::getAdId, AdProfitVo::getProfit)));
            }
            redisService.set(RedisConstant.PROFIT_AD, JSON.toJSONString(profit98Map), RedisConstant.DEFALUT_SECONDS);
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
            List<AdProfitVo> list = advCodeMapper.queryAdMultiData(params);
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
            List<AdProfitVo> list = advCodeMapper.queryAdMultiData(params);
            redisService.set(key, JSON.toJSONString(list), RedisConstant.DEFALUT_SECONDS); //把数据存入缓存
            return list;
        }
    }

    private Map<String, AdProfitVo> getAdMap(Map<String, AdProfitVo> adMap, String date, String appPackage) {
        if(adMap == null) {
            List<AdProfitVo> list = queryAdData(date, appPackage);
            adMap = list.stream().collect(Collectors.toMap(AdProfitVo::getAdId, r->r));
            return adMap;
        }
        return adMap;
    }

    /**
     * 查询最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
     * @param date 日期
     * @param appPackage 包名
     * @return
     */
    private List<AdProfitVo> queryAdData(String date, String appPackage) {
        String key = RedisConstant.AD_DATA + date + appPackage;
        String value =redisService.get(key);
        if(StringUtils.isNotBlank(value)) {
            //从缓存获取
            return JSONArray.parseArray(value, AdProfitVo.class);
        } else {
            List<AdProfitVo> list = advCodeMapper.queryAdData(date, appPackage);
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
    private List<AdvertCodeDto> getAdvCodeListWithCache(AdvertCodeParamDto paramDto, int isAbTest, int isHit, String abTestId) {
        Map<String, Object> param = FlowUtil.convertObjToMap(paramDto);
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
            return JSONArray.parseArray(value, AdvertCodeDto.class);
        } else {
            List<AdvertCodeDto> advertCodes = advCodeMapper.advCodeInfoList(param);
            String cache = RedisConstant.EMPTY_VALUE;
            if(advertCodes != null && !advertCodes.isEmpty()) {

                cache = JSON.toJSONString(advertCodes);
            }
            redisService.set(key, cache, RedisConstant.DEFALUT_SECONDS);  //把查询结果存入缓存
            return advertCodes;
        }
    }

    /**
     * 是否AB测试，并且是否命中AB测试
     * @param param isAbTest(1:走AB测试，0：走默认)，isHit(1:命中AB测试，0:没命中AB测试)
     */
    private void isAbTestAndHit(Map<String, Object> param) {
        String abExps = (param.get("abExp") == null ? "" : param.get("abExp").toString()); //实验id
        String abTestIds = param.get("abTestId") == null ? "" : param.get("abTestId").toString();  //实验分组Id
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
            List<AbTestRuleVo> abTestRuleVos = abTestRuleService.getABTextAdversByRule(param.get("positionType").toString(), param.get("appPackage").toString(), param.get("mobileType").toString());
            if (CollectionUtils.isNotEmpty(abTestRuleVos)) {
                abTestRuleVos.forEach(rule -> {
                    if(rule != null && rule.getAbFlowId() != null){
                        String abFlowId = rule.getAbFlowId();
                        for (AbTestAdvParamExpVo vo:advParamExpVos) {
                            if(StringUtils.isNotEmpty(vo.getExp_id()) && vo.getExp_id().equals(abFlowId)){
                                param.put("isAbTest", 1);  //走Abtest
                                param.put("abTestId", vo.getGroup_id());
                                param.put("openStatus",rule.getOpenStatus() == null ? 0 : rule.getOpenStatus()); //防止缓存失效
                                return;
                            }
                        }
                    }
                });
            }
        } else if (StringUtils.isNotEmpty(abTestIds)){
            List<AbTestAdvParamVo> advParamVos = JSON.parseArray(abTestIds, AbTestAdvParamVo.class);
            Map<String, Object> paramPosition = new HashMap<>();
            paramPosition.put("positionType", param.get("positionType"));
            paramPosition.put("mobileType", param.get("mobileType"));
            paramPosition.put("appPackage", param.get("appPackage"));
            List<AdvertCodeDto> advert = this.positionInfoList(param.get("positionType").toString(), param.get("appPackage").toString(), param.get("mobileType").toString());
            if (CollectionUtils.isNotEmpty(advert)) {
                Long positionId = advert.get(0).getPositionId();
                if (positionId > 0) {
                    String keyStr = "ad_exp_" + positionId + "_";
                    for (AbTestAdvParamVo advParamVo : advParamVos) {
                        if (StringUtils.isNotEmpty(advParamVo.getExp_key()) && advParamVo.getExp_key().contains(keyStr)) {
                            param.put("isAbTest", 1);  //走Abtest
                            param.put("abTestId", advParamVo.getGroup_id());
                            break;
                        }
                    }
                }
            }

            int isAbtest = (param.get("isAbTest") == null ? 0 : Integer.parseInt(param.get("isAbTest").toString()));
            //命中广告位  尝试获取实验下的开关状态
            if(isAbtest == 1){
                Integer openStatus = abTestRuleMapper.queryOpenStatusByAbTestId(Integer.parseInt(param.get("abTestId").toString()));
                openStatus = (openStatus == null ? 0 : openStatus);
                param.put("openStatus", openStatus); //防止缓存失效
            }
        }
        //命中实验，且开启状态为1
        int isAbtest = (param.get("isAbTest") == null ? 0 : Integer.parseInt(param.get("isAbTest").toString()));
        if(isAbtest == 1){
            if(param.get("openStatus") != null && StringUtils.isNotEmpty(param.get("openStatus").toString()) && Integer.valueOf(param.get("openStatus").toString()) == 1){
                param.put("isHit", 1);
            } else {
                param.put("isHit", 0);
            }
        }
    }

    /**
     * 获取广告位信息(每5分钟刷新一次缓存)
     * @param positionType 广告位key
     * @param appPackage  包名
     * @param mobileType 手机类型1*应用端:1-ios，2-安卓
     * @return
     */
    public List<AdvertCodeDto> positionInfoList(String positionType, String appPackage, String mobileType) {
        Map<String, Object> params = new HashMap<>();
        params.put("positionType", positionType);
        params.put("appPackage", appPackage);
        params.put("mobileType", mobileType);

        String key = RedisConstant.AD_POSITION + params.toString();
        String value = redisService.get(key);
        if(RedisConstant.EMPTY_VALUE.equals(value)) {
            //防止缓存击穿
            return null;
        }
        if(StringUtils.isNotBlank(value)) {
            return JSONArray.parseArray(value, AdvertCodeDto.class);
        } else {
            List<AdvertCodeDto> advertCodeDtos = advCodeMapper.positionInfoList(params);
            String cache = RedisConstant.EMPTY_VALUE;
            if(advertCodeDtos != null && !advertCodeDtos.isEmpty()) {
                cache = JSON.toJSONString(advertCodeDtos);
            }
            redisService.set(key, cache, RedisConstant.DEFALUT_SECONDS);  //把查询结果存入缓存
            return advertCodeDtos;
        }

    }
}
