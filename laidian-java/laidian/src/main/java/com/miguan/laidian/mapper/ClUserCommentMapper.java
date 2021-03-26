package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.ClUserComment;

import java.util.List;

public interface ClUserCommentMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ClUserComment record);

    int insertSelective(ClUserComment record);

    ClUserComment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ClUserComment record);

    int updateByPrimaryKey(ClUserComment record);

    List<ClUserComment> findGiveUpComments(String userId, String appType);

    int deleteByUserIdAndCommentId(ClUserComment clUserComment);
}