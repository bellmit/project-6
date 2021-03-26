package com.miguan.laidian.mapper;

import com.miguan.laidian.vo.LockScreenCopywritingVo;

import java.util.List;
import java.util.Map;

/**
 * @author laiyd
 */
public interface LockScreenCopywritingMapper {

    /**
     *
     * @param param
     * @return
     */
    List<LockScreenCopywritingVo> queryCopywritingInfo(Map<String, Object> param);

    /**
     * 更新锁屏文案点击次数
     * @param id
     * @return
     */
    int updateClickNum(Long id);
}
