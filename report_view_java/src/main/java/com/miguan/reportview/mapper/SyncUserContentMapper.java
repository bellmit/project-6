package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.vo.SyncUserContentDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 同步用户内容运营数据表
 *
 */
public interface SyncUserContentMapper {

    /**
     * 批量保存user_content_operation的数据到mysql中
     * @param lists
     */
    void batchSaveUserContent(@Param("lists") List<SyncUserContentDataVo> lists);

    /**
     * 删除时间范围内的数据
     * @param startDate 开始时间,如：2020-08-01
     * @param endDate  结束时间,如：2020-08-19
     */
    void deleteUserContent(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 统计事件相关数据
     * @param startDate 开始时间,如：2020-08-01
     * @param endDate  结束时间,如：2020-08-19
     * @return
     */
    @DS("clickhouse")
    List<SyncUserContentDataVo> statActionData(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("limitSql") String limitSql);

    @DS("clickhouse")
    int countActionData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("clickhouse")
    int deleteActionData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("clickhouse")
    int updateActionData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("clickhouse")
    void batchSaveActionData(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @DS("clickhouse")
    int deleteLdActionData(@Param("date") String date);

    @DS("clickhouse")
    int updateLdActionData(@Param("date") String date);

    @DS("clickhouse")
    void batchSaveLdActionData(@Param("date") String date);
}
