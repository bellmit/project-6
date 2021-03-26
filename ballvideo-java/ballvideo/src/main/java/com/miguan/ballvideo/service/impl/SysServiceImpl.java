package com.miguan.ballvideo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.util.*;
import com.miguan.ballvideo.common.util.adv.AdvFieldType;
import com.miguan.ballvideo.dto.PopConfDetailDto;
import com.miguan.ballvideo.dto.PopConfDto;
import com.miguan.ballvideo.dto.WifiPopConfDto;
import com.miguan.ballvideo.dynamicquery.Dynamic2Query;
import com.miguan.ballvideo.dynamicquery.Dynamic5Query;
import com.miguan.ballvideo.dynamicquery.DynamicQuery;
import com.miguan.ballvideo.entity.*;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.*;
import com.miguan.ballvideo.service.recommend.IPService;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.mongodb.VideoHotspotVo;
import com.miguan.ballvideo.vo.video.FirstVideoParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author shixh
 * @Date 2020/3/23
 **/
@Slf4j
@Service
public class SysServiceImpl implements SysService {

    @Resource
    private RedisService redisService;

    @Resource(name="redisDB8Service")
    private RedisDB8Service redisDB8Service;

    @Resource
    private ThirdDataService thirdDataService;

    @Resource
    private DynamicQuery dynamicQuery;

    @Resource
    private FirstVideosService firstVideosService;

    @Resource
    private Dynamic2Query dynamic2Query;

    @Resource
    private ToolMofangService toolMofangService;

    @Resource
    private Dynamic5Query dynamic5Query;

    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Resource
    private VideoEsService videoEsService;

    @Resource
    private PredictService predictService;

    @Resource
    private IPService ipService;
    @Resource
    private ClDeviceService clDeviceService;

    private final static String code = "code";
    private final static int success = 200;

    @Override
    public void delRedis(String redisKey) {
        if (StringUtils.isBlank(redisKey) || redisKey.contains("*")){
            log.info("参数不存在或者参数带*，删除失败");
            return;
        }
          //删除视频相关缓存
          if (RedisKeyConstant.NEWFIRSTVIDEO161_KEY.contains(redisKey)) {
               //PHP后端操作会导致缓存雪崩，先注释掉 add shixh 0518
                /*Set<String> keys1 = redisDB8Service.keys(RedisKeyConstant.NEWFIRSTVIDEO161_KEY + "*");
                for (String key : keys1) {
                    redisDB8Service.del(key);
                }
                Set<String> keys2 = redisDB8Service.keys(RedisKeyConstant.NEWFIRSTVIDEO_KEY + "*");
                for (String key : keys2) {
                    redisDB8Service.del(key);
                }
                */
                Set<String> keys3 = redisService.keys(RedisKeyConstant.REQUEST_CACHE_KEY + "com.miguan.ballvideo.controller.VideoGatherEsController*");
                for (String key : keys3) {
                    redisService.del(key);
                }
            }else{
                //根据key模糊查询删除
                Set<String> set = redisService.keys("*"+redisKey + "*");
                for (String key : set) {
                    redisService.del(key);
                }
            }
    }

    @Override
    public void updateAdConfigCache() {
        List<AdPositionConfig> adPositionConfigList = thirdDataService.getAdPositionConfigList();
        Map<String, AdPositionConfig> appleMap = adPositionConfigList.stream().collect(Collectors.toMap(AdPositionConfig::getKeywordMobileType, a -> a,(k1, k2) -> k1));
        CacheUtil.initAdPositionConfigs(appleMap);
    }

    @Override
    public void updateAdLadderCache() {
        String sql = "select id,banner_position_id,version_start,version_end,keyword_mobileType,app_package from banner_price_ladder where state = 1 ";
        List<BannerPriceLadderVo> bannerPriceLadders = dynamicQuery.nativeQueryList(BannerPriceLadderVo.class,sql);
        CacheUtil.initBannerPriceLadders(bannerPriceLadders);
    }

