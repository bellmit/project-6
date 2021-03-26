package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.LdBuryingUserVideos;

public interface LdBuryingUserVideosMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LdBuryingUserVideos record);

    int insertSelective(LdBuryingUserVideos record);

    LdBuryingUserVideos selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LdBuryingUserVideos record);

    int updateByPrimaryKey(LdBuryingUserVideos record);

    LdBuryingUserVideos selectByDeviceIdAndVideoIdAndOperationType(LdBuryingUserVideos ldBuryingUserVideos);

    int selectCountByDeviceIdAndVideoIdAndOperationType(LdBuryingUserVideos ldBuryingUserVideos);
}
