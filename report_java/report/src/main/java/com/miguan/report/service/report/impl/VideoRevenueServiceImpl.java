package com.miguan.report.service.report.impl;

import com.miguan.report.common.constant.RevenueConstant;
import com.miguan.report.common.util.NumCalculationUtil;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.dto.RevBarDataDto;
import com.miguan.report.dto.RevBarDto;
import com.miguan.report.dto.RevMidBarDto;
import com.miguan.report.mapper.VideoRevenueMapper;
import com.miguan.report.service.report.VideoRevenueService;
import com.miguan.report.vo.RevenuePlatformVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description 视频广告数据总览-> 总营收报表service
 * @Author zhangbinglin
 * @Date 2020/6/17 14:10
 **/
@Service
public class VideoRevenueServiceImpl implements VideoRevenueService {

    @Resource
    private VideoRevenueMapper videoRevenueMapper;

    /**
     * 统计营收数据
     *
     * @param startDate 统计开始时间
     * @param endDate   统计结束时间
     * @param appType   app类型：1=西柚视频,2=炫来电
     * @param type      统计类型：1=按app统计,2=按平台统计
     * @param totalName 广告位,为空的话就是统计全部广告位
     * @param timeType  时间类型：1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    public List<LineChartDto> countLineRevenueChart(String startDate, String endDate, Integer appType, Integer type, String totalName, Integer timeType) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appType", appType);
        params.put("totalName", totalName);
        params.put("timeType", timeType);
        params.put("type", type);
        return videoRevenueMapper.countLineRevenueChart(params);
    }

    /**
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param appType  app类型：1=西柚视频,2=炫来电
     * @param timeType 时间类型：1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    public RevBarDto countBarRevenueChar(String startDate, String endDate, Integer appType, Integer timeType) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appType", appType);
        params.put("timeType", timeType);
        List<RevenuePlatformVo> baseList = videoRevenueMapper.countBarRevenueChar(params);;

        //如果某天数据为空，则需要填充0，否则前端展示会有问题
        List<RevenuePlatformVo> allItemList = videoRevenueMapper.findAllItem(params);
        List<RevenuePlatformVo> finishList = new ArrayList<>();
        for(RevenuePlatformVo rpv : allItemList) {
            int index = baseList.indexOf(rpv);
            if(index >= 0) {
                finishList.add(baseList.get(index));
            } else {
                rpv.setValue(0D);
                finishList.add(rpv);
            }
        }
        return getRevenuseBarData(finishList);
    }

    private RevBarDto getRevenuseBarData(List<RevenuePlatformVo> baseList) {
        Map<String, Double> dayTotalResMap = new HashMap<>();  //单个app一天内总收益
        for(RevenuePlatformVo vo : baseList) {
            String mapKey = vo.getDate() + "_" + vo.getAppName();
            Double value = vo.getValue(); //单个收益
            Double totalRes = (dayTotalResMap.get(mapKey) != null ? dayTotalResMap.get(mapKey) : 0D);
            totalRes = totalRes + value;
            dayTotalResMap.put(mapKey, totalRes);
        }

        LinkedHashMap<String, RevMidBarDto> midBarMap = new LinkedHashMap<>();
        LinkedHashSet<String> dates = new LinkedHashSet<>();  //时间集合
        for(RevenuePlatformVo vo : baseList) {
            dates.add(vo.getDate());
            String mapKey = vo.getPlatForm() + "_" + vo.getAppName();
            RevMidBarDto midBarDto = (midBarMap.get(mapKey) != null ? midBarMap.get(mapKey) : new RevMidBarDto());
            midBarDto.setName(vo.getPlatForm());
            midBarDto.setStack(vo.getAppName());

            List<RevBarDataDto> dataList = (midBarDto.getData() != null ? midBarDto.getData() : new ArrayList<>());
            RevBarDataDto barDataDto = new RevBarDataDto();
            barDataDto.setName(vo.getAppName());
            barDataDto.setValue(vo.getValue());
            String totalResKey = vo.getDate() + "_" + vo.getAppName();
            Double totalRes = dayTotalResMap.get(totalResKey);
            double rat = (totalRes == 0 ? 0d : (vo.getValue() / totalRes) * 100);
            barDataDto.setZb(NumCalculationUtil.decimalPoint(rat) + "%");
            dataList.add(barDataDto);

            midBarDto.setData(dataList);
            midBarMap.put(mapKey, midBarDto);
        }

        List<RevMidBarDto> midBarList = new ArrayList<>();    //柱状图数据集合
        for(RevMidBarDto midBarDto : midBarMap.values()) {
            midBarList.add(midBarDto);
        }

        RevBarDto revBarDto = new RevBarDto();
        revBarDto.setDates(dates);
        revBarDto.setBarDates(midBarList);
        return revBarDto;
    }
}
