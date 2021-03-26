package com.miguan.report.service.third;

import com.alibaba.fastjson.JSONArray;

/**
 * @Description 第三方广点通Service
 * @Author zhangbinglin
 * @Date 2020/7/13 11:33
 **/
public interface GdtService {
    /**
     * 查询广点通报表数据(包含点击数，展现数，营收等数据)
     * @param startDate 开始日期，yyyyMMdd，时间跨度不超过2天
     * @param endDate 截至日期，yyyyMMdd，时间跨度不超过2天
     * @return
     */
    JSONArray getReportDatas(String startDate, String endDate);
}
