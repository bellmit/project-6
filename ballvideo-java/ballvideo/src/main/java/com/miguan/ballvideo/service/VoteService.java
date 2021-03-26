package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.ClUserComment;

public interface VoteService {

    /** 
    * @Description: 保存用户评论点赞
    * @Param:  clUserComment
    * @return:  ClUserComment
    * @Author: hyl
    * @Date:  2019年9月11日10:23:49
    */ 
    ClUserComment addVoteUserComment(ClUserComment clUserComment);


    /**
     * @Description: 删除用户评论点赞
     * @Param:  clUserComment
     * @return:  ClUserComment
     * @Author: hyl
     * @Date: 2019年9月11日10:23:51
     */
    int deleteVoteUserComment(ClUserComment clUserComment);
}
