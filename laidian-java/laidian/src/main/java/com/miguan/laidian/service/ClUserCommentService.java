package com.miguan.laidian.service;


import com.miguan.laidian.entity.ClUserComment;

import java.util.List;

public interface ClUserCommentService {
    int GiveUpComments(ClUserComment clUserComment);

    List<ClUserComment> findGiveUpComments(String userId, String appType);

    int delCommentsGiveUp(ClUserComment clUserComment);
}
