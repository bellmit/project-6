package com.miguan.ballvideo.mapper3;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.request.AdvertDesignVo;
import com.miguan.ballvideo.vo.response.AdvertDesignListRes;
import com.miguan.ballvideo.vo.response.AdvertDesignRes;
import com.miguan.ballvideo.vo.response.AdvertDesignSimpleRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 计划组mapper
 * @Author zhangbinglin
 * @Date 2020/11/20 16:44
 **/
public interface AdvertDesignMapper {

    void insert(AdvertDesignVo AdvertDesignVo);

    void update(AdvertDesignVo AdvertDesignVo);

    List<AdvertDesignVo> findAll(Map<String, Object> params);

    AdvertDesignVo getById(@Param("id") Long id);

    List<AdvertDesignVo> getByPlanId(@Param("planId") Long group_id);

    void deleteById(@Param("id") Long id);

    void updateDesignState(Map<String, Object> params);

    void updateDesignByPlanState(Map<String, Object> params);

    void deleteByPlanId(@Param("planId")Long planId);

    Page<AdvertDesignListRes> findPageList(Map<String, Object> params);

    void changeState(@Param("state") int state,@Param("id") Long id);

    AdvertDesignRes getResById(Long designId);

    List<AdvertDesignSimpleRes> getDesignList(@Param("advertUserId") Long advertUserId);
}
