package com.miguan.report.service.third;

import com.alibaba.fastjson.JSONArray;

/**
 * @Description 第三方快手Service
 * @Author zhangbinglin
 * @Date 2020/7/13 11:33
 **/
public interface KuaiShouService {

    /**
     * 获取分成数据（日级别）
     * @param date
     * @return
     */
    JSONArray getDailyShare(String date);
}
