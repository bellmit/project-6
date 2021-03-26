package com.miguan.ballvideo.service.impl;

import cn.jiguang.common.utils.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.enums.SectionAdType;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.VersionUtil;
import com.miguan.ballvideo.common.util.adv.AdvGlobal;
import com.miguan.ballvideo.common.util.adv.AdvUtils;
import com.miguan.ballvideo.entity.AdPositionConfig;
import com.miguan.ballvideo.entity.BannerPriceLadderVo;
import com.miguan.ballvideo.mapper.AdvertMapper;
import com.miguan.ballvideo.mapper.BannerPriceLadderMapper;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.vo.AdvertVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 广告ServiceImpl
 *
 * @author laiyudan
 * @date 2019-09-09
 **/

@Slf4j
@Service("AdvertService")
public class AdvertOldServiceImpl implements AdvertOldService {

    @Resource
    private AdvertMapper advertMapper;

    @Resource
    private VideoCacheService videoCacheService;

    @Resource
    private MarketAuditService marketAuditService;

    @Resource
    private BannerPriceLadderMapper bannerPriceLadderMapper;

    @Resource
    private RedisTemplate shangbaoRedisTemplate;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource(name="redisDB8Service")
    private RedisDB8Service redisDB8Service;

    /**
     * 查询广告
     *
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> queryAdertList(Map<String, Object> param) {
        //查询广告位
        if (!param.containsKey("appVersion")) {
            param.put("appVersion", VersionUtil.getVersion(""));
        }
        List<AdvertVo> list = this.getAdvertsBySection(param);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        //同一个广告位如果有多个配置，则每次调用随机返回一个广告位置
        Map<String, List<AdvertVo>> mapList = list.stream().collect(Collectors.groupingBy(AdvertVo::getPositionType));
        List<AdvertVo> resultList = new ArrayList<>();
        for (Map.Entry<String, List<AdvertVo>> map : mapList.entrySet()) {
            List<AdvertVo> oneTypeAdvert = map.getValue();
            if (oneTypeAdvert.get(0).getType() == 0) {
                List<AdvertVo> computer = AdvUtils.computer(oneTypeAdvert, 1);
                if (CollectionUtils.isNotEmpty(computer)) {
                    AdvertVo advertVo = computer.get(0);
                    //锁屏广告开关
                    if (Constant.LOCK_SCREEN_POSITION.equals(advertVo.getPositionType())) {
                        String key = "android_lock_screen_token";
                        if (Constant.IOS_MOBILE.equals(advertVo.getMobileType())) {
                            key = "ios_lock_screen_token";
                        }
                        int lockScreenToken = Global.getInt(key);
                        advertVo.setLockScreenToken(lockScreenToken);
                    }
                    resultList.add(advertVo);
                }
            } else {
                resultList.addAll(oneTypeAdvert);
            }

        }
        return resultList;
    }

    @Override
    public AdvertVo queryOneByRandom(Map<String, Object> param) {
        //版本2.2.0以上，市场审核开关开启，屏蔽广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return null;
        }
        //查询广告
        //param.put("queryNumber", 1);//随机1条
        if (!param.containsKey("appVersion")) {
            param.put("appVersion", VersionUtil.getVersion(""));
        }
        List<AdvertVo> advertVos = videoCacheService.getAdvertList(param, 1);
        if (CollectionUtils.isNotEmpty(advertVos)) {
            return advertVos.get(0);
        }
        return null;
    }

    @Override
    public List<AdvertVo> queryByRandom(Map<String, Object> param, int nums) {
        //版本2.2.0以上，市场审核开关开启，屏蔽广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return null;
        }
        //查询广告
        //param.put("queryNumber", nums);//随机nums条
        if (!param.containsKey("appVersion")) {
            param.put("appVersion", VersionUtil.getVersion(""));
        }
        List<AdvertVo> advertVos = videoCacheService.getAdvertList(param, nums);
        if (CollectionUtils.isNotEmpty(advertVos)) {
            return advertVos;
        }
        return null;
    }

    @Override
    public List<AdvertVo> queryAdertListGame(Map<String, Object> param) {
        //查询广告位
        if (!param.containsKey("appVersion")) {
            param.put("appVersion", VersionUtil.getVersion(""));
        }
        List<AdvertVo> list = getFinalAdertListGame(param);
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        //同一个广告位如果有多个配置，则每次调用随机返回一个广告位置
        Map<String, List<AdvertVo>> mapList = list.stream().collect(Collectors.groupingBy(AdvertVo::getPositionType));
        List<AdvertVo> resultList = new ArrayList<>();
        for (Map.Entry<String, List<AdvertVo>> map : mapList.entrySet()) {
            List<AdvertVo> oneTypeAdvert = map.getValue();
            List<AdvertVo> computer = AdvUtils.computer(oneTypeAdvert, 1);
            if (CollectionUtils.isNotEmpty(computer)) {
                AdvertVo advertVo = computer.get(0);
                //锁屏广告开关
                if (Constant.LOCK_SCREEN_POSITION.equals(advertVo.getPositionType())) {
                    String key = "android_lock_screen_token";
                    if (Constant.IOS_MOBILE.equals(advertVo.getMobileType())) {
                        key = "ios_lock_screen_token";
                    }
                    int lockScreenToken = Global.getInt(key);
                    advertVo.setLockScreenToken(lockScreenToken);
                }
                resultList.add(advertVo);
            } else {
                resultList.addAll(oneTypeAdvert);
            }

        }
        return resultList;
    }

    @Override
    public List<AdvertVo> queryVideoByRandom(Map<String, Object> param, int nums) {
        //版本2.2.0以上，市场审核开关开启，屏蔽广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return null;
        }
        if (!param.containsKey("appVersion")) {
            param.put("appVersion", VersionUtil.getVersion(""));
        }
        List<AdvertVo> advers = advertMapper.queryAdertList(param);
        advers = getAdvertsByChannel(advers, param);
        return AdvUtils.computer(advers, nums);
    }

    @Override
    public Map<String, Object> lockScreenAdertList(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        String androidLockScreenToken = Global.getValue("android_lock_screen_token");
        result.put("androidLockScreenToken", androidLockScreenToken);
        //版本2.2.0以上，市场审核开关开启，屏蔽所有广告
        boolean isShield = marketAuditService.isShield(param);
        if (isShield) {
            return result;
        }
        //锁屏是开的时候才去查广告信息
        if ("10".equals(androidLockScreenToken)) {
            param.put("lockScreenPositionTypes", "1");//随便赋值个标识
            List<AdvertVo> advertVoList = advertMapper.queryAdertList(param);
            if (CollectionUtils.isEmpty(advertVoList)) {
                advertVoList = getAdvertsByChannel(advertVoList, param);
            }
            if (CollectionUtils.isNotEmpty(advertVoList)) {
                List<String> list = advertVoList.stream().map(AdvertVo::getPositionType).collect(toList());
                int i = new Random().nextInt(list.size());
                result.put("positionType", list.get(i));
            }
        }
        return result;
    }

    @Override
    public Map<String, List<AdvertVo>> getAdversByPositionTypes(Map<String, Object> param) {
        //版本2.2.0以上，市场审核开关开启，屏蔽所有广告
        boolean isShieldAdv = marketAuditService.isShield(param);
        if (isShieldAdv) {
            return null;
        }
        List<String> positionTypes = (List<String>) param.get("positionTypes");
        List<AdvertVo> advertVos = new ArrayList<>();
        for (String positionType : positionTypes) {
            param.put("positionType", positionType);
            List<AdvertVo> list = this.getAdvertsBySection(param);
            if (CollectionUtils.isNotEmpty(list)) {
                int type = list.get(0).getType();
                if (type == 0){
                    list = AdvUtils.computer(list, 1);
                }
                advertVos.addAll(list);
            }
        }

        if (CollectionUtils.isNotEmpty(advertVos)) {
            Map<String, List<AdvertVo>> groupBy = advertVos.stream().collect(Collectors.groupingBy(AdvertVo::getPositionType));
            return groupBy;
        }
        return null;
    }

    /**
     * 查询广告结果为空，安卓根据开关是否走默认渠道再查询广告
     *
     * @param param 参数
     * @return
     */
    public boolean checkDefaultChannel(Map<String, Object> param) {
        if (Constant.ANDROID.equals(param.get("mobileType"))) {
            String channelSwitchCode = "adv_channel_switch_xysp";
            String defaultChannelCode = "adv_default_channel_xysp";
            if (Constant.GGSPPACKAGE.equals(param.get("appPackage"))) {
                channelSwitchCode = "adv_channel_switch_ggsp";
                defaultChannelCode = "adv_default_channel_ggsp";
            } else if (Constant.MTSPPACKAGE.equals(param.get("appPackage"))) {
                channelSwitchCode = "adv_channel_switch_mtsp";
                defaultChannelCode = "adv_default_channel_mtsp";
            }
            String channelSwitch = Global.getValue(channelSwitchCode);
            if (Constant.OPENSTR.equals(channelSwitch)) {
                param.put("channelId", Global.getValue(defaultChannelCode));
                param.put("state", 1);
                return true;
            }
        }
        return false;
    }

