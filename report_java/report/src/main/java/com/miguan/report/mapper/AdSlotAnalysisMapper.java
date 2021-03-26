package com.miguan.report.mapper;

import com.miguan.report.vo.AdSlotAnlyDetailVo;
import com.miguan.report.vo.AdSlotAnlyVo;

import java.util.List;
import java.util.Map;

/**广告位分析数据集Mapper
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public interface AdSlotAnalysisMapper {

    /**
     * 按app_name,ad_space进行分组统计数据
     * @param params
     * @return
     */
    List<AdSlotAnlyVo> queryGroupByAppNameAndAdspace(Map<String, Object> params);

    /**
     * 广告位点击详情数据
     * @param params
     * @return
     */
    List<AdSlotAnlyDetailVo> queryAdSlotDetail(Map<String, Object> params);

    /**
     * 统计用户行为数据
     * @param params
     * @return
     */
    List<AdSlotAnlyDetailVo> queryAdUserBehaviorDetail(Map<String, Object> params);

}
