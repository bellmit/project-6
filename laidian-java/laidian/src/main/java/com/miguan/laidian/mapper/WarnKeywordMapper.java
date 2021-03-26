package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.WarnKeyword;

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