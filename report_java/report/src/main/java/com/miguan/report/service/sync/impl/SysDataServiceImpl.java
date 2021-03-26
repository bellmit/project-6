package com.miguan.report.service.sync.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.report.common.constant.CommonConstant;
import com.miguan.report.common.dynamicquery.DynamicQuery4LaiDian;
import com.miguan.report.common.enums.GdtAppKeyEnum;
import com.miguan.report.common.enums.KuaiShouAppKeyEnum;
import com.miguan.report.common.enums.PlatFormEnum;
import com.miguan.report.common.util.NumCalculationUtil;
import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.entity.BannerData;
import com.miguan.report.entity.BannerRule;
import com.miguan.report.entity.report.BannerDataExt;
import com.miguan.report.mapper.SyncDataMapper;
import com.miguan.report.repository.BannerDataExtRepository;
import com.miguan.report.repository.BannerDataRepository;
import com.miguan.report.repository.HourDataRepository;
import com.miguan.report.service.sync.SyncDataService;
import com.miguan.report.service.third.GdtService;
import com.miguan.report.service.third.KuaiShouService;
import com.miguan.report.vo.mongo.BannerExtVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 同步数据 service
 */
@Slf4j
@Service
public class SysDataServiceImpl implements SyncDataService {

    @Resource(name = "primaryMongoTeamplate")
    private MongoTemplate primaryMongoTeamplate;
    @Resource(name = "secondMongoTeamplate")
    private MongoTemplate secondMongoTeamplate;
    @Resource
    private BannerDataExtRepository bannerDataExtRepository;
    @Resource
    private DynamicQuery4LaiDian dynamicQuery4LaiDian;
    @Resource
    private HourDataRepository hourDataRepository;
    @Resource
    private SyncDataMapper syncDataMapper;
    @Resource
    private BannerDataRepository bannerDataRepository;
    @Resource
    private KuaiShouService kuaiShouService;
    @Resource
    private GdtService gdtService;

    /**
     * 同步错误数和请求数到banner
     *
     * @param date (格式:yyyyMMdd)
     */
    @Override
    public void saveBannerDataExt(Date date, Integer appType) {
        List<BannerExtVo> reqNumList = null;    //请求数
        List<BannerExtVo> errorNumList = null;  //错误数
        String dateStr = DateUtil.dateStr7(date);
        if (appType == CommonConstant.VIDEO_APP_TYPE) {
            //视频
            reqNumList = queryVideoRequestNum(dateStr);
            errorNumList = queryVideoErrorNum(dateStr);
        } else {
            //来电
            reqNumList = queryLaidianRequestNum(dateStr);
            errorNumList = queryLaidianErrorNum(dateStr);
        }

        Map<String, Integer> errorNumMap = new HashMap<>();
        for (BannerExtVo bev : errorNumList) {
            errorNumMap.put(bev.get_id(), bev.getValue());
        }

        for (BannerExtVo reqNum : reqNumList) {
            if (reqNum.get_id() == null && reqNum.getAdId() == null) {
                continue;
            }
            String adId = (appType == CommonConstant.VIDEO_APP_TYPE ? reqNum.get_id() : reqNum.getAdId());
            Integer errNum = errorNumMap.get(adId) == null ? 0 : errorNumMap.get(adId);
            Integer requestNum = reqNum.getValue() == null ? 0 : reqNum.getValue();
            BannerDataExt bde = new BannerDataExt(adId, errNum, requestNum, date, appType);
            bde.setCreateTime(Integer.parseInt((System.currentTimeMillis() / 1000) + ""));
            bde.setErrRate(0D);
            bannerDataExtRepository.save(bde);
        }
    }

