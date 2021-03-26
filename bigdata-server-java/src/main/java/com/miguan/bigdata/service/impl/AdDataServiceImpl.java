package com.miguan.bigdata.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.dto.EarlyWarningDto;
import com.miguan.bigdata.mapper.AdDataMapper;
import com.miguan.bigdata.mapper.DspPlanMapper;
import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.vo.DspPlanVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.bigdata.common.constant.BigDataConstants.*;
import static com.miguan.bigdata.common.util.NumCalculationUtil.roundHalfUpDouble;
import static tool.util.DateUtil.dateStr2;
import static tool.util.DateUtil.dateStr7;

/**
 * @Description 监测service
 * @Author zhangbinglin
 * @Date 2020/11/6 8:59
 **/
@Slf4j
@Service
public class AdDataServiceImpl implements AdDataService {

    @Resource
    private AdDataMapper adDataMapper;
    @Resource
    private DspPlanMapper dspPlanMapper;
    @Resource
    private RedisClient redisClient;

    /**
     * 查询出广告配置代码位自动排序阀值的代码位
     * @param dd 日期，格式：yyyy-MM-dd
     * @param showThreshold 展现量阀值
     * @param type 类型，1:未达到阀值，2：已达到阀值
     * @param adIds 代码位id
     * @return
     */
    public List<String> listAdIdShowThreshold(String dd, Integer showThreshold, Integer type, String adIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("dd", Integer.parseInt(dd.replace("-","")));
        params.put("showThreshold", showThreshold);
        params.put("type", type);
        if(StringUtil.isNotBlank(adIds)) {
            params.put("adIds", Arrays.asList(adIds.split(",")));
        }
        return adDataMapper.listAdIdShowThreshold(params);
    }

    /**
     * 钉钉-广告展示/广告库存比值预警
     * @Param warnType 类型：0-每小时预警，1-每天10点预警前24小时
     * @return
     */
    public String findEarlyWarnList(int warnType) {
        Map<String, Object> params = new HashMap<>();
        String appVersions = redisClient.get("early_warn_app_version");
        params.put("appVersions", appVersions);
        params.put("dd", Integer.parseInt(dateStr7(new Date())) + "," + Integer.parseInt(dateStr7(DateUtil.rollDay(new Date(), -1))));
        String activeUser;
        if(warnType == warnTypeHour) {
            //每小时预警
            params.put("startTime", DateUtil.dateStr4(DateUtil.rollMinute(new Date(), -60)));
            params.put("endTime", DateUtil.dateStr4(new Date()));
            activeUser = redisClient.get("hour_active_user");
        } else {
            //每24小时预警
            params.put("startTime", DateUtil.dateStr4(DateUtil.rollDay(new Date(), -1)));
            params.put("endTime", DateUtil.dateStr4(new Date()));
            activeUser = redisClient.get("day_active_user");
        }
        activeUser = (StringUtils.isBlank(activeUser) ? "0" : activeUser);
        params.put("activeUser", Integer.parseInt(activeUser));  //报警条件:昨日日活数大于activeUser

        //广告位与库存比值预警
        params.put("warnType", warnType);
        params.put("type", 1);
        List<EarlyWarningDto> list = this.findEarlyWarnList(params);
        String result1 = earlyWarnString(warnType,1, list);

        //广告位与库存比值降幅预警
        params.put("type", 2);
        List<EarlyWarningDto> list2 = findDecreaseEarlyWarnList(warnType, params);
        String result2 = earlyWarnString(warnType,2, list2);

        //广告位库存为0预警
        params.put("type", 3);
        list = this.findEarlyWarnList(params);
        String result3 = earlyWarnString(warnType,3, list);

        return result1 + result2 + result3;
    }

    /**
     * 获取需要报警的广告数据
     * @param params
     * @return
     */
    private List<EarlyWarningDto> findEarlyWarnList(Map<String, Object> params) {
        List<EarlyWarningDto> list = adDataMapper.findEarlyWarnList(params);
        if(list.isEmpty()) {
            return list;
        }
        return list;
    }

    private List<EarlyWarningDto> findDecreaseEarlyWarnList(int warnType, Map<String, Object> params) {
        List<EarlyWarningDto> list = this.findEarlyWarnList(params);  //查询前24小时的 广告位与库存比值
        log.info("广告位与库存比值降幅预警这次的值：{}", JSONObject.toJSON(list));

        String redisKey = (warnType == warnTypeHour ? "earlyWarnList" : "earlyWarnList24");
        String lastJson =redisClient.get(redisKey);  //从缓存查询出上个24小时的 广告位与库存比值
        String json = JSON.toJSONString(list);
        redisClient.set(redisKey, json, 3 * 24 * 60 * 60);
        if(StringUtil.isBlank(lastJson)) {
            return null;
        }
        log.info("广告位与库存比值降幅预警上次的值：{}", json);


        JSONArray jsonArray = JSONArray.parseArray(lastJson);
        Map<String, Double> lastMap = new HashMap<>();
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject one = jsonArray.getJSONObject(i);
            lastMap.put(one.getString("fatherChannel") + one.getString("appVersion") + one.getString("packageNameZw") + one.getString("adKeyName"), one.getDoubleValue("showStockRate"));
        }

