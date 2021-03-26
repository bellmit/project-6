package com.miguan.reportview.service;


import com.miguan.reportview.vo.CostVo;

import java.util.List;

/**
 * 应用总成本服务接口
 *
 * @author zhongli
 * @since 2020-08-07 18:41:21
 * @description
 */
public interface IAppCostService {

    List<CostVo> getData(String startDate, String endDate);
}
