package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.CommonQuestion;
import com.miguan.laidian.vo.CommonQuestionVO;

import java.util.List;

public interface CommonQuestionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(CommonQuestion record);

    int insertSelective(CommonQuestion record);

    CommonQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommonQuestion record);

    int updateByPrimaryKey(CommonQuestion record);

    int updateCommonQuestionNumber(CommonQuestionVO commonQuestionVO);

    List<CommonQuestion> findAllCommonQuestionList(CommonQuestion commonQuestion);

    List<CommonQuestion> findAllByCommonQuestionList(CommonQuestionVO commonQuestion);
}