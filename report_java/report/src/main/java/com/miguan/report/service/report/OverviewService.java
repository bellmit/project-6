package com.miguan.report.service.report;

import com.miguan.report.vo.AdStaVo;
import com.miguan.report.vo.CaStatNumVoDetail;
import com.miguan.report.vo.CostStaVo;
import com.miguan.report.vo.DisaChartVo;
import com.miguan.report.vo.PerCapitaCostVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**概览页服务接口
 * @author zhongli
 * @date 2020-06-17 
 *
 */
public interface OverviewService {

    /**
     * 加载统计概览首行数据（包括总数，环比，同比）
     * @param date
     * @param appType 1=视频 2=来电
     * @return
     */
    Map loadAllCaStat(LocalDate date, int appType);

    /**
     *
     * @param dataType 数据类型 1=总营收 2=日活 3=总CPM 4=人均展示 5=日活均值
     * @param startDate 查询此【起始日期】的数据
     * @param endDate   查询此【结束日期】的数据
     * @param appType   1=视频 2=来电
     * @return
     */
    List<CaStatNumVoDetail> loadCatStatDetail(int dataType, String startDate, String endDate, int appType);

    /**
     *
     * @param dataType 数据类型 1=CPM 2=人均展示 4=营收 5=日活均值
     * @param date 取此日期数据
     * @param appType 1=视频 2=来电
     * @return
     */
    List<AdStaVo> loadAdSta(int dataType, String date, int appType);

    List<AdStaVo> loadAdStaExt(int dataType, String date, int appType);


    /**
     * 统计所有app总成本，包含环比和同比日期的值（未计算）
     * @param appType
     * @return
     */
    CostStaVo loadCost(int appType);

    /**
     * 查询概览页-总成本的拆线图数据
     * @param startDate
     * @param endDate
     * @param appType
     * @return
     */
    List<DisaChartVo> loadCostDataChart(String startDate, String endDate, int appType);

    /**
     * 统计所有app人均成本，包含环比和同比日期的值（未计算）
     * @param appType
     * @param dataType
     * @return
     */
    CostStaVo loadPerCapitaCost(int appType, int dataType);

    /**
     * 查询概览页-人均成本的拆线图数据
     * @param dataType
     * @param startDate
     * @param endDate
     * @param appType
     * @return
     */
    List<PerCapitaCostVo> loadPerCapitaCostChart(int dataType, String startDate, String endDate, int appType);
}
