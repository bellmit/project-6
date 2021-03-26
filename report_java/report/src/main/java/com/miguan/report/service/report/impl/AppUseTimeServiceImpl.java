package com.miguan.report.service.report.impl;

import com.miguan.report.common.enums.AppUseTimeEnum;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.entity.report.AppUseTime;
import com.miguan.report.mapper.AppUseTimeMapper;
import com.miguan.report.service.report.AppUseTimeService;
import com.miguan.report.vo.AppUseTimePandectVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tool.util.BigDecimalUtil;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 使用时长服务实现
 */
@Slf4j
@Service
public class AppUseTimeServiceImpl implements AppUseTimeService {

    @Resource
    private AppUseTimeMapper appUseTimeMapper;

    /**
     * 使用时长总览
     *
     * @return
     */
    @Override
    public AppUseTimePandectVo getUseTimePandect(Integer appType, Integer dataType) {
        String yesterdayString = DateUtil.yedyyyy_MM_dd();
        String beforeYesterdayString = DateUtil.subyyyy_MM_dd(LocalDate.now(), 2);
        String sevenDayAgoString = DateUtil.subyyyy_MM_dd(LocalDate.now(), -8);

        List<AppUseTime> appUseTimes = appUseTimeMapper.findByAppTypeAndDataTypeAndUseDayOrderByIdDesc(appType, dataType, yesterdayString, beforeYesterdayString, sevenDayAgoString);
        if (CollectionUtils.isEmpty(appUseTimes)) {
            return null;
        }

        // 昨日的数据
        AppUseTime yesterData = appUseTimes.get(0);
        // 前天的数据
        AppUseTime beforeYesterdayData = appUseTimes.size() > 1 ? appUseTimes.get(1) : null;
        // 上周的数据
        AppUseTime sevenDayAgoData = appUseTimes.size() > 2 ? appUseTimes.get(2) : null;

        // 环比
        double linkRelativeRatio = beforeYesterdayData == null ? 0.00d : BigDecimalUtil.div((yesterData.getUseTime() - sevenDayAgoData.getUseTime()), sevenDayAgoData.getUseTime(), 2) * 100;
        // 同比
        double sameRelativeRatio = beforeYesterdayData == null ? 0.00d : BigDecimalUtil.div((yesterData.getUseTime() - beforeYesterdayData.getUseTime()), beforeYesterdayData.getUseTime(), 2) * 100;
        AppUseTimePandectVo pandectVo = new AppUseTimePandectVo();
        pandectVo.setDay(yesterdayString);
        pandectVo.setTime(BigDecimalUtil.div(yesterData.getUseTime(), 60, 2) + "");
        pandectVo.setLinkRelativeRatio(String.valueOf(linkRelativeRatio));
        pandectVo.setSameRelativeRatio(String.valueOf(sameRelativeRatio));
        return pandectVo;
    }

    /**
     * 使用时长报表
     *
     * @return
     */
    @Override
    public List<LineChartDto> getUseTimeReportList(Integer appType, String startDateStr, String endDateStr, Integer timeTypeendDate) {
        String format = "yyyy-MM-dd hh:mm:ss";

        List<LineChartDto> reportList = null;
        switch (timeTypeendDate) {
            case 2:
                reportList = getWeekReportList(appType, startDateStr, endDateStr);
                break;
            case 3:
                reportList = getMonthReportList(appType, startDateStr, endDateStr);
                break;
            default:
                reportList = getDayReportList(appType, startDateStr, endDateStr);
        }

        // 因来电只有炫来电-android一个类型数据，所以去除汇总数据
        if(appType.intValue() == 2 && !CollectionUtils.isEmpty(reportList)){
            Iterator<LineChartDto> it = reportList.iterator();
            while (it.hasNext()){
                LineChartDto dto = it.next();
                if(StringUtils.equals("汇总", dto.getType())){
                    it.remove();
                }
            }
        }
        return reportList;
    }

    private List<LineChartDto> getMonthReportList(Integer appType, String beginDate, String endDate) {
        List<LineChartDto> voList = appUseTimeMapper.findMonthReportByAppTypeAndBetweenDay(appType, beginDate, endDate);
        if (!CollectionUtils.isEmpty(voList)) {
            for (LineChartDto dto : voList) {
                dto.setValue(BigDecimalUtil.div(dto.getValue(), 60, 2));
            }
        }
        return voList;
    }

    private List<LineChartDto> getWeekReportList(Integer appType, String beginDate, String endDate) {
        List<LineChartDto> voList = appUseTimeMapper.findWeekReportByAppTypeAndBetweenDay(appType, beginDate, endDate);
        if (!CollectionUtils.isEmpty(voList)) {
            for (LineChartDto dto : voList) {
                String format = "yyyy-MM-dd";
                Date startDayOfWeek = DateUtil.getFirstDayOfWeek(DateUtil.strToDate(dto.getDate(), format));
                Date endDayOfWeek = DateUtil.getLastDayOfWeek(DateUtil.strToDate(dto.getDate(), format));
                String date = DateUtil.dateToStr(startDayOfWeek, format) + "至" + DateUtil.dateToStr(endDayOfWeek, format);
                dto.setDate(date);
                dto.setValue(BigDecimalUtil.div(dto.getValue(), 60, 2));
            }
        }
        return voList;
    }

    private List<LineChartDto> getDayReportList(Integer appType, String beginDate, String endDate) {
        List<AppUseTime> appUseTimes = appUseTimeMapper.findDayReportByAppTypeAndBetweenDay(appType, beginDate, endDate);
        if (CollectionUtils.isEmpty(appUseTimes)) {
            return null;
        }

        List<LineChartDto> voList = new ArrayList<LineChartDto>();
        for (AppUseTime appUseTime : appUseTimes) {
            LineChartDto vo = new LineChartDto();
            vo.setDate(appUseTime.getUseDay());
            vo.setType(AppUseTimeEnum.getNameByAppTypeAndeDataType(appUseTime.getAppType(), appUseTime.getDataType()));
            vo.setValue(BigDecimalUtil.div(appUseTime.getUseTime(), 60, 2));
            voList.add(vo);
        }
        return voList;
    }
}
