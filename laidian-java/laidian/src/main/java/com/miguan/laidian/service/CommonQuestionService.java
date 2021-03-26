package com.miguan.laidian.service;

import com.miguan.laidian.entity.CommonQuestion;
import com.miguan.laidian.vo.CommonQuestionVO;

import java.util.List;

public interface CommonQuestionService {

    List<CommonQuestion> findAllCommonQuestion(CommonQuestion commonQuestion, int currentPage, int pageSize);

    int updateCommonQuestionNumber(CommonQuestionVO commonQuestionVO);
}
