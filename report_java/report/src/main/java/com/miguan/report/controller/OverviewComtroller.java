package com.miguan.report.controller;

import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.dto.AdStaResDto;
import com.miguan.report.dto.AllCaStatDto;
import com.miguan.report.dto.CostAndMomYoyDto;
import com.miguan.report.dto.DisassemblyChartDto;
import com.miguan.report.dto.ResponseDto;
import com.miguan.report.service.report.OverviewService;
import com.miguan.report.vo.AdStaExtVo;
import com.miguan.report.vo.AdStaVo;
import com.miguan.report.vo.CaStatNumVoDetail;
import com.miguan.report.vo.CostStaVo;
import com.miguan.report.vo.DisaChartVo;
import com.miguan.report.vo.PerCapitaCostVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import tool.util.BigDecimalUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.miguan.report.common.constant.CommonConstant.SUM_NAME;
import static com.miguan.report.common.util.NumCalculationUtil.calMomOrYoy;
import static com.miguan.report.common.util.NumCalculationUtil.calMomOrYoyDouble;
import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUp;
import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUpDouble;

/**概览页
 * @author zhongli
 * @date 2020-06-17 
 *
 */
@Slf4j
public abstract class OverviewComtroller extends BaseController {
    abstract int appType();

    @Resource
    private OverviewService overviewService;

    @ApiOperation(value = "小方格统计数据")
    @PostMapping(value = "/data/load/head")
    @ApiResponse(code = 200, message = "", response = AllCaStatDto.class, responseContainer = "List")
    public ResponseDto<List<AllCaStatDto>> initload() {
        LocalDate nowDate = LocalDate.now().minusDays(1);
        Map allCaStat = overviewService.loadAllCaStat(nowDate, this.appType());
        if (allCaStat == null) {
            return successForNotData();
        }
        String date = DateUtil.yyyy_MM_dd(nowDate);
        List<AllCaStatDto> result = new ArrayList<>();

        bulidAllCaStatData(2, "总营收", date, "revenue", allCaStat, result);
        bulidAllCaStatData(2, "日活", date, "active", allCaStat, result);
        bulidAllCaStatData(2, "总CPM", date, "cpm", allCaStat, result);
        bulidAllCaStatData(2, "人均展现", date, "pre_show_num", allCaStat, result);
        bulidAllCaStatData(4, "日活均价值", date, "active_value", allCaStat, result);
        bulidAllCaStatData(2, "用户日使用时长", date, "useTime", allCaStat, result);
        return success(result);
    }

    /**
     * 构建前端数据模型
     * @param type
     * @param date
     * @param key
     * @param allCaStat
     * @param result
     */
    private void bulidAllCaStatData(int scale, String type, String date, String key, Map allCaStat, List<AllCaStatDto> result) {

        String momKey = "mom".concat(key);
        String yoyKey = "yoy".concat(key);

        BigDecimal firBD = convertBD(allCaStat.get(key));
        //环比数据值
        BigDecimal momBD = convertBD(allCaStat.get(momKey));
        //周同比数据值
        BigDecimal yoyBD = convertBD(allCaStat.get(yoyKey));
        //四舍五入显示值
        String total = roundHalfUp(scale, firBD);
        //环比计算
        String monNun = calMomOrYoy(firBD, momBD);
        //同比计算
        String yoyNum = calMomOrYoy(firBD, yoyBD);
        result.add(new AllCaStatDto(type, date, total, monNun, yoyNum));
    }


