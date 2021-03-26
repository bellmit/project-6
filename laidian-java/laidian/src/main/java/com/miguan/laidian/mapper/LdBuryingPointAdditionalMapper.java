package com.miguan.laidian.mapper;


import com.miguan.laidian.entity.LdBuryingPointAdditional;

public interface LdBuryingPointAdditionalMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LdBuryingPointAdditional record);

    int insertSelective(LdBuryingPointAdditional record);

    LdBuryingPointAdditional selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LdBuryingPointAdditional record);

    int updateByPrimaryKey(LdBuryingPointAdditional record);

    void updateLdBuryingUserVideosByActionId(LdBuryingPointAdditional ldBuryingPointAdditional);

    LdBuryingPointAdditional findTodayBuryingPoint(LdBuryingPointAdditional ldBuryingPointAdditional);
}
