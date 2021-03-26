package com.miguan.ballvideo.mapper3;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertGroupListVo;
import com.miguan.ballvideo.vo.AdvertGroupVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 计划组mapper
 * @Author zhangbinglin
 * @Date 2020/11/20 16:44
 **/
public interface AdvertGroupMapper {

    Page<AdvertGroupListVo> findAdvertGroupList(Map<String, Object> params);

    AdvertGroupVo getAdvertGroupById(@Param("id") int id);

    int insertAdvertGroup(AdvertGroupVo advertGroupVo);

    void updateAdvertGroup(AdvertGroupVo advertGroupVo);

    void deleteGroup(@Param("id") Integer id);

    void deletePlanByGroupId(@Param("id") Integer id);

    void deleteDesignByGroupId(@Param("id") Integer id);

    void deleteDesWeightByGroupId(@Param("id") Integer id);

    void updateGroupState(Map<String, Object> params);

    void updatePlanState(Map<String, Object> params);

    void updateDesignState(Map<String, Object> params);

    List<AdvertGroupVo> getGroupList(@Param("advertUserId") Long advertUserId);
}
