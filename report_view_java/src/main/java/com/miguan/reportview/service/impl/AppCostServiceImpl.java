package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.mapper.AppCostMapper;
import com.miguan.reportview.service.IAppCostService;
import com.miguan.reportview.vo.CostVo;
import com.miguan.reportview.vo.ParamsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 应用总成本服务接口实现
 *
 * @author zhongli
 * @since 2020-08-07 18:41:21
 * @description
 */
@RequiredArgsConstructor
@Service
@DS("report-db")
public class AppCostServiceImpl implements IAppCostService {
    private final AppCostMapper appCostMapper;

    @Override
    public List<CostVo> getData(String startDate, String endDate) {
        Map<String, Object> params = ParamsBuilder.builder(2)
                .put("startDate", startDate)
                .put("endDate", endDate)
                .get();
        return appCostMapper.getCostForDate(params);
    }
}