    /**
     *  根据渠道查询广告
     *  1 如果为空，查询默认渠道广告；
     *  2 如果多个广告位置，循环1的判断逻辑，返回合集；
     * @param list
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> getAdvertsByChannel(List<AdvertVo> list, Map<String, Object> param) {
        //广告为空走默认渠道广告
        if(CollectionUtils.isEmpty(list) && checkDefaultChannel(param)){
            return advertMapper.queryAdertList(param);
        }
        //查询单个广告位置
        if (param.containsKey("positionType") && param.get("positionType") != null) {
            Map<Integer, List<AdvertVo>> advertVoMap = list.stream().collect(Collectors.groupingBy(AdvertVo::getState));
            return advertVoMap.get(1);
        }
        //查询多个广告位置
        if (param.containsKey("positionTypes") && param.get("positionTypes") != null) {
            List<String> positionList = (List<String>) param.get("positionTypes");
            param.remove("positionTypes");
            return getResultList(list, param,positionList);
        }
        return null;
    }

    /**
     * 该渠道未设置游戏广告，则走默认渠道
     *
     * @param param
     * @return
     */
    public List<AdvertVo> getFinalAdertListGame(Map<String, Object> param) {
        List<AdvertVo> resultList = new ArrayList<>();
        //版本2.2.0以上，市场审核开关开启，屏蔽所有广告
        boolean isShieldAdv = marketAuditService.isShield(param);
        if (isShieldAdv) {
            return null;
        }
        //查询全部游戏广告位置，每个广告位置无数据则查询默认渠道广告，如果存在启用则添加，存在广告全部为禁用则返回空
        List<String> positionList = advertMapper.queryPositionTypeGame(param.get("mobileType").toString());
        param.put("game", "game");
        List<AdvertVo> list = advertMapper.queryAdertList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Map<Integer, List<AdvertVo>>> collect = list.stream().collect(Collectors.groupingBy(AdvertVo::getPositionType, Collectors.groupingBy(AdvertVo::getState)));
            for (String position : positionList) {
                if (collect.containsKey(position)) {
                    Map<Integer, List<AdvertVo>> advertMap = collect.get(position);
                    if (advertMap.containsKey(1)) {
                        resultList.addAll(advertMap.get(1));
                    }
                } else {
                    if (checkDefaultChannel(param)) {
                        param.put("positionType", position);
                        List<AdvertVo> advertVoList = advertMapper.queryAdertList(param);
                        if (CollectionUtils.isNotEmpty(advertVoList)) {
                            resultList.addAll(advertVoList);
                        }
                    }
                }
            }
        } else {
            if (checkDefaultChannel(param)) {
                resultList = advertMapper.queryAdertList(param);
            }
        }
        return resultList;
    }

    /**
     * 多个广告位置的处理：
     * 1 每个广告位置无数据则查询默认渠道广告；
     * 2 存在广告全部为禁用则返回空；
     *
     * @param list
     * @param param
     * @param positionList
     */
    private  List<AdvertVo> getResultList(List<AdvertVo> list, Map<String, Object> param, List<String> positionList) {
        List<AdvertVo> resultList = Lists.newArrayList();
        Map<String, Map<Integer, List<AdvertVo>>> collect = list.stream().collect(Collectors.groupingBy(AdvertVo::getPositionType, Collectors.groupingBy(AdvertVo::getState)));
        for (String position : positionList) {
            if (collect.containsKey(position)) {
                Map<Integer, List<AdvertVo>> advertMap = collect.get(position);
                if (advertMap.containsKey(1)) {
                    resultList.addAll(advertMap.get(1));
                }
            } else {
                if (checkDefaultChannel(param)) {
                    param.put("positionType", position);
                    List<AdvertVo> advertVoList = advertMapper.queryAdertList(param);
                    if (CollectionUtils.isNotEmpty(advertVoList)) {
                        resultList.addAll(advertVoList);
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 获取基本广告
     *
     * @param param
     * @return
     */
    public List<AdvertVo> getBaseAdverts(Map<String, Object> param) {
        List<AdvertVo> advertVos = advertMapper.queryAdertList(param);
        return this.getAdvertsByChannel(advertVos, param);
    }

    /**
     * 执行阶梯分层算法
     *
     * @param param
     * @return
     */
    private List<AdvertVo> doStageAlgorithm(Map<String, Object> param, Long advLadderId, Long bannerPositionId) {
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("id", advLadderId);
        paraMap.put("bannerPositionId", bannerPositionId);
        paraMap.put("appPackage", MapUtils.getString(param, "appPackage"));
        List<AdvertVo> advertVos = bannerPriceLadderMapper.queryLadderAdertList(paraMap);
        if (CollectionUtils.isEmpty(advertVos)) {
            return advertVos;
        }
        String retainType = getRetainType(param);
        String keyByCode = SectionAdType.getGroupKeyByCode(retainType);
        if (StringUtils.isNotEmpty(keyByCode)) {
            advertVos = advertVos.stream().filter(s -> keyByCode.equals(s.getGroupKey())).collect(Collectors.toList());
        }
        return advertVos;
    }

    /**
     * 如果阶梯分层算法关闭，走填充互补+基本逻辑+切片过滤
     *
     * @param param
     * @return
     */
    private List<AdvertVo> doBaseAlgorithm(Map<String, Object> param) {
        List<AdvertVo> advertVos = this.doFillAlgorithm(param);
        if (CollectionUtils.isEmpty(advertVos) || (CollectionUtils.isNotEmpty(advertVos)
                && advertVos.get(0).getFillState() == 1)) {
            return advertVos;
        }
        String retainType = getRetainType(param);
        List<AdvertVo> temp = new ArrayList<>();
        List<String> list = SectionAdType.getTypeListByCode(retainType);
        for (int i = 0; i < advertVos.size(); i++) {
            AdvertVo advertVo = advertVos.get(i);
            String adCode = advertVo.getAdCode();//2、10、21广点通3-9、13-19穿山甲
            String adType = advertVo.getAdType();//1、自定义 2、SDK
            if (list.contains("-1") && "1".equals(adType)) {
                continue;
            }
            if (!list.contains(adCode)) {
                advertVo.setType(3);
                temp.add(advertVo);
            }
        }
        Collections.shuffle(temp);
        return temp;
    }

    /**
     * 互补广告查询
     * 1 如果互补开启，则走互补逻辑，否则走基本逻辑
     *
     * @param param
     * @return
     */
    private List<AdvertVo> doFillAlgorithm(Map<String, Object> param) {
        List<AdvertVo> advertVos = getBaseAdverts(param);
        if (CollectionUtils.isEmpty(advertVos)) {
            return advertVos;
        }
        int fillState = advertVos.get(0).getFillState();//填充互补率：1启用  0禁用
        if (fillState == 1) {
            //填充互补广告,每个广告位置，配置多个广告商广告，每个广告商广告各返回一个
            advertVos = this.getFillAdertList(advertVos);
        }
        return advertVos;
    }

    /**
     * 阶梯广告查询V2.3.0
     * 1 如果阶梯广告开关关闭，填充互补开关开启（首页列表、小视频列表、小视频详情页广告位），返回填充互补广告;
     * 2 如果阶梯广告、填充互补都关闭，返回原广告逻辑；
     *
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> getAdvertsByLadder(Map<String, Object> param) {
        String positionType = MapUtils.getString(param, "positionType");
        String mobileType = MapUtils.getString(param, "mobileType");
        String appPackage = MapUtils.getString(param, "appPackage");
        String appVersion = MapUtils.getString(param, "appVersion");
        Map<String, AdPositionConfig> adPositionConfigMap = AdvGlobal.positionConfigMap;
        if (adPositionConfigMap != null) {
            AdPositionConfig adPositionConfig = adPositionConfigMap.get(positionType + "_" + mobileType+"_"+appPackage);
            if (adPositionConfig != null) {
                param.put("csjRate", adPositionConfig.getCsjRate());
                param.put("gdtRate", adPositionConfig.getGdtRate());
                param.put("jsbRate", adPositionConfig.getJsbRate());
                param.put("adPositionId", adPositionConfig.getAdPositionId());
                BannerPriceLadderVo isOpen = AdvUtils.ladderAdvIsOpen(positionType, mobileType, appVersion, appPackage);
                if (isOpen != null) {
                    //阶梯广告开启
                    List<AdvertVo> advertVos = doStageAlgorithm(param, isOpen.getId(), isOpen.getBannerPositionId());
                    if (CollectionUtils.isNotEmpty(advertVos)) {
                        return advertVos;
                    }
                }
                //阶梯广告没有数据，走基本逻辑+切片过滤
                return doBaseAlgorithm(param);
            }
        }
        //阶梯广告没有数据，走互补逻辑
        return doFillAlgorithm(param);
    }

    private String getRetainType(Map<String, Object> param) {
        List<String> list = new ArrayList<>();
        String adPositionId = MapUtils.getString(param, "adPositionId");
        String appVersion = MapUtils.getString(param, "appVersion");
        String appPackage = MapUtils.getString(param, "appPackage");
        boolean high = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_240);//判断是否大于2.4版本
        Map<String, String> map = SectionAdType.getCodeAndRateKey();
        for (String code : map.keySet()) {
            String key;
            if (high){
                key = RedisKeyConstant.KEY_AD_RATE + RedisKeyConstant.KEY_AD_HIGH + appPackage + code + adPositionId;
            }else {
                key = RedisKeyConstant.KEY_AD_RATE + RedisKeyConstant.KEY_AD_LOW + appPackage + code + adPositionId;
            }
            Object n = shangbaoRedisTemplate.opsForValue().get(key);
            String m = "";
            if (n != null) {
                m = n.toString();
            }
            m = StringUtils.isEmpty(m) ? MapUtils.getString(param, map.get(code)) : m;
            Double v = Double.valueOf(m) * 100;
            int a = v.intValue();
            for (int i = 0; i < a; i++) {
                list.add(code);
            }
        }
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        Random random = new Random();
        int i = random.nextInt(list.size());
        return list.get(i);
    }

    /**
     * 填充互补广告,每个广告位置，配置多个广告商广告，每个广告商广告各返回一个
     *
     * @param list
     * @return
     */
    private List<AdvertVo> getFillAdertList(List<AdvertVo> list) {
        //每个广告位置，配置多个广告商广告，每个广告商广告各返回一个
        for (AdvertVo advertVo : list) {
            if (advertVo.getAdCode() == null) {
                advertVo.setAdCode("0");//自定义广告
            }
        }
        Map<String, List<AdvertVo>> collect = list.stream().collect(Collectors.groupingBy(AdvertVo::getAdCode));
        List<AdvertVo> resultList = new ArrayList<>();
        for (Map.Entry<String, List<AdvertVo>> map : collect.entrySet()) {
            String key = map.getKey();
            if("0".equals(key)){
                //自定义广告有2种，各返回1种
                List<AdvertVo> oneList = AdvUtils.splitByLinkType(map.getValue());
                resultList.addAll(oneList);
            }else{
                List<AdvertVo> oneList = AdvUtils.computer(map.getValue(), 1);
                resultList.addAll(oneList);
            }

        }
        Collections.shuffle(resultList);
        return resultList;
    }

    /**
     * 根据切片配置信息
     *
     * @param param
     * @return
     */
    @Override
    public List<AdvertVo> getAdvertsBySection(Map<String, Object> param) {
        String key = AdvUtils.filter(param);
        String json = redisDB8Service.get(key);
        if(RedisKeyConstant.EMPTY_VALUE.equals(json)){
            return null;
        }
        List<AdvertVo> datas  = filterEmpty(param);
        if(CollectionUtils.isEmpty(datas)){
            redisDB8Service.set(key,RedisKeyConstant.EMPTY_VALUE,RedisKeyConstant.EMPTY_VALUE_SECONDS);
            return null;
        }else{
            return datas;
        }
    }

    private List<AdvertVo> filterEmpty(Map<String, Object> param) {
        //魔方后台-广告总开关:true禁用，false非禁用
        if (toolMofangService.stoppedByMofang(param)) {
            return null;
        }

        String appVersion = MapUtils.getString(param, "appVersion");
        //2.2版本之前还是走原来广告逻辑
        boolean high = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_220);
        if (!high) {
            List<AdvertVo> baseAdv = getBaseAdverts(param);
            return baseAdv;
        }
        List<AdvertVo> advertVos = this.getAdvertsByLadder(param);
        return advertVos;
    }


    /**
     * 根据概率给banner广告排序
     *
     * @param param
     * @return
     */
    public List<AdvertVo> bannerInfo(Map<String, Object> param){
        List<AdvertVo> list = this.getAdvertsBySection(param);
        if (CollectionUtils.isNotEmpty(list)){
            int size = list.size();
            list = AdvUtils.computer(list, size);
        }
        return list;
    }
}