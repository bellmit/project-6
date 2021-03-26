package com.miguan.reportview.service;


import com.miguan.reportview.vo.AdTotalVo;

import java.util.List;

/**
 * 数据汇总服务接口
 *
 * @author zhongli
 * @description
 * @since 2020-08-07 17:29:48
 */
public interface IBannerDataTotalNameService {


    List<AdTotalVo> getData(List<String> appPackages,
                            List<String> spaceKeys,
                            Object appAdspace,
                            List<String> plates,
                            String startDate,
                            String endDate,
                            int appType,
                            List<String> groups);


    List<AdTotalVo> getData(String startDate, String endDate, int appType);
}
