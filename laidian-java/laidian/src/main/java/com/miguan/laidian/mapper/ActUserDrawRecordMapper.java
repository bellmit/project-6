package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ActUserDrawRecord;
import com.miguan.laidian.entity.ActUserDrawRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ActUserDrawRecordMapper {
    long countByExample(ActUserDrawRecordExample example);

    int deleteByExample(ActUserDrawRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ActUserDrawRecord record);

    int insertSelective(ActUserDrawRecord record);

    List<ActUserDrawRecord> selectByExample(ActUserDrawRecordExample example);

    ActUserDrawRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ActUserDrawRecord record, @Param("example") ActUserDrawRecordExample example);

    int updateByExample(@Param("record") ActUserDrawRecord record, @Param("example") ActUserDrawRecordExample example);

    int updateByPrimaryKeySelective(ActUserDrawRecord record);

    int updateByPrimaryKey(ActUserDrawRecord record);

    /**
     * 更新用户碎片状态为已兑换
     * @param deviceId
     * @param activityConfigId
     * @param debrisReachNum
     */
    int updateUserIsEchangeState(@Param("deviceId") String deviceId, @Param("activityConfigId")Long activityConfigId, @Param("debrisReachNum")Integer debrisReachNum);
}