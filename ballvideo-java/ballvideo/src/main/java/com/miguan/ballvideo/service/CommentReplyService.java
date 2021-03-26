package com.miguan.ballvideo.service;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.entity.CommentReply;
import com.miguan.ballvideo.entity.CommentReplyRequest;
import com.miguan.ballvideo.entity.CommentReplyResponse;
import com.miguan.ballvideo.vo.CommentReplyRequestVo;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CommentReplyService {
    public CommentReply addNewCommentReply(CommentReplyRequest commentReply) throws UnsupportedEncodingException;

    Page<CommentReplyResponse> findAllCommentReply(CommentReplyRequestVo commentReplyRequestVo);

    List<CommentReplyResponse> findCommentReplyByCommentId(String commentId,String userId);

    List<CommentReplyResponse> findAllCommentReplyByCommentId(String commentId,String userId);

    int findCommentsReplyNumber(String userId);

    int findUserOpinionSubmitNumber(String userId);

    int updateVideosInitInfo(String ids, String videoType);

    void updateCommentCount();

    void updateReplyCount(Integer type);
}
