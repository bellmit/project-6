package com.miguan.laidian.service;

import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.entity.ActUserPrizeExchangeRecord;
import com.miguan.laidian.vo.ActExchangeRecordVo;

import java.util.List;
import java.util.Map;

/**
 * @author chenwf
 * @date 2020/5/22
 */
public interface ActUserPrizeExchangeRecordService {
    /**
     * 获取用户兑奖记录
     * @param deviceId
     * @param activityId
     * @return
     */
    List<ActUserPrizeExchangeRecord> getUserExchangeRecord(String deviceId, Long activityId);


    /**
     * 添加兑换记录
     * @param activityId
     * @param activityConfigId
     * @param contastInfo
     * @param commomParams
     * @param rcvrName
     * @param rcvrAddr
     * @param rcvrPhone
     * @return
     */
    int addPrize(Long activityId, Long activityConfigId, String contastInfo, CommonParamsVo commomParams,String rcvrName, String rcvrAddr, String rcvrPhone);

    /**
     * 查询兑奖记录
     *
     * @param params
     * @return
     */
    List<ActExchangeRecordVo> queryExchangeRecord(Map<String,Object> params);
}
