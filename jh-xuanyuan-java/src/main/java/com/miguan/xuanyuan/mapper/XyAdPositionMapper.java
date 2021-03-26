package com.miguan.xuanyuan.mapper;

import com.github.pagehelper.Page;
import com.miguan.xuanyuan.dto.AdPositionDetailDto;
import com.miguan.xuanyuan.entity.XyAdPosition;
import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.vo.XyAdPositionSimpleVo;
import com.miguan.xuanyuan.vo.XyAdPositionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author kangkunhuang
 * @Description 代码位
 * @Date 2021/1/21
 **/
public interface XyAdPositionMapper extends BaseMapper<XyAdPosition> {
    Page<XyAdPositionVo> findPageList(Map params);
    int deleteById(@Param("id") Long id);

    List<XyAdPositionSimpleVo> findList(@Param("plat")int plat, @Param("userId") Long userId, @Param("appId") Long appId);

    void deleteByAppId(Long appId);

    List<XyAdPosition> findByAppId(Long appId);

    void updateStatusByAppId(@Param("appId")  Long appId,@Param("status") Integer status);

    int judgeExistName(@Param("positionName")String positionName,@Param("appId") Long appId,@Param("id") Long id);

    AdPositionDetailDto getPositionDetail(Long positionId);

    int judgePositionKey(String positionKey);
}
