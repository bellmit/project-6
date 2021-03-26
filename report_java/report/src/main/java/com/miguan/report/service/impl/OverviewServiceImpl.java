package com.miguan.report.service.impl;

import com.google.common.collect.Maps;
import com.miguan.report.mapper.OverviewMapper;
import com.miguan.report.service.report.OverviewService;
import com.miguan.report.vo.AdStaVo;
import com.miguan.report.vo.CaStatNumVoDetail;
import com.miguan.report.vo.CostStaVo;
import com.miguan.report.vo.DisaChartVo;
import com.miguan.report.vo.PerCapitaCostVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.miguan.report.common.util.AppNameUtil.convertDeviceType2name;
import static com.miguan.report.common.util.DateUtil.getDayIndexInWeek;
import static com.miguan.report.common.util.DateUtil.yyyy_MM_dd;
import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUpDouble;

/**
 * @author zhongli
 * @date 2020-06-17 
 *
 */
@Service
public class OverviewServiceImpl implements OverviewService {
    @Resource
    private OverviewMapper overviewMapper;

    /**
     *
     * @param date
     * @param appType 1=视频 2=来电
     * @return
     */
    @Override
    public Map loadAllCaStat(LocalDate date, int appType) {

        //mybatis不支持jdk-8日期，需转换
        String qdate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String momDate = date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        //昨天同比数据（获取的就是上周同天数据）
        String yoyDatePlus1 = date.minusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(4);
        sqlparams.put("qdate", qdate);
        sqlparams.put("momDate", momDate);
        sqlparams.put("yoyDatePlus1", yoyDatePlus1);
        sqlparams.put("appType", appType);
        Map rdata = new HashMap();
        Map data = overviewMapper.queryAllCaStat(sqlparams);
        Map dataExt = overviewMapper.queryUserUsTimeSta(sqlparams);
        if (!CollectionUtils.isEmpty(data)) {
            sqlparams.clear();
            sqlparams.put("appType", appType);
            sqlparams.put("qdate", qdate);
            //查询日日活量
            Long ac1 = overviewMapper.queryActiveForAllApp(sqlparams);
            ac1 = ac1 == null ? 0 : ac1;
            sqlparams.put("qdate", momDate);
            //环比日活量
            Long ac2 = overviewMapper.queryActiveForAllApp(sqlparams);
            ac2 = ac2 == null ? 0 : ac2;
            sqlparams.put("qdate", yoyDatePlus1);
            //同期日活量
            Long ac3 = overviewMapper.queryActiveForAllApp(sqlparams);
            ac3 = ac3 == null ? 0 : ac3;
            cal(data, "active", ac1, ac2, ac3);
            cal1(data, "pre_show_num", ac1, ac2, ac3);
            cal1(data, "active_value", ac1, ac2, ac3);
            rdata.putAll(data);
        }
        if (!CollectionUtils.isEmpty(dataExt)) {
            rdata.putAll(dataExt);
        }
        if (CollectionUtils.isEmpty(rdata)) {
            return null;
        }
        return rdata;
    }

    private void cal(Map data, String key, Long ac1, Long ac2, Long ac3) {
        String momKey = "mom".concat(key);
        String yoyKey = "yoy".concat(key);
        data.put(key, ac1);
        data.put(momKey, ac2);
        data.put(yoyKey, ac3);
    }

    private void cal1(Map data, String key, Long ac1, Long ac2, Long ac3) {
        String momKey = "mom".concat(key);
        String yoyKey = "yoy".concat(key);
        double ac1d = Long.valueOf(ac1).doubleValue();
        double ac2d = Long.valueOf(ac2).doubleValue();
        double ac3d = Long.valueOf(ac3).doubleValue();
        data.put(key, ac1d == 0 ? 0 : convertDouble(data.get(key)) / ac1d);
        data.put(momKey, ac2d == 0 ? 0 : convertDouble(data.get(momKey)) / ac2d);
        data.put(yoyKey, ac3d == 0 ? 0 : convertDouble(data.get(yoyKey)) / ac3d);
    }

    /**
     * 来自持久化数据进行类型转换
     * @param DSObj
     * @return
     */
    private double convertDouble(Object DSObj) {
        if (DSObj == null) {
            return 0;
        }
        if (DSObj instanceof BigDecimal) {
            return ((BigDecimal) DSObj).doubleValue();
        } else if (DSObj instanceof Double) {
            return (double) DSObj;
        } else if (DSObj instanceof Long) {
            Long.valueOf((Long) DSObj).doubleValue();
        }
        return new BigDecimal(DSObj.toString()).doubleValue();
    }

