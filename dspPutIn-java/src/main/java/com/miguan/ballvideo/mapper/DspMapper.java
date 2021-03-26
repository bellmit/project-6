package com.miguan.ballvideo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * DspMapper
 */
public interface DspMapper {

    /**
     * 插入账户余额表
     * @param planId
     * @param remainDayPrice
     * @param remainTotalPrice
     */
    @Insert("insert into idea_advert_account(plan_id, remain_day_price, remain_total_price, created_at) " +
            "VALUES (#{planId},#{remainDayPrice},#{remainTotalPrice},NOW())")
    public void insertAccount(@Param("planId") String planId, @Param("remainDayPrice") Double remainDayPrice, @Param("remainTotalPrice") Double remainTotalPrice);


    /**
     * 更新账户余额表
     * @param remainDayPrice
     * @param remainTotalPrice
     * @param planId
     */
    @Update("update idea_advert_account set remain_day_price = #{remainDayPrice}, remain_total_price = #{remainTotalPrice}, updated_at = NOW() " +
            "where plan_id = #{planId}")
    void updateAccount(@Param("remainDayPrice") Double remainDayPrice, @Param("remainTotalPrice") Double remainTotalPrice, @Param("planId") String planId);

    /**
     * 更新广告计划表状态
     * @param state
     * @param id
     */
    @Update("update idea_advert_plan set state = #{state}, updated_at = NOW() where id = #{id}")
    void updatePlanState(@Param("state") int state, @Param("id") String id);

    /**
     * 更新广告计划表平滑时间
     * @param id
     */
    @Update("update idea_advert_plan set smooth_date = NOW() where id = #{id}")
    void updatePlanSmoothDate( @Param("id") String id);
}
