package com.miguan.report.mapper;

import com.github.pagehelper.Page;
import com.miguan.report.dto.BannerQuotaDto;
import com.miguan.report.dto.LineChartDto;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 数据明细mapper
 * @Author zhangbinglin
 * @Date 2020/6/17 11:54
 **/
public interface DataDetailMapper {


    /**
     * 获取数据明细中，用户自定义的字段列表
     *
     * @param token  token
     * @param appType app类型：1=西柚视频,2=炫来电
     * @return
     */
    List<BannerQuotaDto> getHeadList(@Param("token") String token, @Param("appType") Integer appType);

    /**
     * 获取数据明细列表
     * @return
     */
    Page<LinkedHashMap<String, Object>> findDataDetailList(Map<String, Object> params);

    /**
     * 动态生成数据明细折线图按天、周、月折线图
     * @param params
     * @return
     */
    List<LineChartDto> countLineDataDetailChart(Map<String, Object> params);

    /**
     * 按小时数据明细折线图
     * @return
     */
    List<LineChartDto> countLineHourChart(Map<String, Object> params);

    /**
     * 删除用户自定义列
     * @param token
     * @param appType
     */
    void deleteUserFields(@Param("token") String token, @Param("appType") Integer appType);

    /**
     * 保存用户自定义列
     * @param params
     */
    void saveUserFields(Map<String, Object> params);

}
