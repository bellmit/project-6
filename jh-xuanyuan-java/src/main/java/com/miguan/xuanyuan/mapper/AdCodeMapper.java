package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.xuanyuan.dto.AdCodeDto;
import com.miguan.xuanyuan.dto.AdDataDto;
import com.miguan.xuanyuan.vo.sdk.AdProfitVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 广告代码位mapper
 **/
public interface AdCodeMapper {

    /**
     * 查询广告代码位列表
     * @param params
     * @return
     */
    List<AdCodeDto> adCodeInfoList(Map<String, Object> params);

    String findPositionRule(Map<String, Object> param);

    AdDataDto findPositionInfo(Map<String, Object> param);

    /**
     * 查询当前在使用的第三方广告商数
     * @return
     */
    Integer countUsedPlat();

    /**
     * 查询最近一天的第三方代码位统计数据的日期
     * @return
     */
    @DS("report")
    String queryNearDate(@Param("platNum") Integer platNum);

    /**
     * 查询最近一天第三方代码位收益
     * @param date
     * @return
     */
    @DS("report")
    List<AdProfitVo> queryAdProfit(@Param("date") String date);

    /**
     * 查询最近一天98投代码位收益
     * @param date
     * @return
     */
//    @DS("dsp")
//    List<AdProfitVo> queryAd98Profit(@Param("date") String date);

    /**
     * 查询多维度最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
     * @param params
     * @return
     */

    @DS("xy_report")
    List<AdProfitVo> queryAdMultiData(Map<String, Object> params);

    /**
     * 查询最近一天代码位的曝光数，点击数，有效曝光数，有效点击数
     * @param date 日期,yyyy-MM-dd
     * @param appPackage 包名
     * @return
     */
    @DS("xy_report")
    List<AdProfitVo> queryAdData(@Param("date") String date, @Param("appPackage") String appPackage);
}

