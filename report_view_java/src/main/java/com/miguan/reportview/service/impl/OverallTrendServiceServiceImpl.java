package com.miguan.reportview.service.impl;

import com.google.common.collect.Lists;
import com.miguan.reportview.common.utils.NumCalculationUtil;
import com.miguan.reportview.mapper.RpTotalDayMapper;
import com.miguan.reportview.service.IAppCostService;
import com.miguan.reportview.service.IBannerDataTotalNameService;
import com.miguan.reportview.service.IOverallTrendService;
import com.miguan.reportview.service.IUserKeepService;
import com.miguan.reportview.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
import static com.miguan.reportview.common.utils.NumCalculationUtil.zero;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Service
//@DS("clickhouse")
public class OverallTrendServiceServiceImpl implements IOverallTrendService {

    @Resource
    private RpTotalDayMapper rpTotalDayMapper;

    @Autowired
    private IBannerDataTotalNameService bannerDataTotalNameService;
    @Autowired
    private IAppCostService appCostService;
    @Autowired
    private IUserKeepService userKeepService;

    @Override
    public List<OverallTrendVo> getData(String startDate, String endDate, List<String> showTypes) {
        Map<String, Object> params = ParamsBuilder.builder(3).put("startDate", startDate)
                .put("endDate", endDate)
                .put("showTypes", showTypes).get();
        List<OverallTrendVo> list = rpTotalDayMapper.getOverallTrendData(params);
        //4=日活用户价值
        if (!isEmpty(list) && !isEmpty(showTypes) && showTypes.contains("4")) {
            List<AdTotalVo> list2 = bannerDataTotalNameService.getData(startDate, endDate, 1);
            if (!isEmpty(list2)) {
                list.forEach(e -> {
                    if (e.getUser() == null || e.getUser().doubleValue() == 0) {
                        e.setDuv(zero);
                        return;
                    }
                    Optional<AdTotalVo> op = list2.stream().filter(d -> d.getDd().equals(e.getDate())).findFirst();
                    if (op.isPresent()) {
                        e.setDuv(divide(op.get().getRevenue(), e.getUser(), false));
                    }
                });
            }
        }
        //5=日活客单成本
        if (!isEmpty(list) && !isEmpty(showTypes) && showTypes.contains("5")) {
            List<CostVo> list2 = appCostService.getData(startDate, endDate);
            if (!isEmpty(list2)) {
                list.forEach(e -> {
                    if (e.getUser() == null || e.getUser().doubleValue() == 0) {
                        e.setDuc(zero);
                        return;
                    }
                    Optional<CostVo> op = list2.stream().filter(d -> d.getDate().equals(e.getDate())).findFirst();
                    if (op.isPresent()) {
                        e.setDuc(divide(op.get().getCost(), e.getUser(), false));
                    }
                });
            }
        }
        //6=新用户次留率7=活跃用户次留率
        if (!isEmpty(list) && !isEmpty(showTypes) && CollectionUtils.containsAny(showTypes, "6")) {
            boolean isNew = true;
            List<UserKeepVo> list2 = userKeepService.getData(Lists.newArrayList("1"),
                    isNew,
                    null, null, null, null,
                    1,startDate, endDate);
            if (!isEmpty(list2)) {
                list.forEach(e -> {
                    if (e.getNewUser() == null || e.getNewUser().doubleValue() == 0) {
                        e.setNukeep(zero);
                        return;
                    }
                    Optional<UserKeepVo> op = list2.stream().filter(d -> d.getDd().equals(e.getDate().replaceAll("-", ""))).findFirst();
                    if (op.isPresent()) {
                        Double tvO = op.get().getShowValue();
                        double tv = tvO == null ? 0 : tvO.doubleValue();
                        e.setNukeep(divide(tv, e.getNewUser().doubleValue(), true));
                    }
                });
            }
        }
        if (!isEmpty(list) && !isEmpty(showTypes) && CollectionUtils.containsAny(showTypes, "7")) {
            List<UserKeepVo> list2 = userKeepService.getData(Lists.newArrayList("1"),
                    null,
                    null, null, null, null,
                    1,startDate, endDate);
            if (!isEmpty(list2)) {
                list.forEach(e -> {
                    if (e.getUser() == null || e.getUser().doubleValue() == 0) {
                        e.setDukeep(zero);
                        return;
                    }
                    Optional<UserKeepVo> op = list2.stream().filter(d -> d.getDd().equals(e.getDate().replaceAll("-", ""))).findFirst();
                    if (op.isPresent()) {
                        Double tvO = op.get().getShowValue();
                        double tv = tvO == null ? 0 : tvO.doubleValue();
                        e.setDukeep(divide(tv, e.getUser().doubleValue(), true));
                    }
                });
            }
        }
        list.forEach(this::changeVo);
        return list;
    }

    private void changeVo(OverallTrendVo vo) {
        vo.setPerPlayTime(vo.getPerPlayTime() == null ? null : vo.getPerPlayTime().doubleValue() / 60000);
        vo.setDuAppTime(vo.getDuAppTime() == null ? null : vo.getDuAppTime().doubleValue() / 60000);
        vo.setDuvpc(NumCalculationUtil.roundHalfUpDoubleFO(vo.getDuvpc()));
        vo.setAdClickRate(NumCalculationUtil.percentageFO(vo.getAdClickRate()));
        vo.setDuAdshow(NumCalculationUtil.roundHalfUpDoubleFO(vo.getDuAdshow()));
        vo.setDuAdClick(NumCalculationUtil.roundHalfUpDoubleFO(vo.getDuAdClick()));
        vo.setDuAppTime(NumCalculationUtil.roundHalfUpDoubleFO(vo.getDuAppTime()));
        vo.setPerPlayTime(NumCalculationUtil.roundHalfUpDoubleFO(vo.getPerPlayTime()));
    }

    @Override
    public List<OverallTrendVo> getData(String startDate, String endDate) {
        List<String> showTypes = IntStream.range(1, 18).mapToObj(Integer::toString).collect(Collectors.toList());
        return getData(startDate, endDate, showTypes);
    }

    /**
     * 来电折线图数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param showTypes
     * @return
     */
    public List<LdOverallTrendVo> getLdData(String startDate, String endDate, List<String> showTypes) {
        Map<String, Object> params = ParamsBuilder.builder(3).put("startDate", startDate)
                .put("endDate", endDate)
                .put("showTypes", showTypes).get();
        List<LdOverallTrendVo> list = rpTotalDayMapper.getLdOverallTrendData(params);
        return list;
    }

    /**
     * 来电表格数据
     * @param startDate
     * @param endDate
     * @return
     */
    public List<LdOverallTrendVo> getLdData(String startDate, String endDate) {
        List<String> showTypes = IntStream.range(1, 19).mapToObj(Integer::toString).collect(Collectors.toList());
        return getLdData(startDate, endDate, showTypes);
    }
}
