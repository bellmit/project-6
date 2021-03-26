package com.miguan.ballvideo.mapper3;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.request.AdvertPlanVo;
import com.miguan.ballvideo.vo.response.AdvertPlanListExt;
import com.miguan.ballvideo.vo.response.AdvertPlanListRes;
import com.miguan.ballvideo.vo.response.AdvertPlanRes;
import com.miguan.ballvideo.vo.response.AdvertPlanSimpleRes;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description 计划
 **/
public interface AdvertPlanMapper {

    void insert(AdvertPlanVo advertPlanVo);

    void update(AdvertPlanVo advertPlanVo);

    List<AdvertPlanVo> findAll(Map<String, Object> params);

    Page<AdvertPlanListRes> findPageList(Map<String, Object> params);

    Page<AdvertPlanListExt> findExtPageList(Map<String, Object> params);

    AdvertPlanVo getById(@Param("id") Long id);

    List<AdvertPlanVo> getByGroupId(@Param("groupId") Long group_id);

    void deleteById(@Param("id") Long id);

    void updatePlanState(Map<String, Object> params);

    void updateSmoothDate(@Param("smoothDate") Date smoothDate, @Param("id") Long id);

    List<AdvertPlanSimpleRes> getPlanList(@Param("advertUserId") Long advertUserId, @Param("groupId") Long groupId);

    AdvertPlanRes findResById(Long planId);

    void updateMaterial(@Param("planId") Long planId,@Param("materialType") Integer materialType,@Param("materialShape") Integer materialShape);

    void updateAdvType(@Param("id") Long id, @Param("advertType") String advertType);
}