    /**
     * 从视频库(mongodb)统计代码位每日的错误数
     *
     * @param date (格式:yyyyMMdd)
     */
    private List<BannerExtVo> queryVideoErrorNum(String date) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("ad_id").count().as("value"));
        return primaryMongoTeamplate.aggregate(aggregation, "ad_error" + date, BannerExtVo.class).getMappedResults();
    }

    /**
     * 从视频库(mongodb)统计代码位每日的请求数
     *
     * @param date
     * @return
     */
    private List<BannerExtVo> queryVideoRequestNum(String date) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("ad_id").sum("total_num").as("value"));
        return primaryMongoTeamplate.aggregate(aggregation, "ad_error_count_log" + date, BannerExtVo.class).getMappedResults();
    }

    /**
     * 从来电库(mysql)统计代码位每日的错误数
     *
     * @param date (格式:yyyyMMdd)
     */
    private List<BannerExtVo> queryLaidianErrorNum(String date) {
        String sql = "SELECT ad_id adId, count(1) value FROM ad_error_" + date + " GROUP BY ad_id";
        return dynamicQuery4LaiDian.nativeQueryList(BannerExtVo.class, sql);
    }


    /**
     * 从来电(mysql)统计代码位每日的请求数
     *
     * @param date
     * @return
     */
    private List<BannerExtVo> queryLaidianRequestNum(String date) {
        String sql = "select ad_id adId, sum(total_num) value from ad_error_count_log_" + date + " GROUP BY ad_id";
        return dynamicQuery4LaiDian.nativeQueryList(BannerExtVo.class, sql);
    }


    /**
     * 删除指定日期的banner_data_ext表的数据
     *
     * @param date
     */
    @Override
    public void deleteBannerDataExt(String date) {
        this.bannerDataExtRepository.deleteByDate(date);
    }

    /**
     * 计算错误率
     *
     * @param date
     */
    @Override
    public void updateBannerExtErrRate(String date) {
        this.bannerDataExtRepository.updateErrRate(date);
    }

    /**
     * 从埋点库(mongodb)统计每小时的展现量
     *
     * @param date     (格式:yyyyMMdd)
     * @param statType ad_zone_show：展现量，ad_zone_click：点击量
     */
    private List<BannerExtVo> queryVideoSHowAndClickNum(String date, String statType) {
        MatchOperation match = Aggregation.match(Criteria.where("action_id").is(statType).and("ad_id").exists(true));
        ProjectionOperation project = Aggregation.project("ad_id").and("creat_time").project("substr", 11, 2).as("hour");
        GroupOperation group = Aggregation.group("ad_id", "hour").count().as("value");
        Aggregation aggregation = Aggregation.newAggregation(match, project, group);
        return secondMongoTeamplate.aggregate(aggregation, "xy_burying_point" + date, BannerExtVo.class).getMappedResults();
    }

    /**
     * 同步每小时展现数和点击数到hour_data表
     *
     * @param date (格式:yyyyMMdd)
     */
    @Override
    public void saveHourDataExt(Date date) {
        String dateStr = DateUtil.dateStr7(date);
        List<BannerExtVo> showNumList = queryVideoSHowAndClickNum(dateStr, "ad_zone_show");
        ;    //展现数
        List<BannerExtVo> clickNumList = queryVideoSHowAndClickNum(dateStr, "ad_zone_click");
        ;  //点击数

        Map<String, Integer> clickNumMap = new HashMap<>();
        for (BannerExtVo bev : clickNumList) {
            clickNumMap.put(bev.get_id(), bev.getValue());
        }

        String dateStr2 = DateUtil.dateStr2(date);
        List<String> contents = new ArrayList<>();
        for (BannerExtVo showNumVo : showNumList) {
            JSONObject json = JSONObject.parseObject(showNumVo.get_id());
            String adId = json.getString("ad_id");
            String hour = json.getString("hour");
            Integer showNum = showNumVo.getValue() == null ? 0 : showNumVo.getValue();
            Integer clickNum = clickNumMap.get(showNumVo.get_id()) == null ? 0 : clickNumMap.get(showNumVo.get_id());
            Integer createTime = Integer.parseInt((System.currentTimeMillis() / 1000) + "");
            createMessageValueList(contents, adId, dateStr2, hour, showNum, clickNum, createTime);
            if (contents.size() % 1000 == 0) {  //每1000条，批量插入一次数据
                syncDataMapper.batchInsertHourData(contents);
                contents.clear();
            }
        }
        if (contents.size() > 0) {
            syncDataMapper.batchInsertHourData(contents);
        }
    }

    /**
     * 拼接insert语句的value部分字符串语句
     *
     * @param contents   nsert语句value部分的字符串集合
     * @param adId       代码位
     * @param date       日期：yyyy-MM-dd
     * @param hour       小时
     * @param showNum    展示数
     * @param clickNum   点击数
     * @param createTime 时间戳
     */
    private void createMessageValueList(List<String> contents, String adId, String date, String hour, Integer showNum, Integer clickNum, Integer createTime) {
        String content = "('{adId}', '{date}', '{hour}', {showNum}, {clickNum},0, {createTime})";
        content = content.replace("{adId}", adId).replace("{date}", date).replace("{hour}", hour)
                .replace("{showNum}", showNum + "").replace("{clickNum}", clickNum + "").replace("{createTime}", createTime + "");
        contents.add(content);
    }

    /**
     * 删除指定日期的hour_data表的数据
     *
     * @param date 日期：yyyy-MM-dd
     */
    @Override
    public void deleteHourDataByDate(String date) {
        hourDataRepository.deleteHourDataByDate(date);
    }

    /**
     * 计算hour_data表的点击率
     *
     * @param date 日期：yyyy-MM-dd
     */
    @Override
    public void updateClickRate(String date) {
        hourDataRepository.updateClickRate(date);
    }

    /**
     * 从快手获取数据
     *
     * @param date           日期
     * @param videoAdnameMap 视频代码位信息（广告库中的）
     * @param callAdnameMap  来电代码位信息（广告库中的）
     * @param bannerRuleMap  代码位信息（报表库中的）
     */
    @Override
    @Transactional
    public void getDataFromKs(Date date, Map<String, AdIdAndNameDto> videoAdnameMap, Map<String, AdIdAndNameDto> callAdnameMap, Map<String, BannerRule> bannerRuleMap) {
        log.info("从快手获取数据任务开始");
        String dateStr = DateUtil.dateStr2(date);
        bannerDataRepository.deleteByDateAndPlatForm(dateStr, PlatFormEnum.kuai_shou.getId());  //获取数据钱，先删除之前获取的数据

        JSONArray array = kuaiShouService.getDailyShare(dateStr);
        log.info("快手数据返回结果>>{}", array.toString());
        if (CollectionUtils.isNotEmpty(array)) {
            List<BannerData> dataList = new ArrayList<BannerData>();

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                // 代码位ID
                String ad_id = object.getString("position_id");
                KuaiShouAppKeyEnum anEnum = KuaiShouAppKeyEnum.getByAppId(object.getString("app_id"));
                if(anEnum == null){
                    log.error("快手应用ID不存在>>{}", object.getString("app_id"));
                    continue;
                }

                AdIdAndNameDto adName = null;
                if (anEnum.getAppEnum().getAppType() == CommonConstant.VIDEO_APP_TYPE) {
                    adName = videoAdnameMap.get(ad_id);
                } else {
                    adName = callAdnameMap.get(ad_id);
                }
                if (adName == null) {
                    log.error("快手的adName不存在>>{}", object.getString("app_id"));
                    continue;
                }
                BannerRule bannerRule = bannerRuleMap.get(adName.getTotalName() + "-" + anEnum.getAppEnum().getAppType());
                if (bannerRule == null) {
                    log.error("快手的bannerRule不存在>>{}", object.getString("app_id"));
                    continue;
                }
                BannerData bannerData = new BannerData();
                bannerData.setDate(date);
                bannerData.setAppId(anEnum.getAppId());
                bannerData.setAppName(anEnum.getName());
                bannerData.setAdSpace("");
                bannerData.setAdSpaceType("");
                bannerData.setRuleAdSpace(bannerRule.getAdSpace());
                bannerData.setAdSpaceId(ad_id);
                bannerData.setAccessMode("SDK");
                bannerData.setShowNumber(object.getIntValue("impression"));
                bannerData.setClickNumber(object.getIntValue("click"));
                bannerData.setClickRate(object.getDoubleValue("ctr") * 100);
                bannerData.setProfit(object.getDoubleValue("share"));
                bannerData.setCpm(object.getDoubleValue("ecpm"));
                //点击单价=营收/点击量
                if (bannerData.getClickNumber() == 0) {
                    bannerData.setClickPrice(0D);
                } else {
                    double clickPrice = bannerData.getProfit() / bannerData.getClickNumber();
                    bannerData.setClickPrice(NumCalculationUtil.roundHalfUpDouble(clickPrice));
                }
                bannerData.setCreatedAt(new Date());
                bannerData.setUpdatedAt(new Date());
                bannerData.setRuleId(bannerRule.getId());
                bannerData.setCutAppName(anEnum.getAppEnum().getAppName());
                bannerData.setClientId(anEnum.getClientEnum().getId());
                bannerData.setAdStyle(bannerRule.getAdStyle());
                bannerData.setAdType(bannerRule.getAdType());
                bannerData.setAppType(anEnum.getAppEnum().getAppType());
                bannerData.setTotalName(adName.getTotalName());
                bannerData.setPlatForm(PlatFormEnum.kuai_shou.getId());
                dataList.add(bannerData);
            }
            log.info("快手数据打包结果>>{}", JSONObject.toJSONString(dataList));
            bannerDataRepository.saveAll(dataList);
        }

        //汇总数据到banner_data_total_name表
        syncDataMapper.deleteBannerDataTotalName(dateStr, PlatFormEnum.kuai_shou.getId());
        syncDataMapper.insertBannerDataTotalName(dateStr, PlatFormEnum.kuai_shou.getId());
        log.info("从快手通获取数据任务结束");
    }

    /**
     * 从广点通获取数据
     *
     * @param date           日期
     * @param videoAdnameMap 视频代码位信息（广告库中的）
     * @param callAdnameMap  来电代码位信息（广告库中的）
     * @param bannerRuleMap  代码位信息（报表库中的）
     */
    @Override
    @Transactional
    public void getDataFromGdt(Date date, Map<String, AdIdAndNameDto> videoAdnameMap, Map<String, AdIdAndNameDto> callAdnameMap, Map<String, BannerRule> bannerRuleMap) {
        log.info("从广点通获取数据任务开始");
        //获取数据钱，先删除之前获取的数据
        bannerDataRepository.deleteByDateAndPlatForm(DateUtil.dateStr2(date), PlatFormEnum.guang_dian_tong.getId());
        JSONArray array = gdtService.getReportDatas(DateUtil.dateStr7(date), DateUtil.dateStr7(date));
        log.info("广点通数据返回结果>>{}", array.toString());
        if (CollectionUtils.isNotEmpty(array)) {
            List<BannerData> dataList = new ArrayList<BannerData>();

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                // 代码位ID
                String ad_id = object.getString("placement_id");
                GdtAppKeyEnum anEnum = GdtAppKeyEnum.getByAppId(object.getString("app_id"));
                if(anEnum == null){
                    log.error("广点通的应用ID不存在>>{}", object.getString("app_id"));
                    continue;
                }

                AdIdAndNameDto adName = null;
                if (anEnum.getAppEnum().getAppType() == CommonConstant.VIDEO_APP_TYPE) {
                    adName = videoAdnameMap.get(ad_id);
                } else {
                    adName = callAdnameMap.get(ad_id);
                }
                if (adName == null) {
                    log.error("广点通的adName不存在>>{}", object.getString("app_id"));
                    continue;
                }
                BannerRule bannerRule = bannerRuleMap.get(adName.getTotalName() + "-" + anEnum.getAppEnum().getAppType());
                if (bannerRule == null) {
                    log.error("广点通的bannerRule不存在>>{}", object.getString("app_id"));
                    continue;
                }

                BannerData bannerData = new BannerData();
                bannerData.setDate(date);
                bannerData.setAppId(anEnum.getAppId());
                bannerData.setAppName(anEnum.getName());
                bannerData.setAdSpace("");
                bannerData.setAdSpaceType("");
                bannerData.setRuleAdSpace(bannerRule.getAdSpace());
                bannerData.setAdSpaceId(ad_id);
                bannerData.setAccessMode("");

                String pv = object.getString("pv").replace(",", ""); //展现量
                bannerData.setShowNumber(Integer.valueOf(pv));
                String click = object.getString("click").replace(",", "");//点击量
                bannerData.setClickNumber(Integer.valueOf(click));
                String clickRate = object.getString("click_rate").replace("%", "");
                bannerData.setClickRate(Double.parseDouble(clickRate));  //点击率
                String revenue = object.getString("revenue").replace(",", "");//营收
                if (StringUtils.isBlank(revenue)) {
                    bannerData.setProfit(0D);
                } else {
                    bannerData.setProfit(Double.parseDouble(revenue));
                }
                bannerData.setCpm(object.getDoubleValue("ecpm"));   //千展收益
                String requestCount = object.getString("request_count").replace(",", "");//广告位请求量
                bannerData.setAdSpaceRequest(Integer.valueOf(requestCount));
                String returnCount = object.getString("return_count").replace(",", "");//广告位返回量
                bannerData.setAdSpaceReturn(Integer.valueOf(returnCount));
                String fillRate = object.getString("fill_rate").replace("%", "");
                bannerData.setAdSpaceFilling(Double.parseDouble(fillRate));   //广告位填充率
                String adRequestCount = object.getString("ad_request_count").replace(",", "");//广告请求量
                bannerData.setAdRequest(Integer.valueOf(adRequestCount));
                String adReturnCount = object.getString("ad_return_count").replace(",", "");//广告返回量
                bannerData.setAdReturn(Integer.valueOf(adReturnCount));
                //广告填充率
                if (bannerData.getAdRequest() == null || bannerData.getAdReturn() == null || bannerData.getAdRequest() == 0) {
                    bannerData.setAdFilling(0D);
                } else {
                    double adFilling = (bannerData.getAdReturn() / (double) bannerData.getAdRequest()) * 100;
                    bannerData.setAdFilling(NumCalculationUtil.roundHalfUpDouble(adFilling));
                }
                //点击单价=营收/点击量
                if (bannerData.getClickNumber() == 0) {
                    bannerData.setClickPrice(0D);
                } else {
                    double clickPrice = bannerData.getProfit() / bannerData.getClickNumber();
                    bannerData.setClickPrice(NumCalculationUtil.roundHalfUpDouble(clickPrice));
                }
                String exposureRate = object.getString("exposure_rate").replace("%", "");
                bannerData.setExposureRate(Double.parseDouble(exposureRate));   //曝光率
                bannerData.setCreatedAt(new Date());
                bannerData.setUpdatedAt(new Date());
                bannerData.setRuleId(bannerRule.getId());
                bannerData.setCutAppName(anEnum.getAppEnum().getAppName());
                bannerData.setClientId(anEnum.getClientEnum().getId());
                bannerData.setAdStyle(bannerRule.getAdStyle());
                bannerData.setAdType(bannerRule.getAdType());
                bannerData.setAppType(anEnum.getAppEnum().getAppType());
                bannerData.setTotalName(adName.getTotalName());
                bannerData.setPlatForm(PlatFormEnum.guang_dian_tong.getId());
                dataList.add(bannerData);
            }
            log.info("广点通数据打包结果>>{}", JSONObject.toJSONString(dataList));
            bannerDataRepository.saveAll(dataList);
        }

        //汇总数据到banner_data_total_name表
        syncDataMapper.deleteBannerDataTotalName(DateUtil.dateStr2(date), PlatFormEnum.guang_dian_tong.getId());
        syncDataMapper.insertBannerDataTotalName(DateUtil.dateStr2(date), PlatFormEnum.guang_dian_tong.getId());
        log.info("从广点通获取数据任务结束");
    }
}
