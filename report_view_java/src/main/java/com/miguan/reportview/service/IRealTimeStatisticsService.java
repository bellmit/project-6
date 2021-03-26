package com.miguan.reportview.service;

import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.vo.LdRealTimeStaVo;
import com.miguan.reportview.vo.RealTimeStaVo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
public interface IRealTimeStatisticsService {

    List<RealTimeStaVo> getData(List<String> dates, List<String> showTypes);

    Map<String, List<RealTimeStaVo>> getCheckereddata(List<String> dates, LocalDateTime date, List<String> showTypes);

    void staData(Map<String, Object> param);

    void staLdData(int startDh, int endDh, int showDh);

    Map<String, List<LdRealTimeStaVo>> getLdCheckereddata(List<String> dates, LocalDateTime date);

    List<LdRealTimeStaVo> getLdData(List<String> dates, List<String> showTypes);

    /**
     * 实时统计折线图（分钟纬度）
     * @param dates
     * @param showTypes
     * @return
     */
    List<DisassemblyChartDto> getMinuteData(List<String> dates, List<String> showTypes);

    /**
     * 来电实时统计折线图（分钟纬度）
     * @param dates
     * @param showTypes
     * @return
     */
    List<DisassemblyChartDto> getLdMinuteData(List<String> dates, List<String> showTypes);
}
