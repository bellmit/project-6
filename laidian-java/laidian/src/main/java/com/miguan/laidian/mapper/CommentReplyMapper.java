package com.miguan.laidian.mapper;

import com.miguan.laidian.entity.CommentReply;
import com.miguan.laidian.entity.CommentReplyRequest;
import com.miguan.laidian.entity.CommentReplyResponse;

import java.util.List;

public interface CommentReplyMapper {

    int deleteByPrimaryKey(Long id);

    int insert(CommentReply record);

    int insertSelective(CommentReply record);

    CommentReply selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CommentReply record);

    int updateByPrimaryKey(CommentReply record);

    List<CommentReplyResponse> findAllCommentReplyTow(CommentReplyRequest record);

    List<CommentReplyResponse> findAllCommentReply(CommentReplyRequest record);

    List<CommentReplyResponse> findMessageCenter(CommentReplyRequest commentReply);

    int updateCommentReplyByPComment(Long toFromUid, String appType);

    int updateCommentReplyByToFromUserIdAndName(CommentReply commentReply);

    int findAllCommentReplyByAlreadyRead(String userId, String appType);

    List<CommentReplyResponse> findAllCommentReplyByGiveUp(CommentReplyRequest commentReply);

    List<CommentReplyResponse> findAllCommentReplyByNoGiveUpNumber(CommentReplyRequest commentReply);
}