    @Override
    public List<CaStatNumVoDetail> loadCatStatDetail(int dataType, String startDate, String endDate, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(4);
        sqlparams.put("showtype", dataType);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("appType", appType);
        List<CaStatNumVoDetail> data;
        if (dataType != 6) {
            data = overviewMapper.queryCatStatDetail(sqlparams);
        } else {
            data = overviewMapper.queryUserUsTimeStaDetail(sqlparams);
        }
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }
        return data.stream().map(d -> {
            if (dataType == 6) {
                d.setAppName(convertDeviceType2name(Integer.valueOf(d.getAppName()), d.getDeviceType()));
            } else {
                d.setAppName(convertDeviceType2name(d.getAppName(), d.getDeviceType()));
            }
            return d;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdStaVo> loadAdSta(int dataType, String date, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(3);
        sqlparams.put("showtype", dataType);
        sqlparams.put("qdate", date);
        sqlparams.put("appType", appType);
        List<AdStaVo> data = overviewMapper.queryAdSta(sqlparams);
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }
        return data.stream().filter(e -> e.getAdSpace() != null && !e.getAdSpace().contains("小游戏")).map(e -> {
            e.setAppName(convertDeviceType2name(e.getAppName(), e.getDeviceType()));
            return e;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AdStaVo> loadAdStaExt(int dataType, String date, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(2);
        sqlparams.put("qdate", date);
        sqlparams.put("appType", appType);
        List<AdStaVo> data = overviewMapper.queryAdStaExt(sqlparams);
        if (CollectionUtils.isEmpty(data)) {
            return null;
        }
        return data.stream().filter(e -> e.getAdSpace() != null && !e.getAdSpace().contains("小游戏")).map(e -> {
            e.setAppName(convertDeviceType2name(e.getAppName(), e.getDeviceType()));
            return e;
        }).collect(Collectors.toList());
    }

    @Override
    public CostStaVo loadCost(int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(3);
        sqlparams.put("appType", appType);
        CostStaVo lastCost = overviewMapper.queryLastDatelCost(sqlparams);
        if (lastCost == null) {
            return null;
        }
        LocalDate lastDate = yyyy_MM_dd(lastCost.getDate());
        String momDate = lastDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        //昨天同比数据，（获取的就是上周同天的数据）
        String yoyDate = lastDate.minusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        sqlparams.put("momDate", momDate);
        sqlparams.put("yoyDate", yoyDate);
        CostStaVo momAndYoy = overviewMapper.queryMonAndYoyCost(sqlparams);
        if (momAndYoy != null) {
            lastCost.setMomcost(momAndYoy.getMomcost());
            lastCost.setYoycost(momAndYoy.getYoycost());
        }
        lastCost.setDataOfWeek(getDayIndexInWeek(lastDate));
        return lastCost;
    }

    @Override
    public List<DisaChartVo> loadCostDataChart(String startDate, String endDate, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(3);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("appType", appType);
        List<DisaChartVo> vos = overviewMapper.queryChartCost(sqlparams);
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        return vos.stream().map(e -> {
            //e.setName(convertDeviceType2name(e.getName(), e.getDeviceType()));
            e.setName(e.getName());
            return e;
        }).collect(Collectors.toList());
    }

    @Override
    public CostStaVo loadPerCapitaCost(int appType, int dataType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(4);
        sqlparams.put("appType", appType);
        sqlparams.put("showtype", dataType);
        //查询最新日期
        PerCapitaCostVo lastPerCost = overviewMapper.queryLastDateForPerCost(sqlparams);
        if (lastPerCost == null) {
            return null;
        }

        LocalDate lastDate = yyyy_MM_dd(lastPerCost.getDate());
        String momDate = lastDate.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        //昨天同比数据，（获取的就是上周同天的数据）
        String yoyDate = lastDate.minusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        sqlparams.put("qDate", lastPerCost.getDate());
        sqlparams.put("momDate", momDate);
        sqlparams.put("yoyDate", yoyDate);
        List<PerCapitaCostVo> momAndYoy = overviewMapper.queryMomAndYoyPerCost(sqlparams);
        //按日期分组
        Map<String, List<PerCapitaCostVo>> momAndYoyMap = momAndYoy.stream().collect(Collectors.groupingBy(PerCapitaCostVo::getDate));

        CostStaVo staVo = new CostStaVo();
        staVo.setDate(lastPerCost.getDate());

        List<PerCapitaCostVo> qdate = momAndYoyMap.get(lastPerCost.getDate());
        if (qdate != null) {
            staVo.setCost(getPerCostValue(dataType, qdate));
        }
        //计算环比
        List<PerCapitaCostVo> mom = momAndYoyMap.get(momDate);
        if (mom != null) {
            staVo.setMomcost(getPerCostValue(dataType, mom));
        }
        //计算同比
        List<PerCapitaCostVo> yoy = momAndYoyMap.get(yoyDate);
        if (yoy != null) {
            staVo.setYoycost(getPerCostValue(dataType, yoy));
        }
        staVo.setDataOfWeek(getDayIndexInWeek(lastDate));
        return staVo;
    }

    private double getPerCostValue(int dataType, List<PerCapitaCostVo> valus) {
        double totalCost = valus.stream().mapToDouble(e -> e.getCost()).sum();
        double calValue = dataType == 1 ? valus.stream().mapToDouble(e -> e.getNewUsers()).sum()
                : valus.stream().mapToDouble(e -> e.getActive()).sum();
        return calValue == 0 ? 0 : roundHalfUpDouble(totalCost / calValue);
    }

    @Override
    public List<PerCapitaCostVo> loadPerCapitaCostChart(int dataType, String startDate, String endDate, int appType) {
        Map<String, Object> sqlparams = Maps.newHashMapWithExpectedSize(4);
        sqlparams.put("startDate", startDate);
        sqlparams.put("endDate", endDate);
        sqlparams.put("appType", appType);
        sqlparams.put("showtype", dataType);
        List<PerCapitaCostVo> vos = overviewMapper.queryChartPerCost(sqlparams);
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        return vos.stream().map(e -> {
            //e.setName(convertDeviceType2name(e.getName(), e.getDeviceType()));
            e.setName(e.getName());
            return e;
        }).collect(Collectors.toList());
    }
}