    /**
     * 来自持久化数据进行类型转换
     * @param DSObj
     * @return
     */
    private BigDecimal convertBD(Object DSObj) {
        if (DSObj == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal dataBD;
        if (DSObj instanceof BigDecimal) {
            dataBD = (BigDecimal) DSObj;
        } else if (DSObj instanceof Double) {
            dataBD = BigDecimal.valueOf((Double) DSObj);
        } else {
            dataBD = new BigDecimal(DSObj.toString());
        }
        return dataBD;
    }


    @ApiOperation(value = "小方格数据详情(拆线图)")
    @PostMapping(value = "/data/load/head/detail")
    @ApiResponse(code = 200, message = "", response = DisassemblyChartDto.class, responseContainer = "List")
    public ResponseDto<List<DisassemblyChartDto>> initload2(
            @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "数据类型：1=总营收 2=日活 3=总CPM 4=人均展示 5=日活均值 6=用户日使用时长", required = true) Integer dataType) {
        List<CaStatNumVoDetail> allCaStatDetail = overviewService.loadCatStatDetail(dataType, startDate, endDate, this.appType());
        if (allCaStatDetail == null) {
            return successForNotData();
        }
        List<DisassemblyChartDto> totalDataDetail = null;
        if (dataType == 3 || dataType == 4 || dataType == 5) {
            Map<String, List<CaStatNumVoDetail>> totalData = allCaStatDetail.stream().collect(Collectors.groupingBy(CaStatNumVoDetail::getDate));
            if (appType() == 1) {
                totalDataDetail = totalData.entrySet().stream()
                        .map(d -> {
                            double sumMol = d.getValue().stream().mapToDouble(CaStatNumVoDetail::getSumMol).sum();
                            double sumDem = d.getValue().stream().mapToDouble(CaStatNumVoDetail::getSumDem).sum();
                            //dataType == 3计算cpm=汇总营收/汇总展现量*1000
                            double dataValue = sumDem == 0 ? 0 :
                                    dataType == 3 ? roundHalfUpDouble(sumMol / sumDem * 1000) :
                                            roundHalfUpDouble(dataType == 5 ? 4 : 2, sumMol / sumDem);
                            return new DisassemblyChartDto(d.getKey(), SUM_NAME, dataValue);
                        })
                        .collect(Collectors.toList());
            } else {
                totalDataDetail = new ArrayList<>();
            }
            List<DisassemblyChartDto> partRD = allCaStatDetail.stream().map(v -> new DisassemblyChartDto(v.getDate(), v.getAppName(), v.getDataValue()))
                    .collect(Collectors.toList());
            totalDataDetail.addAll(partRD);
        } else {
            Map<String, DoubleSummaryStatistics> totalData = allCaStatDetail.stream().collect(Collectors.groupingBy(CaStatNumVoDetail::getDate, Collectors.summarizingDouble(CaStatNumVoDetail::getDataValue)));
            if (appType() == 1) {
                totalDataDetail = totalData.entrySet().stream()
                        //计算用户时长
                        .map(d -> new DisassemblyChartDto(d.getKey(), SUM_NAME, dataType != 6 ? d.getValue().getSum() : d.getValue().getSum() / 3))
                        .collect(Collectors.toList());
            } else {
                totalDataDetail = new ArrayList<>();
            }
            List<DisassemblyChartDto> partRD = allCaStatDetail.stream().map(v -> new DisassemblyChartDto(v.getDate(), v.getAppName(), v.getDataValue()))
                    .collect(Collectors.toList());
            totalDataDetail.addAll(partRD);
        }
        sort(totalDataDetail);
        return success(totalDataDetail);
    }