    /**
     * 首页弹窗接口，返回值为空则不需要弹
     * @param popTime 之前出现弹窗的时间戳
     * @param popPosition 弹窗位置：1--首页弹窗，2--首页悬浮窗
     * @param channelId 渠道号
     * @param appVersion 版本号
     * @param mobileType 手机类型应用端:1-ios，2-安卓
     * @return
     */
    public PopConfDto popConf(Long popTime, int popPosition, String channelId, String appVersion, String mobileType) {
        StringBuffer versionSql = new StringBuffer("select * from pop_conf a where a.state=1 ");
        versionSql.append(" and a.pop_position = ").append(popPosition);
        List<PopConf> popConfLst = dynamicQuery.nativeQueryList(PopConf.class,versionSql.toString());
        if(CollectionUtils.isEmpty(popConfLst)){
            return null;
        }
        PopConf popConf = popConfLst.get(0);
        //popConfRepository.findFirstByPopPositionAndState(popPosition, 1);
        if(popConf == null) {
            return null;
        }
        if(!VersionUtil.isBetween(popConf.getVersion1(),popConf.getVersion2(),appVersion)){
            return null;
        }
        channelId = ChannelUtil.filter(channelId, mobileType);
        if(StringUtils.isNotEmpty(channelId) && popConf.getChannelId().indexOf(channelId) == -1){
            return null;
        }

        PopConfDto popConfDto = new PopConfDto();
        BeanUtils.copyProperties(popConf, popConfDto);

        int timing = popConf.getTiming(); //出现时机：1每天出现1次。2期间仅出现1次。3每次重新进入出现1次。
        Date sDate = popConf.getSdate();
        Date eDate = popConf.getEdate();
        popTime = Optional.ofNullable(popTime).orElse(-1L);
        if(popTime == -1)
            return null;
        Long now = System.currentTimeMillis();  //当前时间戳
        Long sDateTime = (sDate == null ? 0 : sDate.getTime());
        Long sEndTime = (eDate == null ? 9999999999999L : eDate.getTime());
        if(sDateTime <= now && now <= sEndTime) {
            if(timing == Constant.POP_TIMING_START) {
                return popConfDto;
            }
            //没出现过
            if(popTime == 0){
                if(timing == Constant.POP_TIMING_DAY || timing == Constant.POP_TIMING_SECTION) {
                    return popConfDto;
                }
            }
            //出现过
            if(popTime != 0){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String popDate = sdf.format(new Date(popTime));
                String nowDate = sdf.format(new Date());
                //当天没出现过
                if(!popDate.equals(nowDate) && timing == Constant.POP_TIMING_DAY) {
                    return popConfDto;
                }
            }
        }
        return null;
    }


