package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.RpTotalHour;
import com.miguan.reportview.vo.LdRealTimeStaVo;
import com.miguan.reportview.vo.RealTimeStaVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 全局汇总表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface RpTotalHourMapper extends BaseMapper<RpTotalHour> {

    List<RealTimeStaVo> getData(Map<String, Object> param);

    List<RealTimeStaVo> getMinuteData(Map<String, Object> param);

    @DS("clickhouse")
    void deleteHourAccumulated(@Param("dh")int dh);

    @DS("clickhouse")
    void staData(Map<String, Object> param);

    @DS("clickhouse")
    void staLdData(@Param("startDh")int startDh, @Param("endDh")int endDh, @Param("showDh")int showDh);

    @DS("clickhouse")
    void deleteLdHourAccumulated(@Param("dh")int dh);

    List<LdRealTimeStaVo> getLdData(Map<String, Object> param);

    List<LdRealTimeStaVo> getLdMinuteData(Map<String, Object> param);
}
