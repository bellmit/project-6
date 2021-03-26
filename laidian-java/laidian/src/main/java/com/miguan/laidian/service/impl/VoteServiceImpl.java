package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.ClUserComment;
import com.miguan.laidian.repositories.ClUserCommentDao;
import com.miguan.laidian.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    ClUserCommentDao clUserCommentDao;

    @Override
    public ClUserComment addVoteUserComment(ClUserComment clUserComment) {
        ClUserComment clUserComment1 = clUserCommentDao.saveAndFlush(clUserComment);
        return clUserComment1;
    }

    @Override
    public int deleteVoteUserComment(ClUserComment clUserComment) {
        return clUserCommentDao.deleteByCommentIdandUserId(clUserComment.getUserId(),clUserComment.getCommentId());
    }
}
