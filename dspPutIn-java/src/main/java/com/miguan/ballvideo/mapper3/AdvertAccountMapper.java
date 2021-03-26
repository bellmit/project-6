package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.request.AdvertAccountVo;
import org.apache.ibatis.annotations.Param;

public interface AdvertAccountMapper {
    AdvertAccountVo findOneByPlanId(@Param("planId") Long planId);

    void update(AdvertAccountVo accountVo);
}
