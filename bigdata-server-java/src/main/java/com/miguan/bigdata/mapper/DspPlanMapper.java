package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.vo.DspPlanVo;
import com.miguan.bigdata.vo.UserRatioVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 获取广告行为数据mapper
 */
public interface DspPlanMapper {

    /**
     * 批量同步数据到idea_advert_report
     * @param list
     */
    @DS("dsp")
    void batchReplaceAdActionDay(@Param("list") List<DspPlanVo> list);

    /**
     * 获取近7天每个时间段（30分钟一个时间段）的日活用户数
     * @param params
     */
    List<UserRatioVo> getUserRatio(Map<String, Object> params);

    Integer countTimeSlotActiveUse(Map<String, Object> params);

    void deleteTimeSlotActiveUser(Map<String, Object> params);

    void insertTimeSlotActiveUser(Map<String, Object> params);
}
