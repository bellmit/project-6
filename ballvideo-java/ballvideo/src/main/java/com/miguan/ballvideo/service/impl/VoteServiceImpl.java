package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.ClUserComment;
import com.miguan.ballvideo.repositories.ClUserCommentDao;
import com.miguan.ballvideo.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 点赞/取消点赞接口VoteServiceImpl
 * @author HYL
 * @date 2019年9月11日10:22:45
 **/
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