    @Override
    public List<WifiPopConfDto> wifiPopConf(String appPackage, String channelId, String appVersion,String deviceId,int popPosition) {
        StringBuffer versionSql = new StringBuffer("select * from pop_conf a where a.state=1 and now() BETWEEN a.s_date and a.e_date ");
        versionSql.append(" and a.app_package like ? ");
        versionSql.append(" and a.channel_id like ?  ");
        versionSql.append(" and concat(lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX('").append(appVersion).append("','.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX('").append(appVersion).append("','.',-1), 3, '0')) + 0");
        versionSql.append(" BETWEEN ");
        versionSql.append(" concat(lpad(SUBSTRING_INDEX(version1,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version1,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version1,'.',-1), 3, '0')) + 0");
        versionSql.append(" AND ");
        versionSql.append(" concat(lpad(SUBSTRING_INDEX(version2,'.',1), 3, '0'),lpad(SUBSTRING_INDEX(SUBSTRING_INDEX(version2,'.',-2),'.',1), 3, '0'),lpad(SUBSTRING_INDEX(version2,'.',-1), 3, '0')) + 0");
        versionSql.append(" and a.pop_position = ").append(popPosition);
        versionSql.append(" order by a.s_date");
        //List<PopConf> popConfLst = popConfRepository.findWifiPopConf(appPackages, channelIds, versionSql.toString());
        //System.out.println(versionSql.toString());
        List<PopConf> popConfLst = dynamicQuery.nativeQueryList(PopConf.class,versionSql.toString(),"%" + appPackage + "%","%" + channelId + "%");
        if(popConfLst==null && popConfLst.size() == 0){
            return null;
        }
        //获取弹窗明细配置
        List<String> popConfIdLst = popConfLst.stream().map(popConf -> ""+popConf.getId()).collect(Collectors.toList());
        StringBuffer sqlBuf = new StringBuffer("select * from pop_conf_detail where state=1 ");
        sqlBuf.append("and pop_id in ('").append(String.join("','",popConfIdLst)).append("')");
        List<Map<String,Object>> popConfDetailLst = dynamicQuery.nativeQueryListMap(sqlBuf.toString());

        List<WifiPopConfDto> wifiPopConfDtoList = Lists.newArrayList();
        Map<String,Date> popMap = Maps.newHashMap();
        popConfLst.stream().forEach(popConf -> {
            WifiPopConfDto popConfDto = new WifiPopConfDto();
            String popKey = popConf.getChannelId() + "_" +popConf.getAppPackage();
            if(MapUtils.isNotEmpty(popMap) && popMap.get(popKey) != null){
                return;
            }
            List<Map<String,Object>> singleDetailLst =
                    popConfDetailLst.stream().filter(popConfDetailDto ->
                            popConfDetailDto.get("pop_id").equals(popConf.getId().intValue())).collect(Collectors.toList());
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(singleDetailLst)){
                String jsonStr = JSONObject.toJSONString(singleDetailLst);
                popConf.setConfData(jsonStr);
            }
            //v3.0.4以前版本固定1分钟，v3.0.4及以后读取后台配置秒数
            boolean isHigh = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_303);
            if (!isHigh) {
                popConf.setRotationTime("1");
            }
            BeanUtils.copyProperties(popConf, popConfDto);
            wifiPopConfDtoList.add(popConfDto);
        });
        queryWifiPopConfInfo(wifiPopConfDtoList,appPackage,deviceId,appVersion);
        return wifiPopConfDtoList;
    }

    /**
     * 查询wifi内容迭代信息
     * @param wifiPopConfDtoList
     */
    private void queryWifiPopConfInfo(List<WifiPopConfDto> wifiPopConfDtoList, String appPackage, String deviceId, String appVersion) {
        //根据设备号查询用户激活日；
        ClDeviceVo deviceVo = clDeviceService.getDeviceByDeviceIdAppPackage(deviceId,appPackage);
        if (deviceVo == null || StringUtils.isEmpty(deviceVo.getDistinctId())) {
            return;
        }
        String result = predictService.getActiveDate(appPackage, deviceVo.getDistinctId());
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(jsonObject.getInteger(code) != null && success == jsonObject.getInteger(code)) {
            String dataTime = jsonObject.getString("data");
            if (StringUtils.isNotEmpty(dataTime)) {
                Map<String,String> timeMap = JSON.parseObject(dataTime,Map.class);
                String activeTime = timeMap.get("activeTime");
                if (StringUtils.isNotEmpty(activeTime)) {
                    Date userDate = DateUtil.valueOf(activeTime);
                    int days = 1 + DateUtil.daysBetween(userDate, new Date());
                    if (days > 0) {
                        StringBuffer sql = new StringBuffer("select * from pop_conf_auto_search where project_type = 2 and app_packages like ");
                        sql.append(" '%" + appPackage + "%'");
                        List<PopConfAutoSearch> popConfList = dynamicQuery.nativeQueryList(PopConfAutoSearch.class,sql.toString());
                        if (popConfList != null && popConfList.size() > 0) {
                            PopConfAutoSearch autoSearch = popConfList.get(0);
                            if (days <= autoSearch.getDayCount()) {
                                sql = new StringBuffer("select * from pop_conf_video where project_type = 2 and act_day = ");
                                sql.append(days);
                                sql.append(" order by sort");
                                List<PopConfVideo> popConfVideoList = dynamicQuery.nativeQueryList(PopConfVideo.class, sql.toString());
                                if (popConfVideoList == null && popConfVideoList.size() == 0) {
                                    return;
                                }
                                WifiPopConfDto popConfDto = new WifiPopConfDto();
                                List<PopConfDetailDto> detailDtoList = Lists.newArrayList();
                                if (!CollectionUtils.isEmpty(wifiPopConfDtoList)) {
                                    popConfDto = wifiPopConfDtoList.get(0);
                                    String confData = popConfDto.getConfData();
                                    if (StringUtils.isNotEmpty(confData)) {
                                        detailDtoList = JSON.parseArray(confData,PopConfDetailDto.class);
                                    }
                                }
                                popConfDto.setPopPosition(3);
                                //v3.0.4以前版本固定1分钟，v3.0.4及以后读取后台配置秒数
                                boolean isHigh = VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_303);
                                if (!isHigh) {
                                    popConfDto.setRotationTime("1");
                                } else {
                                    popConfDto.setRotationTime(autoSearch.getRotationTime() + "");
                                }
                                popConfDto.setPopType(autoSearch.getPopType() + "");
                                Date sdate = DateUtil.getDayStartTime(new Date());
                                Date edate = DateUtil.getDayEndTime(new Date());
                                popConfDto.setSdate(sdate);
                                popConfDto.setEdate(edate);
                                for (PopConfVideo confVideo : popConfVideoList) {
                                    PopConfDetailDto detailDto = new PopConfDetailDto();
                                    detailDto.setId(Long.valueOf(confVideo.getId()));
                                    detailDto.setTitle(confVideo.getTitle());
                                    if (confVideo.getType() == 1) {
                                        detailDto.setJump_type(2);
                                        detailDto.setImg(confVideo.getImgUrl());
                                        detailDto.setJump_link(confVideo.getVideoId() + "");
                                    } else if (confVideo.getType() == 1) {
                                        detailDto.setJump_type(4);
                                    } else {
                                        detailDto.setJump_type(1);
                                        detailDto.setJump_link(confVideo.getLinkUrl());
                                    }
                                    detailDtoList.add(detailDto);
                                }
                                popConfDto.setConfData(JSON.toJSONString(detailDtoList));
                                if (CollectionUtils.isEmpty(wifiPopConfDtoList)) {
                                    wifiPopConfDtoList.add(popConfDto);
                                }
                                log.info("deviceId：{},激励日：{},appPackage：{},appVersion：{},wifi通知栏内容迭代下发数据：{}",deviceId,days,appPackage,appVersion,JSON.toJSONString(wifiPopConfDtoList));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ResultMap projectCondition() {
        String errorMsg = "";
        try {
            firstVideosService.getFirstVideosById(4312L);
            log.info("【西柚数据库ballvideo】：查询成功！");
        } catch (Exception e) {
            log.error("【西柚数据库ballvideo】：查询失败！{}",e);
            errorMsg = errorMsg + "【西柚数据库ballvideo】：查询失败！" + "/n";
        }
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("projectCondition", "projectCondition");
            param.put(Constant.queryType, Constant.position);
            param.put("positionType", "detailPage");
            param.put("mobileType", Constant.ANDROID);
            param.put("appPackage", Constant.GGSPPACKAGE);
            dynamic2Query.getAdversWithCache(param, AdvFieldType.PositionType);
            log.info("【广告数据库ballvideoadv】：查询成功！");
        } catch (Exception e) {
            log.error("【广告数据库ballvideoadv】：查询失败！{}",e);
            errorMsg = errorMsg + "【广告数据库ballvideoadv】：查询失败！" + "/n";
        }
        try {
            toolMofangService.getChannelGroups(Constant.GGSPPACKAGE);
            log.info("【魔方数据库channel_tool_mofang】：查询成功！");
        } catch (Exception e) {
            log.error("【魔方数据库channel_tool_mofang】：查询失败！{}",e);
            errorMsg = errorMsg + "【魔方数据库channel_tool_mofang】：查询失败！" + "/n";
        }
        try {
            String appType = AppPackageUtil.getAppType(Constant.GGSPPACKAGE);
            String nativeSql = "select count(1) from user_gold a " +
                    "where a.user_id = '" + 33 + "' and a.app_type = '" + appType + "' ";
            dynamic5Query.nativeQueryObject(nativeSql);
            log.info("【任务中心数据库ballvideotask】：查询成功！");
        } catch (Exception e) {
            log.error("【任务中心数据库ballvideotask】：查询失败！{}",e);
            errorMsg = errorMsg + "【任务中心数据库ballvideotask】：查询失败！" + "/n";
        }
        try {
            String key = RedisKeyConstant.CAT_IDS+1+"_"+10;
            redisService.get(key);
            log.info("【西柚Redis-db4】：查询成功！");
        } catch (Exception e) {
            log.error("【西柚Redis-db4】：查询失败！{}",e);
            errorMsg = errorMsg + "【西柚Redis-db4】：查询失败！" + "/n";
        }
        try {
            Query query = new Query(Criteria.where("video_id").is(4312).and("state").is(1));
            mongoTemplate.findOne(query, VideoHotspotVo.class);
            log.info("【西柚mongodb-log】：查询成功！");
        } catch (Exception e) {
            log.error("【西柚mongodb-log】：查询失败！{}",e);
            errorMsg = errorMsg + "【西柚mongodb-log】：查询失败！" + "/n";
        }
        try {
            videoEsService.getById(4312L);
            log.info("【西柚ES】：查询成功！");
        } catch (Exception e) {
            log.error("【西柚ES】：查询失败！{}",e);
            errorMsg = errorMsg + "【西柚ES】：查询失败！" + "/n";
        }
        try {
            FirstVideoParamsVo paramsVo = new FirstVideoParamsVo();
            String[] ipInfo = ipService.getCurrentIpInfo();
            if (ipInfo != null) {
                paramsVo.setIp(ipInfo[0]);
            }
            paramsVo.setCatid("1");
            paramsVo.setNum("1");
            predictService.getRecommendVideoIds(paramsVo, Constant.recommend2);
            log.info("【大数据推荐算法】：查询成功！");
        } catch (Exception e) {
            log.error("【大数据推荐算法】：查询失败！{}",e);
            errorMsg = errorMsg + "【大数据推荐算法】：查询失败！" + "/n";
        }

        if ("".equals(errorMsg)) {
            return ResultMap.success(200, "西柚项目检测情况：正常！");
        } else {
            errorMsg = "西柚项目检测情况：/n" + errorMsg;
            return ResultMap.error(400, errorMsg);
        }
    }

}