    @ApiOperation(value = "总成本(折线图)")
    @PostMapping(value = "/data/load/cost")
    @ApiResponse(code = 200, message = "", response = CostAndMomYoyDto.class)
    public ResponseDto<CostAndMomYoyDto> initload3(
            @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate
    ) {
        CostStaVo costData = overviewService.loadCost(appType());
        if (costData == null) {
            return successForNotData();
        }
        CostAndMomYoyDto costAndMomYoyDto = new CostAndMomYoyDto();
        costAndMomYoyDto.setDate(costData.getDate());
        costAndMomYoyDto.setDayOfWeek(costData.getDataOfWeek());
        costAndMomYoyDto.setValueOfDay(roundHalfUpDouble(costData.getCost()));
        costAndMomYoyDto.setMom(calMomOrYoyDouble(costData.getCost(), costData.getMomcost()));
        costAndMomYoyDto.setYoy(calMomOrYoyDouble(costData.getCost(), costData.getYoycost()));
        List<DisaChartVo> chartVos = overviewService.loadCostDataChart(startDate, endDate, appType());
        if (chartVos != null) {
            //计算合计
            double totalValue = chartVos.stream().mapToDouble(DisaChartVo::getDataValue).sum();
            costAndMomYoyDto.setTotalValue(BigDecimalUtil.round(totalValue));
            Map<String, DoubleSummaryStatistics> totalLists = chartVos.stream()
                    .collect(Collectors.groupingBy(DisaChartVo::getDate,
                            Collectors.summarizingDouble(DisaChartVo::getDataValue)));
            List<DisassemblyChartDto> totalData = null;
            if (appType() == 1) {
                totalData = totalLists.entrySet().stream().map(et -> {
                    DisassemblyChartDto dto = new DisassemblyChartDto();
                    dto.setDate(et.getKey());
                    dto.setType(SUM_NAME);
                    dto.setValue(et.getValue().getSum());
                    return dto;
                }).collect(Collectors.toList());
            } else {
                totalData = new ArrayList<>();
            }
            int count = totalLists.size();
            //计算均值
            double avg = BigDecimal.valueOf(totalValue)
                    .divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
            costAndMomYoyDto.setAveValue(avg);
            //对象类型转换
            List<DisassemblyChartDto> vdata = chartVos.stream()
                    .map(cv -> new DisassemblyChartDto(cv.getDate(), cv.getName(), cv.getDataValue()))
                    .collect(Collectors.toList());
            totalData.addAll(vdata);
            //排序
            sort(totalData);
            costAndMomYoyDto.setChartData(totalData);
        }
        return success(costAndMomYoyDto);
    }

