package com.miguan.report.mapper;

import com.miguan.report.vo.AdStaVo;
import com.miguan.report.vo.CaStatNumVoDetail;
import com.miguan.report.vo.CostStaVo;
import com.miguan.report.vo.DisaChartVo;
import com.miguan.report.vo.PerCapitaCostVo;

import java.util.List;
import java.util.Map;

/**概览数据集Mapper
 * @author zhongli
 * @date 2020-06-17 
 *
 */
public interface OverviewMapper {


    /**
     * 查询所有应用的的日活量
     * @param params
     * @return
     */
    Long queryActiveForAllApp(Map<String, Object> params);
    /**
     * 统计概览首行数据（包括总数，环比，同比）
     * @param params
     * @return
     */
    Map queryAllCaStat(Map<String, Object> params);

    /**
     * 查询首行数据（包括总数，环比，同比）的详情统计
     * @param params
     * @return
     */
    List<CaStatNumVoDetail> queryCatStatDetail(Map<String, Object> params);

    /**
     * 查询首行数据（包括总数，环比，同比）的详情统计 扩展实现，查询用户日使用时长
     * @param params
     * @return
     */
    List<CaStatNumVoDetail> queryUserUsTimeStaDetail(Map<String, Object> params);


    /**
     * 广告位对统计
     * @param params
     * @return
     */
    List<AdStaVo> queryAdSta(Map<String, Object> params);

    /**
     * 广告位对统计-扩展查询（错误率）
     * @param params
     * @return
     */
    List<AdStaVo> queryAdStaExt(Map<String, Object> params);

    /**
     * 统计用户日使用时长
     * @param params
     * @return
     */
    Map queryUserUsTimeSta(Map<String, Object> params);

    /**
     * 统计最新日期的所有app总成本
     * @return
     */
    CostStaVo queryLastDatelCost(Map<String, Object> params);

    /**
     * 查询总成本环比和同比数据
     * @param params
     * @return
     */
    CostStaVo queryMonAndYoyCost(Map<String, Object> params);

    /**
     * 查询总成本拆线图数据
     * @param params
     * @return
     */
    List<DisaChartVo> queryChartCost(Map<String, Object> params);

    /**
     * 统计所有app人均成本最新日期
     * @param params
     * @return
     */
    PerCapitaCostVo queryLastDateForPerCost(Map<String, Object> params);

    /**
     * 查询人均成本环比和同比数据
     * @param params
     * @return
     */
    List<PerCapitaCostVo> queryMomAndYoyPerCost(Map<String, Object> params);

    /**
     * 查询人均成本拆线图数据
     * @param params
     * @return
     */
    List<PerCapitaCostVo> queryChartPerCost(Map<String, Object> params);
}
