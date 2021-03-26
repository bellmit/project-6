package com.miguan.reportview.service;

import com.miguan.reportview.vo.LdOverallTrendVo;
import com.miguan.reportview.vo.OverallTrendVo;
import com.miguan.reportview.vo.ParamsBuilder;

import java.util.List;
import java.util.Map;

/**
 * @author zhongli
 * @date 2020-08-04 
 *
 */
public interface IOverallTrendService {

    List<OverallTrendVo> getData(String startDate, String endDate,List<String> showTypes);

    List<OverallTrendVo> getData(String startDate, String endDate);

    List<LdOverallTrendVo> getLdData(String startDate, String endDate, List<String> showTypes);

    List<LdOverallTrendVo> getLdData(String startDate, String endDate);
}
