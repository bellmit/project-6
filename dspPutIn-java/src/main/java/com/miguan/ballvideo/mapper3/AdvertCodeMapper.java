package com.miguan.ballvideo.mapper3;


import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import com.miguan.ballvideo.vo.response.AdvertCodeSimpleRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 代码位
 * @Date 2020/11/20 16:44
 **/
public interface AdvertCodeMapper {

    List<AdvertCodeVo> findByStyleSize(@Param("styleSize") String styleSize,@Param("adType") Integer adType);

    List<AdvertCodeSimpleRes> findByCodeIds(@Param("codeIds") List<Long> codeIds);

    List<AdvertCodeSimpleRes> getCodeList(@Param("appId") Long appId, @Param("name") String name, @Param("materialShape") String materialShape,@Param("adType") Integer adType);

    List<AdvertCodeVo> findAdvCodeByGroupIds(@Param("groupIds") List<Integer> groupIds);

    List<AdvertCodeVo> findAdvCodeInfoByDesignIds(@Param("designIds") List<Long> designIds);

    List<AdvertCodeVo> findAdvCodeByPlanIds(@Param("planIds") List<String> planIds);
}
