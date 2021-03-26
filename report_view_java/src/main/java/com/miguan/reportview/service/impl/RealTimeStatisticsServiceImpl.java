package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.common.utils.NumCalculationUtil;
import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.mapper.RpTotalDayMapper;
import com.miguan.reportview.mapper.RpTotalHourMapper;
import com.miguan.reportview.service.IRealTimeStatisticsService;
import com.miguan.reportview.service.RedisService;
import com.miguan.reportview.vo.LdRealTimeStaVo;
import com.miguan.reportview.vo.ParamsBuilder;
import com.miguan.reportview.vo.RealTimeStaVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tool.util.StringUtil;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
@Service
//@DS("clickhouse")
public class RealTimeStatisticsServiceImpl implements IRealTimeStatisticsService {
    @Resource
    private RpTotalHourMapper rpTotalHourMapper;
    @Resource
    private RpTotalDayMapper rpTotalDayMapper;
    @Resource
    private RedisService redisService;

    @Override
    public List<RealTimeStaVo> getData(List<String> dates, List<String> showTypes) {
        if (CollectionUtils.isEmpty(dates) || CollectionUtils.isEmpty(showTypes)) {
            throw new NullParameterException();
        }
        Map<String, Object> p = ParamsBuilder.builder(2).put("dates", dates).put("showTypes", showTypes).get();
        List<RealTimeStaVo> list = rpTotalHourMapper.getData(p);
        boolean isRate = showTypes.contains("14");
        list.forEach(e -> {
            e.setDh(e.getDh().substring(8));
            if(isRate){
                e.setShowValue(NumCalculationUtil.percentageFO(e.getShowValue()));
            }
        });
        return list;
    }

    /**
     * 实时统计折线图（分钟纬度）
     * @param dates
     * @param showTypes
     * @return
     */
    public List<DisassemblyChartDto> getMinuteData(List<String> dates, List<String> showTypes) {
        if (CollectionUtils.isEmpty(dates) || CollectionUtils.isEmpty(showTypes)) {
            throw new NullParameterException();
        }
        Map<String, Object> p = ParamsBuilder.builder(2).put("dates", dates).put("showTypes", showTypes).get();
        List<RealTimeStaVo> list = rpTotalHourMapper.getMinuteData(p);

        List<String> minute = dayMinuteList(); //获取每天分钟数，格式为： 12:50
        Map<String, List<RealTimeStaVo>> map = list.stream().collect(Collectors.groupingBy(RealTimeStaVo::getDd));
        //把没有数据的时间节点添加默认值
        list = map.entrySet().stream().flatMap(entry -> {
            List<RealTimeStaVo> listv = entry.getValue();
            minute.stream().forEach(e -> {
                Optional<RealTimeStaVo> op = listv.stream().filter(d -> d.getShowMinute().equals(e)).findFirst();
                if (!op.isPresent()) {
                    RealTimeStaVo addvo = new RealTimeStaVo();
                    addvo.setShowMinute(e);
                    addvo.setDd(entry.getKey());
                    addvo.setShowValue(Double.valueOf(0));
                    listv.add(addvo);
                }
            });
            return listv.stream();
        }).collect(Collectors.toList());

        boolean isRate = showTypes.contains("14");
        List<DisassemblyChartDto> rdata = list.stream().map(e -> {
            String type = DateUtil.yyyy_MM_dd().equals(e.getDd()) ? "今日" : DateUtil.yedyyyy_MM_dd().equals(e.getDd()) ? "昨日" : e.getDd().concat(" ");
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(e.getShowMinute());
            dto.setType(type);
            dto.setFormart(isRate ? "%" : "");
            dto.setValue(e.getShowValue() == null ? 0 : e.getShowValue().doubleValue());
            return dto;
        }).collect(Collectors.toList());
        rdata.sort(Comparator.comparing(DisassemblyChartDto::getDate));
        return rdata;
    }

