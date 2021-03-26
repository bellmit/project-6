package com.miguan.laidian.service;

import com.github.pagehelper.Page;
import com.miguan.laidian.entity.CommentReply;
import com.miguan.laidian.entity.CommentReplyRequest;
import com.miguan.laidian.entity.CommentReplyResponse;


import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CommentReplyService {
    public CommentReply addNewCommentReply(CommentReplyRequest commentReply) throws UnsupportedEncodingException;

    Page<CommentReplyResponse> findAllCommentReply(CommentReplyRequest commentReply, int currentPage, int pageSize, int type, String userId);

    List<CommentReplyResponse> findCommentReplyByCommentId(String commentId, String userId, String appType);

    List<CommentReplyResponse> findAllCommentReplyByCommentId(String commentId, String userId, String appType);

    int findCommentsReplyNumber(String userId, String appType);

    int findUserOpinionSubmitNumber(String deviceId);
}
