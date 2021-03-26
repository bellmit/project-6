package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import com.miguan.ballvideo.vo.response.AdvertCodeSimpleRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码位
 */
public interface AdvertCodeService {
    List<AdvertCodeVo> findByStyleSize(String styleSize, Integer adType);

    List<AdvertCodeSimpleRes> findByCodeIds(List<Long> codeIds);

    List<AdvertCodeSimpleRes> getCodeList(Long appId, String name, Integer materialShape, Integer adType);

    List<AdvertCodeVo> findAdvCodeByGroupIds(List<Integer> groupIds);

    List<AdvertCodeVo> findAdvCodeInfoByDesignIds(List<Long> designIds);

    List<AdvertCodeVo> findAdvCodeByPlanIds(List<String> planIds);
}