        List<EarlyWarningDto> result = new ArrayList<>();
        for(EarlyWarningDto dto : list) {
            String fatherChannel = dto.getFatherChannel();
            String appVerson = dto.getAppVersion();
            String packageNameZw = dto.getPackageNameZw();
            String adKeyName = dto.getAdKeyName();
            double lastValue = lastMap.get(fatherChannel + appVerson + packageNameZw + adKeyName) == null ? 0 : lastMap.get(fatherChannel + appVerson + packageNameZw + adKeyName);
            double cha = lastValue - dto.getShowStockRate();
            if(cha > 20) {
                //上个时间点计算比值 - 本次计算比值 ＞ 20%
                EarlyWarningDto one = new EarlyWarningDto();
                BeanUtils.copyProperties(dto, one);
                one.setShowStockRate(cha);
                result.add(one);
            }
        }
        //根据showStockRate 降序
        result = result.stream().sorted(Comparator.comparing(EarlyWarningDto::getShowStockRate).reversed()).collect(Collectors.toList());
        return result;
    }

    /**
     * 把结果转成字符串
     * @param type 1:广告位与库存比值预警 2:广告位与库存比值降幅预警3:广告位库存为0
     * @param warnType 类型：0-每半小时预警，1-每天10点预警前24小时
     * @param list
     * @return
     */
    private String earlyWarnString(int warnType, int type, List<EarlyWarningDto> list) {
        if(list == null || list.isEmpty()) {
            return "";
        }
        String warnTypeStr = (warnType == warnTypeHour ? "(每小时)" : "(每24小时)");
        StringBuffer result = new StringBuffer();
        if(type == 1) {
            result.append("【广告位与库存比值预警" + warnTypeStr +"】\n");
        } else if(type == 2) {
            result.append("【广告位与库存比值降幅预警" + warnTypeStr +"】\n");
        } else if(type == 3) {
            result.append("【广告位库存为0" + warnTypeStr + "】\n");
        }
        for(EarlyWarningDto dto : list) {
            result.append(dto.getFatherChannel() + "_" + dto.getAppVersion() + "_" +dto.getPackageNameZw() + "_" + dto.getAdKeyName() + "_日活数" + dto.getActiveUser());
            if(type == 1) {
                result.append(" 比值为：" + roundHalfUpDouble(2,dto.getShowStockRate()) + "%\n");
            } else if(type == 2) {
                result.append(" 降幅为：" + roundHalfUpDouble(2,dto.getShowStockRate()) + "%\n");
            } else if(type == 3) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * 从大数据 同步数据到dsp的idea_advert_report表（存储计划的有效曝光数，有效点击数等指标）
     * @param startDay 开始日期，格式：yyyyMMdd
     * @param endDay 结束日期, 格式：yyyyMMdd
     * @return
     */
    public void syncDspPlan(Integer startDay, Integer endDay) {
        List<DspPlanVo> list =  adDataMapper.findDspPlanList(startDay, endDay);  //从大数据查询出数据
        if(list == null || list.isEmpty()) {
            return;
        }
        dspPlanMapper.batchReplaceAdActionDay(list);  //把数据同步到mysql的dsp库中（批量替换，存在则先删除，在插入）
    }

    /**
     * 从大数据 查询广告计划今天消耗的金额
     * @return
     */
    public BigDecimal findPlanConsumption(Long planId) {
        String date = DateUtil.dateStr2(new Date()); //获取当天日期
        return adDataMapper.findDspPlanConsumption(date, date,planId.toString());  //从大数据查询出数据
    }

    /**
     * 从ck统计数据同步到allvideoadv库ad_multi_dimensional_data的表
     * @param dd 统计日期，格式：yyyy-MM-dd
     */
    public void syncAdMultiDimensionalData(String dd) {
        Integer dt = Integer.parseInt(dd.replace("-", ""));
        Map<String, Object> params = new HashMap<>();
        params.put("dd", dd);
        params.put("dt", dt);

        params.put("type", 1); //type=1表示删除指定日期的数据
        adDataMapper.deleteAdMultiDimensionalData(params);
        adDataMapper.syncAdMultiDimensionalData(params);  //统计并同步数据
    }
}
