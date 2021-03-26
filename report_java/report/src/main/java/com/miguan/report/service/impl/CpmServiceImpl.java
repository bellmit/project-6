package com.miguan.report.service.impl;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.mapper.CpmMapper;
import com.miguan.report.service.CpmService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 千展收益（CPM）service
 * @Author zhangbinglin
 * @Date 2020/6/17 14:10
 **/
@Service
public class CpmServiceImpl implements CpmService {

    @Resource
    private CpmMapper cpmMapper;

    /**
     * 统计千展收益（CPM）数据
     *
     * @param startDate 统计开始时间
     * @param endDate   统计结束时间
     * @param appType   app类型：1=西柚视频,2=炫来电
     * @param type      统计类型：1=按app统计,2=按平台统计
     * @param totalName 广告位,为空的话就是统计全部广告位
     * @param timeType  时间类型：1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    public List<LineChartDto> countLineCpmChart(String startDate, String endDate, Integer appType, Integer type, String totalName, Integer timeType) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("appType", appType);
        params.put("totalName", totalName);
        params.put("timeType", timeType);
        params.put("type", type);
        return cpmMapper.countLineCpmChart(params);
    }


}
