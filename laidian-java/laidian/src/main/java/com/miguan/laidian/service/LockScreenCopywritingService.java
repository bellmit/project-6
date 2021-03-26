package com.miguan.laidian.service;

import com.miguan.laidian.vo.LockScreenCopywritingVo;

import java.util.Map;

/**
 * Created by shixh on 2019/12/11.
 */
public interface LockScreenCopywritingService {

    public LockScreenCopywritingVo getCopywritingInfo(Map<String, Object> params);

    /**
     * 根据ID更新锁屏文案点击次数
     *
     * @param id
     * @return
     */
    public boolean updateCopywritingClickNum(Long id);
}