    /**
     * 获取每天分钟数列表
     * @return
     */
    private List<String> dayMinuteList() {
        List<String> minuteList = new ArrayList<>();
        String dayMinutes = redisService.get("dayMinuteList");
        if(StringUtil.isNotBlank(dayMinutes)) {
            //有缓存，直接返回
            minuteList = Arrays.asList(dayMinutes.split(","));
        } else {
            String nowDate = DateUtil.yyyy_MM_dd();
            String startTime = nowDate + " 00:00:00";
            String endTime = nowDate + " 23:59:59";
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tool.util.DateUtil.valueOf(startTime, "yyyy-MM-dd HH:mm:ss"));
            Long endTimeMillis = tool.util.DateUtil.valueOf(endTime, "yyyy-MM-dd HH:mm:ss").getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            while(calendar.getTimeInMillis() <= endTimeMillis) {
                minuteList.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.MINUTE, 1);
            }
            redisService.set("dayMinuteList", String.join(",", minuteList), 60 * 60 *24);
        }
        return minuteList;
    }

    @Override
    public Map<String, List<RealTimeStaVo>> getCheckereddata(List<String> dates, LocalDateTime date, List<String> showTypes) {
        String df = date.format(DateUtil.YYYYMMDDHH_FORMATTER);
        String ydf = date.minusDays(1).format(DateUtil.YYYYMMDDHH_FORMATTER);
        Map<String, Object> p = ParamsBuilder.builder(6)
                .put("qdate", dates.get(0))
                .put("todayH", Integer.valueOf(df))
                .put("yesendDateH", Integer.valueOf(ydf))
                .put("showTypes", showTypes).get();
        List<RealTimeStaVo> list = rpTotalDayMapper.getCheckereddata(p);
        if (CollectionUtils.isEmpty(list)) {
            throw new ResultCheckException();
        }
        Map<String, List<RealTimeStaVo>> m = list.stream().collect(Collectors.groupingBy(RealTimeStaVo::getDd));
        List<RealTimeStaVo> list2 = rpTotalDayMapper.getRealTimeCheckereddata(p);
        m.put("real", list2);
        return m;
    }

    public Map<String, List<LdRealTimeStaVo>> getLdCheckereddata(List<String> dates, LocalDateTime date) {
        String df = date.format(DateUtil.YYYYMMDDHH_FORMATTER);
        String ydf = date.minusDays(1).format(DateUtil.YYYYMMDDHH_FORMATTER);
        Map<String, Object> p = ParamsBuilder.builder(6)
                .put("qdate", dates.get(0))
                .put("todayH", Integer.valueOf(df))
                .put("yesendDateH", Integer.valueOf(ydf))
                .get();
        List<LdRealTimeStaVo> list = rpTotalDayMapper.getLdCheckereddata(p);
        if (CollectionUtils.isEmpty(list)) {
            throw new ResultCheckException();
        }
        Map<String, List<LdRealTimeStaVo>> m = list.stream().collect(Collectors.groupingBy(LdRealTimeStaVo::getDd));
        List<LdRealTimeStaVo> list2 = rpTotalDayMapper.getLdRealTimeCheckereddata(p);
        m.put("real", list2);
        return m;
    }

    /**
     * 来电实时统计折线图（小时纬度）
     * @param dates
     * @param showTypes
     * @return
     */
    public List<LdRealTimeStaVo> getLdData(List<String> dates, List<String> showTypes) {
        if (CollectionUtils.isEmpty(dates) || CollectionUtils.isEmpty(showTypes)) {
            throw new NullParameterException();
        }
        Map<String, Object> p = ParamsBuilder.builder(2).put("dates", dates).put("showTypes", showTypes).get();
        List<LdRealTimeStaVo> list = rpTotalHourMapper.getLdData(p);
        list.forEach(e -> {
            e.setDh(e.getDh().substring(8));
        });
        return list;
    }

    /**
     * 来电实时统计折线图（分钟纬度）
     * @param dates
     * @param showTypes
     * @return
     */
    public List<DisassemblyChartDto> getLdMinuteData(List<String> dates, List<String> showTypes) {
        if (CollectionUtils.isEmpty(dates) || CollectionUtils.isEmpty(showTypes)) {
            throw new NullParameterException();
        }
        Map<String, Object> p = ParamsBuilder.builder(2).put("dates", dates).put("showTypes", showTypes).get();
        List<LdRealTimeStaVo> list = rpTotalHourMapper.getLdMinuteData(p);

        List<String> minute = dayMinuteList(); //获取每天分钟数，格式为： 12:50
        Map<String, List<LdRealTimeStaVo>> map = list.stream().collect(Collectors.groupingBy(LdRealTimeStaVo::getDd));
        //把没有数据的时间节点添加默认值
        list = map.entrySet().stream().flatMap(entry -> {
            List<LdRealTimeStaVo> listv = entry.getValue();
            minute.stream().forEach(e -> {
                Optional<LdRealTimeStaVo> op = listv.stream().filter(d -> d.getShowMinute().equals(e)).findFirst();
                if (!op.isPresent()) {
                    LdRealTimeStaVo addvo = new LdRealTimeStaVo();
                    addvo.setShowMinute(e);
                    addvo.setDd(entry.getKey());
                    addvo.setShowValue(Double.valueOf(0));
                    listv.add(addvo);
                }
            });
            return listv.stream();
        }).collect(Collectors.toList());

        boolean isRate = showTypes.contains("15") || showTypes.contains("16");
        List<DisassemblyChartDto> rdata = list.stream().map(e -> {
            String type = DateUtil.yyyy_MM_dd().equals(e.getDd()) ? "今日" : DateUtil.yedyyyy_MM_dd().equals(e.getDd()) ? "昨日" : e.getDd().concat(" ");
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(e.getShowMinute());
            dto.setType(type);
            dto.setFormart(isRate ? "%" : "");
            dto.setValue(e.getShowValue() == null ? 0 : e.getShowValue().doubleValue());
            return dto;
        }).collect(Collectors.toList());
        rdata.sort(Comparator.comparing(DisassemblyChartDto::getDate));
        return rdata;
    }

    @Override
    public void staData(Map<String, Object> param) {
        rpTotalHourMapper.deleteHourAccumulated((Integer)param.get("timeEndDh"));
        rpTotalHourMapper.staData(param);
    }

    public void staLdData(int startDh, int endDh, int showDh) {
        rpTotalHourMapper.deleteLdHourAccumulated(showDh);
        rpTotalHourMapper.staLdData(startDh, endDh, showDh);
    }
}