    @ApiOperation(value = "人均成本(折线图)")
    @PostMapping(value = "/data/load/percost")
    @ApiResponse(code = 200, message = "", response = CostAndMomYoyDto.class)
    public ResponseDto<CostAndMomYoyDto> initload4(
            @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "数据类型：1=新增用户 2=日活用户", required = true) Integer dataType
    ) {
        CostStaVo costData = overviewService.loadPerCapitaCost(appType(), dataType);
        if (costData == null) {
            return successForNotData();
        }
        CostAndMomYoyDto costAndMomYoyDto = new CostAndMomYoyDto();
        costAndMomYoyDto.setDate(costData.getDate());
        costAndMomYoyDto.setDayOfWeek(costData.getDataOfWeek());
        costAndMomYoyDto.setValueOfDay(roundHalfUpDouble(costData.getCost()));
        costAndMomYoyDto.setMom(calMomOrYoyDouble(costData.getCost(), costData.getMomcost()));
        costAndMomYoyDto.setYoy(calMomOrYoyDouble(costData.getCost(), costData.getYoycost()));

        List<PerCapitaCostVo> chartVos = overviewService.loadPerCapitaCostChart(dataType, startDate, endDate, appType());
        if (chartVos != null) {
            //计算合计
            //总成本
            double totalCost = chartVos.stream().mapToDouble(e -> e.getCost()).sum();

            double calValue = dataType == 1 ? chartVos.stream().mapToDouble(e -> e.getNewUsers()).sum()
                    : chartVos.stream().mapToDouble(e -> e.getActive()).sum();
            costAndMomYoyDto.setTotalValue(calValue == 0 ? 0 : roundHalfUpDouble(totalCost / calValue));

            //按日期分组
            Map<String, List<PerCapitaCostVo>> totalLists = chartVos.stream()
                    .collect(Collectors.groupingBy(DisaChartVo::getDate));
            List<DisassemblyChartDto> totalData = null;
            if (appType() == 1) {
                //汇总组装
                totalData = totalLists.entrySet().stream().map(et -> {
                    DisassemblyChartDto dto = new DisassemblyChartDto();
                    dto.setDate(et.getKey());
                    dto.setType(SUM_NAME);

                    double tc = et.getValue().stream().mapToDouble(e -> e.getCost()).sum();

                    double cv = dataType == 1 ? et.getValue().stream().mapToDouble(e -> e.getNewUsers()).sum()
                            : et.getValue().stream().mapToDouble(e -> e.getActive()).sum();
                    dto.setValue(cv == 0 ? 0 : roundHalfUpDouble(tc / cv));
                    return dto;
                }).collect(Collectors.toList());
            } else {
                totalData = new ArrayList<>();
            }
            //计算均值
            costAndMomYoyDto.setAveValue(costAndMomYoyDto.getTotalValue());
            //对象类型转换
            List<DisassemblyChartDto> vdata = chartVos.stream()
                    .map(cv -> new DisassemblyChartDto(cv.getDate(), cv.getName(), roundHalfUpDouble(cv.getDataValue())))
                    .collect(Collectors.toList());
            totalData.addAll(vdata);
            //排序
            sort(totalData);
            costAndMomYoyDto.setChartData(totalData);
        }
        return success(costAndMomYoyDto);
    }


    @ApiOperation(value = "广告位数据对比(柱状图)")
    @PostMapping(value = "/data/load/adcompla")
    @ApiResponse(code = 200, message = "", response = AdStaResDto.class)
    public ResponseDto<AdStaResDto> initload5(
            @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String date,
            @ApiParam(value = "数据类型：1=CPM 2=人均展示 3=错误率 4=营收 5=日活均值", required = true) Integer dataType) {
        List<AdStaExtVo> revos;
        String momdate = DateUtil.yyyy_MM_dd(DateUtil.yyyy_MM_dd(date).minusDays(1));
        if (dataType == 3) {
            List<AdStaVo> adStaVos = overviewService.loadAdStaExt(dataType, date, this.appType());
            List<AdStaVo> momadStaVos = overviewService.loadAdStaExt(dataType, momdate, this.appType());
            revos = mergeAdSta(adStaVos, momadStaVos);
        } else {
            List<AdStaVo> adStaVos = overviewService.loadAdSta(dataType, date, this.appType());
            List<AdStaVo> momadStaVos = overviewService.loadAdSta(dataType, momdate, this.appType());
            revos = mergeAdSta(adStaVos, momadStaVos);
        }
        if (revos == null) {
            log.warn("广告位数据对比(柱状图).");
            return successForNotData();
        }
        //获取所有广告位名称集，改变应用名称
        List<String> listName;
        if (appType() == 1) {
            listName = Arrays.asList("启动页", "首页列表", "锁屏原生", "视频底部banner广告", "视频开始广告", "视频中间广告", "视频结束广告",
                    "首页视频详情", "搜索页广告", "搜索结果广告", "小视频列表", "小视频详情");
        } else {
            listName = Arrays.asList("启动页", "来电列表", "详情页间隔广告位", "锁屏广告", "来电分类解锁", "来电详情-退出-激励", "来电详情-解锁",
                    "退出页", "来电详情-弹窗", "来电强制解锁");
        }
        //使用应用名称进行分组
        Map<String, List<AdStaExtVo>> groupData = revos.stream().filter(e -> {
            //过滤不在固定列表中显示的广告位名称
            return listName.stream().filter(d -> d.equals(e.getAdSpace()) || e.getAdSpace().contains(d)).count() > 0;
        }).collect(Collectors.groupingBy(AdStaExtVo::getAppName));
        //应前端要求，这里会进行硬编码
        if (appType() == 1) {
            Arrays.asList("茜柚Android", "果果Android").forEach(appName -> {
                if (!groupData.containsKey(appName)) {
                    groupData.put(appName, Arrays.asList(new AdStaExtVo(listName.get(0), appName, 1, "0", 0)));
                }
            });
        } else {
            Arrays.asList("炫来电Android").forEach(appName -> {
                if (!groupData.containsKey(appName)) {
                    groupData.put(appName, Arrays.asList(new AdStaExtVo(listName.get(0), appName, 1, "0", 0)));
                }
            });
        }
        //将分组后的数据进行扁平化处理
        List<Map<String, Object>> rlist = groupData.entrySet().stream().map(et -> {
            List<AdStaExtVo> adlist = new ArrayList<>();
            int clientId = et.getValue().get(0).getClientId();
            for (int i = 0; i < listName.size(); i++) {
                String s = listName.get(i);
                Optional<AdStaExtVo> op = et.getValue().stream().filter(e -> s.equals(e.getAdSpace()) || e.getAdSpace().contains(s)).findFirst();
                AdStaExtVo vo = op.isPresent() ? op.get() : new AdStaExtVo(s, et.getKey(), clientId, "0", 0);
                adlist.add(vo);
            }
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(3);
            map.put("name", et.getKey());
            map.put("data", adlist);
            return map;
        }).collect(Collectors.toList());

        return success(new AdStaResDto(listName, rlist));
    }


    /**
     * 合并当天类型和前一天的类型，如：当天某个类型不存在，昨天有此类型则要进行聚合
     * @param data1
     * @param data2
     * @return
     */
    private List<AdStaExtVo> mergeAdSta(List<AdStaVo> data1, List<AdStaVo> data2) {
        if (data1 == null && data2 == null) {
            return null;
        }
        List<AdStaVo> newVos = new ArrayList<>();
        List<AdStaExtVo> resVos = new ArrayList<>();
        //查询前一天的广告类跟今天对比，如果今天有不包含昨天的类要进行合并
        if (data2 != null && data1 != null) {
            data2.forEach(v -> {
                //查询当天是否有此类
                Optional<AdStaVo> op = data1.stream().filter(v1 -> v1.getAdSpace().equals(v.getAdSpace()) && v1.getAppName().equals(v.getAppName())).findFirst();
                if (!op.isPresent()) {
                    //如果前一天有此类，当天没有此类则要加入此类进行展示
                    newVos.add(new AdStaVo(v.getDeviceType(), v.getAdSpace(), v.getAppName(), 0));
                }
            });
        }

        if (data1 == null) {
            data2.forEach(v -> {
                //如果前一天有此类，当天没有此类则环比-100%，也就是降幅100%
                resVos.add(new AdStaExtVo(v.getAdSpace(), v.getAppName(), v.getDeviceType(), "-1", 0));
            });
            return resVos;
        }

        newVos.addAll(data1);
        //进行环比计算
        newVos.forEach(v -> {
            //查询前一天是否有此类
            Optional<AdStaVo> op = data2 == null ? Optional.empty() :
                    data2.stream().filter(v2 -> v2.getAdSpace().equals(v.getAdSpace()) && v2.getAppName().equals(v.getAppName())).findFirst();
            if (!op.isPresent()) {
                //前一天data2没有此类，则环比100%
                resVos.add(new AdStaExtVo(v.getAdSpace(), v.getAppName(), v.getDeviceType(), "1", v.getDataValue()));
            } else if (v.getDataValue() == 0) {
                //如果前一天data2有此类，当天没有此类则环比-100%，也就是降幅100%
                resVos.add(new AdStaExtVo(v.getAdSpace(), v.getAppName(), v.getDeviceType(), "-1", v.getDataValue()));
            } else {
                //前一天有此类data2 当前天也有此类v,进行环比计算
                String mom = calMomOrYoy(v.getDataValue(), op.get().getDataValue());
                resVos.add(new AdStaExtVo(v.getAdSpace(), v.getAppName(), v.getDeviceType(), mom, v.getDataValue()));
            }
        });
        return resVos;
    }
}
