package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.ClUserComment;
import com.miguan.laidian.mapper.ClUserCommentMapper;
import com.miguan.laidian.repositories.CommentReplyDao;
import com.miguan.laidian.service.ClUserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ClUserCommentServiceImpl implements ClUserCommentService {

    @Resource
    ClUserCommentMapper clUserCommentMapper;

    @Autowired
    CommentReplyDao commentReplyDao;

    @Override
    @Transactional
    public int GiveUpComments(ClUserComment clUserComment) {
        int i = clUserCommentMapper.insertSelective(clUserComment);
        commentReplyDao.updateGiveUpComment(clUserComment.getCommentId());
        return i;
    }

    @Override
    public List<ClUserComment> findGiveUpComments(String userId, String appType) {
        return clUserCommentMapper.findGiveUpComments(userId, appType);
    }

    @Override
    @Transactional
    public int delCommentsGiveUp(ClUserComment clUserComment) {
        int i = clUserCommentMapper.deleteByUserIdAndCommentId(clUserComment);
        commentReplyDao.deleteGiveUpComment(clUserComment.getCommentId());
        return i;
    }
}
