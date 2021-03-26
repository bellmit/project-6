package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ActActivityConfig;
import com.miguan.laidian.entity.ActActivityConfigExample;
import java.util.List;

import com.miguan.laidian.vo.ActivityConfigVo;
import org.apache.ibatis.annotations.Param;

public interface ActActivityConfigMapper {
    long countByExample(ActActivityConfigExample example);

    int deleteByExample(ActActivityConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActActivityConfig record);

    int insertSelective(ActActivityConfig record);

    List<ActActivityConfig> selectByExample(ActActivityConfigExample example);

    ActActivityConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActActivityConfig record, @Param("example") ActActivityConfigExample example);

    int updateByExample(@Param("record") ActActivityConfig record, @Param("example") ActActivityConfigExample example);

    int updateByPrimaryKeySelective(ActActivityConfig record);

    int updateByPrimaryKey(ActActivityConfig record);

    /**
     * 获取用户碎片信息
     * @param activityId
     * @param deviceId
     * @return
     */
    List<ActivityConfigVo> getUserActConfigById(@Param("activityId") Long activityId, @Param("deviceId") String deviceId);
}