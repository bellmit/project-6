package com.miguan.ballvideo.mapper;


import com.miguan.ballvideo.entity.WarnKeyword;

import java.util.Set;

public interface WarnKeywordMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(WarnKeyword record);

    int insertSelective(WarnKeyword record);

    WarnKeyword selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WarnKeyword record);

    int updateByPrimaryKey(WarnKeyword record);

    Set<String> findAllWarnKey();

}