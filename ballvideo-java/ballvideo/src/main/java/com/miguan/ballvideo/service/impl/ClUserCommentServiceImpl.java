package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.ClUserComment;
import com.miguan.ballvideo.mapper.ClUserCommentMapper;
import com.miguan.ballvideo.repositories.CommentReplyDao;
import com.miguan.ballvideo.service.ClUserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 点赞ClUserCommentServiceImpl
 * @author HYL
 * @date 2019年9月11日10:22:45
 **/
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
    public List<ClUserComment> findGiveUpComments(String userId) {
        return clUserCommentMapper.findGiveUpComments(userId);
    }

    @Override
    @Transactional
    public int delCommentsGiveUp(ClUserComment clUserComment) {
        int i = clUserCommentMapper.deleteByUserIdAndCommentId(clUserComment);
        commentReplyDao.deleteGiveUpComment(clUserComment.getCommentId());
        return i;
    }
}
