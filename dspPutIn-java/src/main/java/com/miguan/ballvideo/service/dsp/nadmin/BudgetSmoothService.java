package com.miguan.ballvideo.service.dsp.nadmin;

/**
 * 预算平滑service
 */
public interface BudgetSmoothService {

    /**
     * 预算平滑算法、参竞率（根据每个时间段的日活数，计算出每个时间段的预算）
     */
    void budgetSmooth();

    /**
     * 当前时间段的预算，是否还有剩余
     * @param planId 计划id
     * @param price 单价
     * @return true:还有剩余,false：已无预算
     */
    boolean isHasBudget(Integer planId, double price);

    /**
     * 广告点击后，减少对应时间段的预算值
     * @param planId 计划id
     * @param price 单价
     */
    void reduceTimeSlotPrice(Integer planId, double price);

    /**
     * 修改计划组剩余预算
     * @param remainGroupPrice  组剩余预算
     * @param planId 计划id
     */
    void updateGroupRemainAccount(Double remainGroupPrice, Integer planId);

    /**
     * 判断计划是否命中参竞率
     * @param planId 计划id
     * @return 返回格式：命中结果-参竞率，例如 true-0.15664
     */
    String isHitPartRate(Integer planId);

    /**
     * 98度昨天有效点击率
     * @return
     */
    double staYesPreClickRate();
}
