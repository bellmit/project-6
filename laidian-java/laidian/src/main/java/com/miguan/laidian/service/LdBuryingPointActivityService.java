package com.miguan.laidian.service;


import com.miguan.laidian.vo.LdBuryingPointActivityVo;

/**
 * @author chenwf
 * @date 2020/5/27
 */
public interface LdBuryingPointActivityService {
    /**
     * 活动埋点写入
     * @param ldBuryingPointActivityVo
     */
    void insert(LdBuryingPointActivityVo ldBuryingPointActivityVo);
}
